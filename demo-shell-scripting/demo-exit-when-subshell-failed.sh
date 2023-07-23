#!/bin/bash

# 当subshell错误时终止shell执行
# https://stackoverflow.com/questions/14970663/why-doesnt-bash-flag-e-exit-when-a-subshell-fails

(commandnotexists) || exit $?

echo "Hello world!"
