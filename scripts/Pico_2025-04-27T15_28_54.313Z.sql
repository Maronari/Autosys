
CREATE TABLE "sensors" (
	"sensor_id" serial NOT NULL UNIQUE,
	"sensor_opcua_endpoint" varchar(255),
	"sensor_status" integer,
	PRIMARY KEY("sensor_id")
);


CREATE TABLE "alarm_type" (
	"type_id" serial NOT NULL UNIQUE,
	"type_description" varchar(255),
	PRIMARY KEY("type_id")
);


CREATE TABLE "sensor_params" (
	"param_id" serial NOT NULL UNIQUE,
	"sensor_id" integer,
	"node_id" varchar(255),
	"param_name" varchar(255),
	"param_min_value" integer,
	"param_max_value" integer,
	PRIMARY KEY("param_id")
);


CREATE TABLE "sensor_trends" (
	"trend_id" integer NOT NULL UNIQUE,
	"sensor_id" int,
	"node_value" float,
	"timestamp" TIMESTAMPTZ,
	PRIMARY KEY("trend_id")
);


CREATE TABLE "user_role" (
	"role_id" integer NOT NULL UNIQUE,
	"name" varchar(255),
	"description" varchar(255),
	"privilege" boolean,
	PRIMARY KEY("role_id")
);


CREATE TABLE "user" (
	"user_id" serial NOT NULL UNIQUE,
	"role_id" int,
	"name" varchar(255),
	"login" varchar(255),
	"password" varchar(255),
	PRIMARY KEY("user_id")
);


CREATE TABLE "user_logs" (
	"log_id" serial NOT NULL UNIQUE,
	"user_id" int,
	"action_id" int,
	"timestamp" TIMESTAMPTZ,
	"description" varchar(255),
	PRIMARY KEY("log_id")
);


CREATE TABLE "user_action" (
	"action_id" integer NOT NULL UNIQUE,
	"name" varchar(255),
	"description" varchar(255),
	PRIMARY KEY("action_id")
);


CREATE TABLE "actuators" (
	"actuator_id" serial NOT NULL UNIQUE,
	"actuator_opcua_endpoint" varchar(255),
	"actuator_status" int,
	PRIMARY KEY("actuator_id")
);


CREATE TABLE "actuator_params" (
	"actuator_id" int,
	"node_id" varchar(255) NOT NULL UNIQUE,
	"param_name" varchar(255),
	"param_value" integer,
	"timestamp" TIMESTAMPTZ,
	PRIMARY KEY("node_id")
);


CREATE TABLE "actuator_trends" (
	"trend_id" integer NOT NULL UNIQUE,
	"actuator_id" int,
	"node_value" float,
	"timestamp" TIMESTAMPTZ,
	PRIMARY KEY("trend_id")
);


CREATE TABLE "alarms" (
	"alarm_id" serial NOT NULL UNIQUE,
	"type" int,
	"description" varchar(255),
	"user_id" int,
	"acturator_id" int,
	"sensor_id" int,
	"timestamp" TIMESTAMPTZ,
	PRIMARY KEY("alarm_id")
);


CREATE TABLE "report" (
	"report_id" serial NOT NULL UNIQUE,
	"alarm_id" int,
	"filepath" varchar(255),
	PRIMARY KEY("report_id")
);


ALTER TABLE "sensor_params"
ADD FOREIGN KEY("sensor_id") REFERENCES "sensors"("sensor_id")
ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE "user_logs"
ADD FOREIGN KEY("action_id") REFERENCES "user_action"("action_id")
ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE "user_logs"
ADD FOREIGN KEY("user_id") REFERENCES "user"("user_id")
ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE "user"
ADD FOREIGN KEY("role_id") REFERENCES "user_role"("role_id")
ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE "actuator_params"
ADD FOREIGN KEY("actuator_id") REFERENCES "actuators"("actuator_id")
ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE "sensor_trends"
ADD FOREIGN KEY("sensor_id") REFERENCES "sensors"("sensor_id")
ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "actuator_trends"
ADD FOREIGN KEY("actuator_id") REFERENCES "actuators"("actuator_id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "alarms"
ADD FOREIGN KEY("acturator_id") REFERENCES "actuators"("actuator_id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "alarms"
ADD FOREIGN KEY("sensor_id") REFERENCES "sensors"("sensor_id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "alarms"
ADD FOREIGN KEY("user_id") REFERENCES "user"("user_id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "alarms"
ADD FOREIGN KEY("type") REFERENCES "alarm_type"("type_id")
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "report"
ADD FOREIGN KEY("alarm_id") REFERENCES "alarms"("alarm_id")
ON UPDATE NO ACTION ON DELETE NO ACTION;