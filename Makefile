#!make
include .env
VARS:=$(shell sed -ne 's/ *\#.*$$//; /./ s/=.*$$// p' .env )
$(foreach v,$(VARS),$(eval $(shell echo export $(v)="$($(v))")))

docker_compose_bin := $(shell command -v docker-compose 2> /dev/null)
java_bin := $(shell command -v java 2> /dev/null)
mvn_bin := $(shell command -v mvn 2> /dev/null)

define run-docker-dev
$(docker_compose_bin) -f docker-compose.dev.yml up -d --force-recreate
endef

define run-migrations
cd ./creep-tenuous-repository && mvn clean flyway:migrate -Dflyway.configFiles=flyway.conf
endef

build-dev-docker:
	$(docker_compose_bin) -f docker-compose.dev.yml build

dev-docker:
	@$(run-docker-dev)

migrate:
	@$(run-migrations)

full-dev:
	./init.sh
	@$(run-docker-dev)
	@$(run-migrations)
	mvn spring-boot:run

build:
	mvn clean package spring-boot:repackage

build-scip-tests:
	mvn clean package spring-boot:repackage -Dmaven.test.skip

run:
	$(java_bin) -jar creep-tenuous-api/target/creep-tenuous-api-0.0.10-SNAPSHOT.jar

move-integration-key-implants:
	mv integration-main-system.pem ./creep-tenuous-integration/creep-tenuous-integration-implants/src/main/resources/keys
