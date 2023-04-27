# CreepTenuous

### Modules
1) Client - [CreepTenuousClient](https://github.com/Zer0S2m/CreepTenuousClient)

### Development
Before starting, create an **`.env`** file and edit it (it is advisable to specify the variables that are described in the files: **`./docker-compose.dev.yml`** and **`./flyway-config.conf`**):
```shell
cp .env.example .env
```

InitL
```shell
sudo chmod 755 ./init.sh
```

Build docker:
```shell
make build-dev-docker
```

Run docker:
```shell
make dev
```

Run full system:
```shell
sudo make full-dev
```

Build system:
```shell
make build
```

Run system after build:
```shell
make dev
make run
```
