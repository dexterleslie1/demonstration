## cnat演示

> 参考cnat/cnat-client-go
> https://github.com/programming-kubernetes/cnat

```shell
# 创建crd
kubectl apply -f artifacts/examples/cnat-crd.yaml

# 运行cnat-controller
unset HTTPS_PROXY
go run main.go

# 创建at
kubectl apply -f artifacts/examples/cnat-example.yaml

# 删除at
kubectl delete -f artifacts/examples/cnat-example.yaml
# 删除crd
kubectl delete -f artifacts/examples/cnat-crd.yaml
```

