# 配置和使用`dashboard`

>`https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/`



## 什么是`dashboard`

Dashboard 是一个基于 Web 的 Kubernetes 用户界面。您可以使用 Dashboard 将容器化应用程序部署到 Kubernetes 集群、对容器化应用程序进行故障排除以及管理集群资源。您可以使用 Dashboard 获取集群上运行的应用程序的概览，以及创建或修改单个 Kubernetes 资源（例如部署、作业、守护进程集等）。例如，您可以使用部署向导扩展部署、启动滚动更新、重新启动 pod 或部署新应用程序。



## 部署`dashboard`

下载`dashboard charts`，`https://github.com/kubernetes/dashboard/releases/download/kubernetes-dashboard-7.10.0/kubernetes-dashboard-7.10.0.tgz`

通过本地的`charts`部署`dashboard`

```bash
helm upgrade --install kubernetes-dashboard ./kubernetes-dashboard-7.10.0.tgz --create-namespace --namespace kubernetes-dashboard
```

查看`dashboard`相关服务是否正常启动

```bash
kubectl -n kubernetes-dashboard get svc
kubectl -n kubernetes-dashboard get pods -o wide
```

本地`8443`端口转发到`kubernetes-dashboard-kong-proxy`服务

```bash
kubectl -n kubernetes-dashboard port-forward svc/kubernetes-dashboard-kong-proxy 8443:443
```

访问`https://localhost:8443/`登录`dashboard`

创建`bearer token`，`https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md`

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kubernetes-dashboard
---
apiVersion: v1
kind: Secret
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
  annotations:
    kubernetes.io/service-account.name: "admin-user"   
type: kubernetes.io/service-account-token  

```

获取`bearer token`

```bash
kubectl get secret admin-user -n kubernetes-dashboard -o jsonpath={".data.token"} | base64 -d
```