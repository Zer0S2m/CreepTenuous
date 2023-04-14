#!/bin/bash
# shellcheck disable=SC2046

export $(shell sed 's/=.*//' .env)
