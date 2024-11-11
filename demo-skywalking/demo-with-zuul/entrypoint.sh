#!/bin/bash

set -e

chmod -R g+w /usr/share/elasticsearch

/bin/tini -- /usr/local/bin/docker-entrypoint.sh "eswrapper"
