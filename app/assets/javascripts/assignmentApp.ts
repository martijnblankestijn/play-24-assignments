/// <reference path='_all.ts' />

module assignment {
    'use strict';

    export class ResourceErrorHandler {
        constructor(private $q:ng.IQService, private flash) {
        }

        public responseError = (rejection) => {
            var message = "Error : status=" + rejection.status + ", rejection=" + JSON.stringify(rejection);
            console.log(message)
            this.flash.error = message
            return this.$q.reject(rejection)
        }

        //static $inject = ["$q", 'flash'];
        public static Create($q:ng.IQService, flash) {
            return new ResourceErrorHandler($q, flash)
        }
    }

    export function flashProviderConfig(flashProvider) {
        // Support bootstrap 3.0 "alert-danger" class with error flash types
        flashProvider.errorClassnames.push('alert-danger');
    }

    export function httpProviderConfig($httpProvider: ng.IHttpProvider, $q:ng.IQService, flash) {
        $httpProvider.interceptors.push(ResourceErrorHandler.Create)
    }


    export class RouteProvider {
        static $inject = ["$routeProvider"];

        constructor($routeProvider:ng.route.IRouteProvider) {
            console.log("Initializing router");
            $routeProvider
                .when("/",
                    {
                        redirectTo: "/list"
                    })
                .when("/list",
                    {
                        templateUrl: "assets/partials/list.html",
                        controller: "listController",
                        controllerAs: "vm"
                    })
                .when("/new",
                    {
                        templateUrl: "assets/partials/edit.html",
                        controller: "editPersonController",
                        controllerAs: "vm"
                    })
                .when("/edit/:id",
                    {
                        templateUrl: "assets/partials/edit.html",
                        controller: "editPersonController",
                        controllerAs: "vm"
                    })
                .when("/details/:id",
                    {
                        templateUrl: "assets/partials/details.html",
                        controller: "personDetailsController",
                        controllerAs: "vm"
                    })
                .when("/addresscheck",
                    {
                        templateUrl: "assets/partials/addresscheck.html",
                        controller: "addressCheckController",
                        controllerAs: "vm"
                    })
                .when("/paginated",
                    {
                        templateUrl: "assets/partials/paginated.html",
                        controller: "paginatedListController",
                        controllerAs: "vm"
                    })
        }
    }


    export interface IPersonResource extends ng.resource.IResourceClass<IPerson> {
        update(data:Object, success:Function, error?:Function): IPerson;
    }

    export interface Address {
        streetName: string;
        houseNumber: number;
    }

    export interface IPerson extends ng.resource.IResource<IPerson> {
        firstName: string;
        postalCode: string;
        houseNumber: string;
        id: number;
    }

    export interface PersonAddressView {
        address: Address;
        person: IPerson;
    }

    export class PersonResource {
        public static Client($resource:ng.resource.IResourceService):IPersonResource {
            return <IPersonResource>
                $resource("/api/persons/:id",
                    {},
                    {update: {method: 'PUT', params: {id: '@id'}}}
                );
        }
    }

    export class ListController {
        title:string = "List of persons";
        persons:IPerson[];

        static $inject = ["personResource"];

        constructor(private resource:IPersonResource) {
            this.persons = resource.query()
        }

        delete(id:number) {
            console.log("Deleting person " + id);
            this.resource.delete(
                {id: id},
                () => this.persons = this.resource.query());
        }
    }

    export interface PersonIdentificationParam extends ng.route.IRouteParamsService {
        id:number;
    }

    export class EditPersonController {
        title:string;
        person:IPerson;
        isEdit:boolean = false;
        static $inject = ["personResource", "$routeParams", "$location"];

        constructor(private resource:IPersonResource, $routeParams:PersonIdentificationParam, private $location:ng.ILocationService) {
            if ($routeParams.id === undefined) {
                console.log("Create a new Person");
            } else {
                console.log("Edit an existing person");
                this.person = this.resource.get({id: $routeParams.id})
                this.isEdit = true;
            }

        }

        submit() {
            var errorHandling = (r:ng.IHttpPromiseCallbackArg<string>) =>
                console.log("Got error. Status: " + r.status + ", text: " + r.statusText +
                    ", response: " + JSON.stringify(r.data));

            console.log("Saving person ....");

            if (this.isEdit) {
                this.resource.update(
                    this.person,
                    () => {
                        console.log('The person with id ' + this.person.id + " has been updated.");
                        this.$location.path("/list")
                    },
                    errorHandling
                )
            }
            else {
                // server expects an id for marshalling, this will be replaced on the server.
                this.person.id = 0;
                this.resource.save(
                    this.person,
                    (p:IPerson) => {
                        console.log('The person has been saved with id ' + p.id);
                        this.$location.path("/list")
                    },
                    errorHandling
                )
            }
        }
    }
    export class PersonDetailsController {
        person:IPerson;
        address;

        static $inject = ["$http", "$routeParams"];

        constructor(private $http:ng.IHttpService, $routeParams:PersonIdentificationParam) {
            console.log("Retrieving details of " + $routeParams.id)
            var url = "/api/persons/" + $routeParams.id + "/details";
            $http.get(url)
                .success((view:PersonAddressView) => {
                    this.person = view.person;
                    this.address = view.address;
                });
        }

        addressFound() {
            return this.address != undefined;
        }
    }

    export class AddressCheckController {
        postalCode: string;
        houseNumber: number;
        address: Address;

        static $inject = ["$http"];

        constructor(private $http:ng.IHttpService) {
            console.log("Address check initializing")
        }

        check() {
            this.address = null;
            this.$http.get("/api/addresscheck",
                { params: { postalCode: this.postalCode, houseNumber: this.houseNumber}})
                .success( (address: Address) => this.address = address)
        }

        addressFound() {
            return this.address != undefined;
        }
    }


    export class PaginatedListController {
        persons:IPerson[];
        reloadPage:boolean = true;
    }


    angular.module('assignmentApp',
        ['ngResource', 'ngRoute', '' +
        'bgf.paginateAnything',
            'angular-flash.service', 'angular-flash.flash-alert-directive'
        ]);

    angular.module('assignmentApp')
        .config(['flashProvider', flashProviderConfig])
        .config(['$httpProvider', httpProviderConfig])
        .factory('personResource', ['$resource', PersonResource.Client])
        .controller("paginatedListController", PaginatedListController)
        .controller("personDetailsController", PersonDetailsController)
        .controller("listController", ListController)
        .controller("editPersonController", EditPersonController)
        .controller("addressCheckController", AddressCheckController)
        .config(RouteProvider);

}