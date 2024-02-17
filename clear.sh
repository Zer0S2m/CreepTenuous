#!/bin/sh

docker exec -it creeptenuous-redis-1 redis-cli flushall
rm -rf $(echo "${CT_ROOT_PATH}"/*)
