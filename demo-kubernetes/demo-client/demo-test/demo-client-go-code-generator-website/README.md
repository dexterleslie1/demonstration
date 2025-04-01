## client-go code-generator使用

> https://www.redhat.com/en/blog/kubernetes-deep-dive-code-generation-customresources
> https://tangxusc.github.io/blog/2019/05/code-generator%E4%BD%BF%E7%94%A8/
> https://www.cnblogs.com/double12gzh/p/13734830.html
> https://github.com/kubernetes/sample-controller
> https://github.com/chenzongshu/Kubernetes/blob/master/%E8%87%AA%E5%B7%B1%E5%8A%A8%E6%89%8B%E5%86%99controller.md

```shell
# 编译website-controller
sh buildAndPush.sh

# 安装crd、启动 website-controller pod
helm install mywebsite .

# 创建website
kubectl apply -f examples/website-example.yaml

# 删除website
kubectl delete -f examples/website-example.yaml

# 卸载website-controller pod
helm uninstall mywebsite
```

