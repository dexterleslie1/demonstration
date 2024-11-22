# `Google Kubernetes Engine`

>`https://cloud.google.com/kubernetes-engine/docs/concepts/kubernetes-engine-overview?hl=zh-cn`

`todo`创建集群后`GKE`的`Cluster Networking`显示`34.118.224.0/20`导致`service`的`cluster ip`为`34`开头。

提醒：

- 无论是`Autopilot`还是`Standard`模式的`GKE`都是通过`CloudShell`中配置的`kubectl`通过互联网管理`GKE`集群。
- 在`GKE`控制台中支持通过`yaml`部署应用。



## `GKE`的工作原理

>`https://cloud.google.com/kubernetes-engine/docs/concepts/kubernetes-engine-overview?hl=zh-cn#how-works`

`GKE`环境由节点`nodes`组成，节点是 [`Compute Engine`虚拟机 (`VM`)](https://cloud.google.com/compute?hl=zh-cn)，它们组合在一起构成集群`nodes`。您将应用（也称为工作负载）打包到容器中。您可以将多个容器作为`Pod`部署到节点。您可以使用`Kubernetes API`与工作负载进行交互，包括管理、扩缩和监控。

`Kubernetes`集群有一组称为“控制平面”的管理节点，它们运行`Kubernetes API`服务器等系统组件。在`GKE`中，`Google`会为您管理控制平面和系统组件。在 `Autopilot`模式下（推荐采用此方法运行`GKE`），`Google`还会管理您的工作器节点。`Google`会自动升级组件版本以提高稳定性和安全性，从而确保高可用性和集群永久性存储空间中存储的数据的完整性。



## 选择`GKE`操作模式

>`https://cloud.google.com/kubernetes-engine/docs/concepts/choose-cluster-mode?hl=zh-cn`

`GKE`为集群提供以下操作模式：

- **`Autopilot`模式（推荐）**：`GKE`管理底层基础架构，例如节点配置、自动扩缩、自动升级、基准安全配置和基准网络配置。
- **`Standard`模式**：您负责管理底层基础架构，包括配置各个节点。

使用全托管式 [`Autopilot`模式](https://cloud.google.com/kubernetes-engine/docs/concepts/autopilot-overview?hl=zh-cn)，在该模式下，`Google Cloud`为您管理节点，并提供以工作负载为中心、费用优化且可直接投入生产的体验。仅当您知道自己具有特定需求，需要手动管理节点池和集群时，才应使用`Standard`模式。



## 创建`Autopilot`集群

提醒：

- 因为使用`CloudShell`中的`kubectl`使用互联网连接`API server`，在创建集群时，需要勾选`Control Plane Access`>`Access using DNS`、`Access using IPv4 addresses`、`Access using the control plane's external IP address`、`Access using the control plane's internal IP address from any region`。
- 成功使用 <a href="/spring-cloud/assistant示例.html#运行assistant-k8s示例" target="_blank">链接</a> 测试集群环境（注意：需要手动修改`storageClass`为`standard`，在部署应用等待过程需要等待约`5`到`10`分钟，因为`GKE`自动创建并运行`worker`节点）。



## 创建`Standard`集群

提醒：

- 因为使用`CloudShell`中的`kubectl`使用互联网连接`API server`，在创建集群时，需要勾选`Control Plane Access`>`Access using DNS`、`Access using IPv4 addresses`、`Access using the control plane's external IP address`、`Access using the control plane's internal IP address from any region`。
- 成功使用 <a href="/spring-cloud/assistant示例.html#运行assistant-k8s示例" target="_blank">链接</a> 测试集群环境（注意：需要手动修改`storageClass`为`standard`，`worker`节点配置为`4`核`16g`，否则报告`cpu`或者内存不足不能调度`pod`）。