## client-go code-generator使用

> https://www.redhat.com/en/blog/kubernetes-deep-dive-code-generation-customresources
> https://tangxusc.github.io/blog/2019/05/code-generator%E4%BD%BF%E7%94%A8/
> https://www.cnblogs.com/double12gzh/p/13734830.html
> https://github.com/kubernetes/sample-controller

```shell
# 下载 k8s.io/code-generator 到 vendor/k8s.io/code-generator
go mod vendor

# 生成代码 pkg/generated 和 pkg/apis/extensions.example.com/v1/zz_generated.deepcopy.go
./hack/update-codegen.sh

# 运行 main.go
go run main.go
```

