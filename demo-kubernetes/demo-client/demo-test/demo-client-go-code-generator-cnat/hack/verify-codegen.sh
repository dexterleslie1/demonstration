#!/usr/bin/env bash

# 这个脚本会调用 hack/update-codegen.sh 脚本并检查是否有什么东西发生了变化，
# 如果已经生成的代码不是最新的，这个脚本会中断运行并返回一个非零值。
# 这在持续集成的脚本中非常有用，如果开发者不小心修改了文件或者文件没有及时更新，
# CI就能发现这个问题并及时发出通知

set -o errexit
set -o nounset
set -o pipefail

SCRIPT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"
DIFFROOT="${SCRIPT_ROOT}/pkg"
TMP_DIFFROOT="$(mktemp -d -t "$(basename "$0").XXXXXX")/pkg"

cleanup() {
  rm -rf "${TMP_DIFFROOT}"
}
trap "cleanup" EXIT SIGINT

cleanup

mkdir -p "${TMP_DIFFROOT}"
cp -a "${DIFFROOT}"/* "${TMP_DIFFROOT}"

"${SCRIPT_ROOT}/hack/update-codegen.sh"
echo "diffing ${DIFFROOT} against freshly generated codegen"
ret=0
diff -Naupr "${DIFFROOT}" "${TMP_DIFFROOT}" || ret=$?
if [[ $ret -eq 0 ]]; then
  echo "${DIFFROOT} up to date."
else
  echo "${DIFFROOT} is out of date. Please run hack/update-codegen.sh"
  exit 1
fi
