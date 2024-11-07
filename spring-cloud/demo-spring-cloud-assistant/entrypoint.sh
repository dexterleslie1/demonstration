#!/bin/bash

set -e

chmod -R g+w /usr/share/elasticsearch

/tini -- /usr/local/bin/docker-entrypoint.sh "eswrapper"
