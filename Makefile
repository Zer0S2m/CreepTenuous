docker_compose_bin := $(shell command -v docker-compose 2> /dev/null)
mvn_bin := $(shell command -v mvn 2> /dev/null)

define run-docker-dev
$(docker_compose_bin) -f docker-compose.dev.yml up -d --force-recreate
endef

build-dev:
	$(docker_compose_bin) -f docker-compose.dev.yml build

dev:
	@$(run-docker-dev)
