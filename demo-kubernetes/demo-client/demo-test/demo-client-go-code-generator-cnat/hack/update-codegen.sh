#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

source "vendor/k8s.io/code-generator/kube_codegen.sh"

## 生成zz_generated.deepcopy.go
kube::codegen::gen_helpers \
    --input-pkg-root demo-client-go-code-generator-cnat/pkg/apis \
    --output-base ../ \
    --boilerplate ./hack/boilerplate.go.txt

# 生成client、lister、informer
kube::codegen::gen_client \
    --with-watch \
    --input-pkg-root demo-client-go-code-generator-cnat/pkg/apis \
    --output-pkg-root demo-client-go-code-generator-cnat/pkg/generated \
    --output-base ../ \
    --boilerplate ./hack/boilerplate.go.txt
