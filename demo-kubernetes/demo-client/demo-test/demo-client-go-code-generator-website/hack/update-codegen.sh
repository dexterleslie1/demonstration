#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

# NOTE: 新版本不code-generator不使用vendor/k8s.io/code-generator/generate-groups.sh
#vendor/k8s.io/code-generator/generate-groups.sh all \
#demo-client-go-code-generator/pkg/generated \
#demo-client-go-code-generator/pkg/apis \
#extensions.example.com/v1 \
#--output-base ../ \
#--go-header-file ./hack/boilerplate.go.txt

source "vendor/k8s.io/code-generator/kube_codegen.sh"

# 生成zz_generated.deepcopy.go
kube::codegen::gen_helpers \
    --input-pkg-root demo-client-go-code-generator-website/pkg/apis \
    --output-base ../ \
    --boilerplate ./hack/boilerplate.go.txt

# 生成client、lister、informer
kube::codegen::gen_client \
    --with-watch \
    --input-pkg-root demo-client-go-code-generator-website/pkg/apis \
    --output-pkg-root demo-client-go-code-generator-website/pkg/generated \
    --output-base ../ \
    --boilerplate ./hack/boilerplate.go.txt
