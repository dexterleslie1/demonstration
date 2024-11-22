# `Elastic Kubernetes Service(EKS)`

提醒：

- 在`CloudShell`中的`kubectl`通过互联网连接`EKS`的`API Server`，`https://docs.aws.amazon.com/zh_cn/eks/latest/userguide/create-kubeconfig.html`

  打开`CloudShell`后执行以下命令会自动配置`~/.kube/config`

  ```bash
  aws eks update-kubeconfig --region ap-northeast-1 --name demo-k8s1
  ```

  - 将`region-code`替换为您的集群所在的`AWS`区域，并将`my-cluster`替换为您的集群的名称。

  测试

  ```bash
  kubectl get nodes -o wide
  ```

- 成功使用 <a href="/spring-cloud/assistant示例.html#运行assistant-k8s示例" target="_blank">链接</a> 测试集群环境。



`Amazon Elastic Kubernetes Service（Amazon EKS）`是一项托管服务，无需在`Amazon Web Services (AWS) `上安装、操作和维护自己的`Kubernetes`控制面板。支持`AWS Fargate`、`Karpenter`、托管节点组和自行管理`worker`节点`https://docs.aws.amazon.com/zh_cn/eks/latest/userguide/eks-compute.html`。



## 创建集群

>`https://docs.aws.amazon.com/zh_cn/eks/latest/userguide/getting-started-console.html`

提醒：

- `Cluster endpoint access(Configure access to the Kubernetes API server endpoint.)`选择`Public and private(The cluster endpoint is accessible from outside of your VPC. Worker node traffic to the endpoint will stay within your VPC.)`因为`CloudShell`中的`kubectl`通过互联网连接到`API server`。
- 集群创建后需要手动添加`Node Group`以创建`worker`节点，`https://ap-northeast-1.console.aws.amazon.com/eks/home?region=ap-northeast-1#/clusters/demo-k8s1?selectedTab=cluster-compute-tab`。