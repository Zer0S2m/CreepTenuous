# CreepTenuous

<p align="center">
<img src="./doc/logo.svg" alt="Logo project" width="300" height="300">
</p>

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

## Integration modules

### [CreepTenuousImplants](https://github.com/Zer0S2m/CreepTenuousImplants)
In order to integrate an additional module and open access to it, 
you must complete the steps that are described in the module 
[documentation](https://github.com/Zer0S2m/CreepTenuousImplants#integration)

The generated key (in an additional module) you must place in the following directory:
**`./creep-tenuous-integration/creep-tenuous-integration-implants/src/main/resources/keys`**.

Or run the following command to transfer the private key:
```shell
make move-integration-key-implants
```

Before starting the system, write the following variable in the **`.env`** file:
```shell
export CT_IS_INTEGRATION_IMPLANTS=true
```