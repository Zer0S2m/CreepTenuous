#!make
include .env
VARS:=$(shell sed -ne 's/ *\#.*$$//; /./ s/=.*$$// p' .env )
$(foreach v,$(VARS),$(eval $(shell echo export $(v)="$($(v))")))

docker_compose_bin := $(shell command -v docker-compose 2> /dev/null)
mvn_bin := $(shell command -v mvn 2> /dev/null)

define run-docker-dev
$(docker_compose_bin) -f docker-compose.dev.yml up -d --force-recreate
endef


build-dev-docker:
	$(docker_compose_bin) -f docker-compose.dev.yml build

dev:
	@$(run-docker-dev)

full-dev:
	sudo chmod 755 ./init.sh
	./init.sh
	@$(run-docker-dev)
	mvn spring-boot:run

build:
	mvn clean package spring-boot:repackage

run:
	java -jar target/CreepTenuous-0.0.1-SNAPSHOT.jar
