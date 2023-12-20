#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

source "vendor/k8s.io/code-generator/kube_codegen.sh"

kube::codegen::gen_helpers \
    --input-pkg-root demo-client-go-code-generator/pkg/apis \
    --output-base ../ \
    --boilerplate ./hack/boilerplate.go.txt

kube::codegen::gen_client \
    --with-watch \
    --input-pkg-root demo-client-go-code-generator/pkg/apis \
    --output-pkg-root demo-client-go-code-generator/pkg/generated \
    --output-base ../ \
    --boilerplate ./hack/boilerplate.go.txt
