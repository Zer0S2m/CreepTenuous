# CreepTenuous

## Modules
1) Client - [CreepTenuousClient](https://github.com/Zer0S2m/CreepTenuousClient)
2) Tentacles - [CreepTenuousTentacle](https://github.com/Zer0S2m/CreepTenuousTentacle)
3) Implants - [CreepTenuousImplants](https://github.com/Zer0S2m/CreepTenuousImplants)

## Development
Before starting, create an **`.env`** file and edit it (it is advisable to specify the variables that are described in the files: **`./docker-compose.dev.yml`** and **`./flyway-config.conf`**):
```shell
cp .env.example .env
```

Init:
```shell
sudo chmod 755 ./init.sh
```

Build docker:
```shell
make build-dev-docker
```

Run docker:
```shell
make dev-docker
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
