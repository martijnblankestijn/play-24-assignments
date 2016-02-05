# DO NOT forget the LINE WITH !Ups AS your script will NOT be applied
# --- !Ups

create table "person"
( "first_name" VARCHAR NOT NULL
, "postal_code" VARCHAR NOT NULL
, "house_no" INTEGER NOT NULL
, "id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY);

# --- !Downs

drop table person;