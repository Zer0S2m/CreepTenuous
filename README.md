# CreepTenuous

<p align="center">
<img src="./doc/logo.svg" alt="Logo project" width="300" height="300">
</p>

## Modules

1) Client - [CreepTenuousClient](https://github.com/Zer0S2m/CreepTenuousClient)
2) Tentacles - [CreepTenuousTentacle](https://github.com/Zer0S2m/CreepTenuousTentacle)
3) Implants - [CreepTenuousImplants](https://github.com/Zer0S2m/CreepTenuousImplants)
4) Desktop - [CreepTenuousDesktop](https://github.com/Zer0S2m/CreepTenuousDesktop)

## Development

Before starting, create an **`.env`** file and edit it (it is advisable to specify the variables that are described in
the files: **`./docker-compose.dev.yml`** and **`./flyway-config.conf`**):

```shell
cp .env.example .env
```

_[Parameter information](#env-params)_

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

## Env params

| Env param                        | Value                                                                                                                             | Example          |
|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|------------------|
| **`CT_REDIS_PORT`**              | Indicates the port where the server is listening                                                                                  | `6379`           |
| **`CT_REDIS_HOST`**              | Is where the server host is located                                                                                               | `localhost`      |
| **`CT_REDIS_DATABASE`**          | Sets the database index used by the connection factory                                                                            | `0`              |
| **`CT_REDIS_TIMEOUT`**           | Establishes the connection timeout                                                                                                | `60000`          |
| **`CT_POSTGRES_PORT`**           | Indicates the port where the server is listening                                                                                  | `localhost`      |
| **`CT_POSTGRES_HOST`**           | Is where the server host is located                                                                                               | `5432`           |
| **`CT_POSTGRES_PASSWORD`**       | Password for connecting to the specified database                                                                                 | `my_db_password` |
| **`CT_POSTGRES_USER`**           | Database user                                                                                                                     | `my_db_user`     |
| **`CT_POSTGRES_DB`**             | Database name                                                                                                                     | `my_db`          |
| **`CT_ROOT_PATH`**               | Primary location of files for distribution                                                                                        | `/path`          |
| **`CT_DIRECTORY_AVATARS`**       | The main location of uploaded avatars for users. <br/>Default - `/var/www/creep-tenuous/avatars`                                  | `/path`          |
| **`CT_SECRET_ACCESS`**           | Secret key for creating jwt tokens of type - access                                                                               | `secret-key`     |
| **`CT_SECRET_REFRESH`**          | Secret key for creating jwt tokens of type - refresh                                                                              | `secret-key`     |
| **`CT_IS_INTEGRATION_IMPLANTS`** | The main parameter for connecting module integration is - [CreepTenuousImplants](https://github.com/Zer0S2m/CreepTenuousImplants) | `true`           |
