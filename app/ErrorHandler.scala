import java.util.UUID
import javax.inject.{Inject, Provider}

import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent.Future

class ErrorHandler @Inject()(env: Environment,
                             conf: Configuration,
                             mapper: OptionalSourceMapper,
                             router: Provider[Router])
  extends DefaultHttpErrorHandler(env, conf, mapper, router) {
  override protected def onBadRequest(request: RequestHeader, message: String) =
    Future.successful(
      BadRequest(Json.obj(
          "error" -> BAD_REQUEST,
          "message" -> s"Invalid request to ${request.uri}")))

  override protected def onNotFound(request: RequestHeader, message: String) =
    Future.successful(
      BadRequest(Json.obj(
        "error" -> NOT_FOUND,
        "message" -> s"Uri ${request.uri} not found")))


  override protected def onForbidden(request: RequestHeader, message: String) = {
    Logger.info(s"Forbidden entry to ${request.uri}: $message")
    Future.successful(Forbidden)
  }

  override protected def onOtherClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Logger.info(s"Other client errors $statusCode: $message")
    Future.successful(Status(statusCode)(Json.obj("error" -> statusCode)))
  }

  override protected def onProdServerError(request: RequestHeader, e: UsefulException) = {
    val uuid = UUID.randomUUID().toString

    Logger.warn(s"$uuid: Error on ${request.uri}", e)
    Future.successful(InternalServerError(Json.obj(
      "id" -> uuid,
      "message" -> "Unexpected problem on server"
    )))
  }


}
