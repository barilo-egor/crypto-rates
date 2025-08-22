#!/bin/sh
docker stop crypto-rates
docker rm -f crypto-rates && docker build "$@" -t auth . && \
docker run -it -d --name auth --cpus="2"  --memory="1g" --memory-reservation="1g"  --network tgb --restart always --dns 8.8.8.8 -v /home/tgb/docker/data/crypto-rates:/root/tgb crypto-rates