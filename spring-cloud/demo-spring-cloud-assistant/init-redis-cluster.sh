#!/bin/bash

sleep 30;

echo "yes" | redis-cli --cluster create $@;

sleep 5;

for i in {1..15}
do
  redis-cli -c -h node1 set key$i value$i;
done
