## terraform用法



### pulumi

> todo 学习pulumi



### terraform基础



#### 启用terraform apply调试模式

> https://developer.hashicorp.com/terraform/internals/debugging

```
# 启用调试模式
export TF_LOG=trace

# 关闭调试模式
export TF_LOG=ERROR
```



#### meta-arguments



##### for_each

> 参考 demo-meta-arguments-foreach.tf



#### expression表达式



##### for表达式

> 参考 demo-expression-for.tf



#### local变量

> 参考 demo-variable.tf#演示本地变量



#### 使用variable.validation验证参数

> 参考 demo-variable.tf#参数验证



#### 使用terraform.tfvars提供变量值

```
# 在terraform工作目录中创建名为terraform.tfvars的文件，内容如下:
my_var1 = "Hello world!"
```



#### module

> 参考 demo-modular、demo-deploy-webapp-to-aws





#### 发布自定义模块modular



##### 使用github发布自定义模块

> 参考 demo-publish-self-defined-module
>
> NOTE: 仓库命名规范为 terraform-<PROVIDER>-<NAME>，否则在使用terraform注册表发布模块时无法自动识别仓库。

```
# 编写自定义模块

# 把自定义模块上传到github中

# 新建main.tf引用自定义模块
module "say_hello" {
  source = "github.com/dexterleslie1/terraform-demo-publish.git"
  # source = "./src"

  name = "Dexter"
}

output "output_for_debugging" {
  value = module.say_hello.hello_string
}
```



##### 使用terraform注册表发布自定义模块

```
# 参考上面的"使用github发布自定义模块"把代码上传到github中

# 访问 https://registry.terraform.io 点击publish并使用github帐号登录

# 更加指引选择仓库dexterleslie1/terraform-demo-publish仓库点击publish

# 新建main.tf调试自定义模块
module "say_hello" {
  source  = "dexterleslie1/publish/demo"
  version = "1.0.0"

  name = "Dexter"
}

output "output_for_debugging" {
  value = module.say_hello.hello_string
}
```



#### 迁移状态

##### 使用terraform state mv移动状态文件

```
# 使用下面main.tf创建第一个资源
variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}

provider "aws" {
  region = var.my_aws_region
}

resource "aws_resourcegroups_group" "group1" {
  name = "demo-group1"

  resource_query {
    query = <<JSON
{
  "ResourceTypeFilters": [
    "AWS::AllSupported"
  ],
  "TagFilters": [
    {
      "Key": "Stage",
      "Values": ["demo"]
    }
  ]
}
JSON
  }
}

# 部署地一个资源
terraform apply

# 模拟修改main.tf结构导致资源重建，main.tf内容如下:
variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}

provider "aws" {
  region = var.my_aws_region
}

resource "aws_resourcegroups_group" "group2" {
  name = "demo-group1"

  resource_query {
    query = <<JSON
{
  "ResourceTypeFilters": [
    "AWS::AllSupported"
  ],
  "TagFilters": [
    {
      "Key": "Stage",
      "Values": ["demo"]
    }
  ]
}
JSON
  }
}

# 此时terraform plan会提示resourcegroups会先被destroy然后再create

# 显示当前资源列表
terraform state list

# 迁移aws_resourcegroups_group.group1到aws_resoucegroups_group.group2
terraform state mv aws_resourcegroups_group.group1 aws_resourcegroups_group.group2

# 执行terraform plan会提示没有objects被修改
```



##### 使用terraform state rm删除旧资源，然后使用terraform import重新导入

```
# 使用下面main.tf创建第一个资源
variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}

provider "aws" {
  region = var.my_aws_region
}

resource "aws_resourcegroups_group" "group1" {
  name = "demo-group1"

  resource_query {
    query = <<JSON
{
  "ResourceTypeFilters": [
    "AWS::AllSupported"
  ],
  "TagFilters": [
    {
      "Key": "Stage",
      "Values": ["demo"]
    }
  ]
}
JSON
  }
}

# 创建资源
terraform apply -auto-approve

# 查看当前资源列表
terraform state list

# 获取资源的id稍后导入用到
terraform state show aws_resourcegroups_group.group1

# 删除指定的资源
terraform state rm aws_resourcegroups_group.group1

# 尝试再次创建同名的资源报错
terraform apply -auto-approve

# 导入已存在的资源
terraform import aws_resourcegroups_group.group1 demo-group1
```





##### 自动化测试IaC terraform-exec

> 参考 demo-testing

```
# 编写完go代码后，在demo-testing目录初始化go module
go mod init example.com/m/v2
go mod tidy

# 执行测试
go test -v
```





### workspace

> 参考 demo-workspace
>
> https://developer.hashicorp.com/terraform/language/state/workspaces
> https://developer.hashicorp.com/terraform/cli/workspaces



```
# 查询workspace列表
terraform workspace list

# 新建workspace
terraform workspace new prod
terrafrom workspace new dev

# 切换workspace
terraform workspace select default
terraform workspace select prod

# 删除workspace
terraform workspace delete prod

# 切换workspace后指定tfvars文件
terraform workspace select prod
terraform apply -var-file=prod.tfvars
```



### terraform cli用法



#### terraform apply

```
# 执行main.tf文件
terraform apply

# terraform从v0.15.2起弃用terraform taint，使用terraform apply -replace替换
# https://jeffbrown.tech/terraform-taint-replace/
terraform apply -replace="google_compute_instance.demo_jmeter_master_vm"
terraform apply -replace="google_compute_instance.demo_jmeter_slave_vm"

# 自动确认
terraform apply --auto-approve
```



#### terraform plan

> 查看terraform将要创建资源的参数但不实际执行资源创建

```
# 执行命令
terraform plan
```



#### terraform show

> 把tf文件中所有变量解析并显示实际值

```
# 执行命令
terraform show
```



#### terraform refresh

> 此命令读取远程资源状态并同步到本地状态存储中。用于处理配置漂移情况，执行refresh后再执行terraform show就能够查看远程状态已经成功同步到本地状态存储中。



#### terraform console

> https://developer.hashicorp.com/terraform/cli/commands/console
> https://stackoverflow.com/questions/65818997/how-can-i-print-debug-all-available-fields-of-a-data-source-resource
>
> 使用terraform console调试 

```
# main.tf内容如下:
variable "vsphere_user" {
  type    = string
  default = "xxx@vsphere.local"
}
variable "vsphere_password" {
  type = string
  default = "xxx"
}
variable "vsphere_server" {
  type = string
  default = "192.168.1.xxx"
}

provider "vsphere" {
  user                 = var.vsphere_user
  password             = var.vsphere_password
  vsphere_server       = var.vsphere_server
  allow_unverified_ssl = true
}

data "vsphere_datacenter" "datacenter" {
  name = "Datacenter"
}

data "vsphere_datastore" "datastore" {
  name          = "datastoreraid0"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_host" "host" {
  name          = "192.168.1.49"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_network" "network" {
  name          = "VM Network"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_virtual_machine" "template" {
  name          = "my-template-centOS8"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

# 初始化项目
terraform init

# 通过apply同步远程状态到本地
terraform apply

# 进入terraform console
terraform console
# 在console中显示变量值
> var.vsphere_user
# 在console中打印datasource template的值
> data.vsphere_virtual_machine.template

```



#### terraform destroy删除指定的资源

> https://www.devopsschool.com/blog/how-to-destroy-one-specific-resource-from-tf-file-in-terraform/

```
# 显示所有资源
terraform state list

# 删除指定的资源
terraform destroy --target=vsphere_virtual_machine.vm_devops_master
```



#### terraform output

> 从state文件中读取output变量并打印
>
> https://developer.hashicorp.com/terraform/cli/commands/output

```
# main.tf内容如下
output "current_dir" {
    value = "${path.module}"
}

terraform init
terraform apply

# 打印变量
terraform output
```



#### terraform state

> https://developer.hashicorp.com/terraform/cli/commands/state/show

```
# 显示指定资源的状态
terraform state show "aws_instance.demo_vm1"
```





#### terraform taint

> 参考 demo-taint
>
> https://developer.hashicorp.com/terraform/cli/commands/taint

```
# 获取资源地址
terraform state list

# 标记资源taint
terraform tain aws_iam_access_key.app1

# 取消标记资源taint
terraform untaint aws_iam_access_key.app1

# 重建被taint的资源
terraform apply -auto-approve
```



### terraform插件本地缓存配置

> 避免每次terraform init时都到官方下载插件
> https://bbs.huaweicloud.com/blogs/352925

```
# 创建目录 ~/.terraform.d/plugin-cache
mkdir ~/.terraform.d/plugin-cache

# 创建目录 /root/.terraform.d/plugin-filesystem-mirror
#mkdir /root/.terraform.d/plugin-filesystem-mirror

# 创建 ~/.terraformrc文件内容如下
plugin_cache_dir = "$HOME/.terraform.d/plugin-cache"
disable_checkpoint = true

#provider_installation {
#  filesystem_mirror {
#    path    = "/root/.terraform.d/plugin-filesystem-mirror"
#    include = ["registry.terraform.io/*/*"]
#  }
#  direct {
#    include = ["registry.terraform.io/*/*"]
#  }
#}

# 切换到main.tf所在目录
# terraform会根据当前main.tf下载插件到 /root/.terraform.d/plugin-filesystem-mirror 目录，以便下次terraform init时使用本地缓存不需要到官方下载插件
#terraform providers mirror ~/.terraform.d/plugin-filesystem-mirror/

# 使用本地插件缓存初始化当前tf目录
terraform init
```



### Provisioner Connection用法

> https://developer.hashicorp.com/terraform/language/resources/provisioners/connection
> 用于配置SSH或者WinRM连接信息

```
# 在resource中配置connection，命令在虚拟机创建后执行一次
resource "vsphere_virtual_machine" "vm" {

  connection {
    type     = "ssh"
    user     = "root"
    password = "xxx"
    # 通过vsphere_virtual_machine.vm.default_ip_address获取当前虚拟机ip地址
    # https://stackoverflow.com/questions/62498591/how-do-i-get-the-ip-of-a-vsphere-virtual-machine-to-run-ansible-playbook
    host     = self.default_ip_address
  }

  provisioner "remote-exec" {
    inline = [
      # 命令只会在虚拟机被创建后执行一次
      "date >> /tmp/1.log",
    ]
  }
...
}
```



### Provisioner local-exec用法

> https://developer.hashicorp.com/terraform/language/resources/provisioners/local-exec
> NOTE: 在本地(terraform主机)执行命令

```
# main.tf内容如下:
resource "null_resource" "test" {
        triggers = {
                // 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
                always_run = "${timestamp()}"
        }
        provisioner "local-exec" {
                command = "date"
        }
}

# 初始化
terraform init

# 执行
terraform apply

# 会看到在本地执行命令date的输出
```



### Provisioner remote-exec用法

> https://developer.hashicorp.com/terraform/language/resources/provisioners/remote-exec#scripts
> 用于连接到远程主机执行命令

```
# 在resource中配置remote-exec，命令在虚拟机创建后执行一次
resource "vsphere_virtual_machine" "vm" {

  connection {
    type     = "ssh"
    user     = "root"
    password = "xxx"
    # 通过vsphere_virtual_machine.vm.default_ip_address获取当前虚拟机ip地址
    host     = self.default_ip_address
  }

  provisioner "remote-exec" {
    inline = [
      # 命令只会在虚拟机被创建后执行一次
      "date >> /tmp/1.log",
    ]
  }
...
}
```



### providers



#### template provider

> 参考 demo-provider-template.tf



#### external provider

> 参考 demo-provider-external.tf



#### local provider

> 参考demo-local-provider.tf



#### random provider

> 参考 demo-provider-random.tf



#### null provider

> 参考 provider/null demo
>





#### azure provider

> https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs



##### 配置认证信息

```
# 这里选择Service Principal with a client secret方式配置认证
# https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/guides/service_principal_client_secret
# NOTE: 根据文档步骤配置client secret，在Add role assignment时Members选择Azure Active Directory中创建的App Application，否则在执行terraform apply时会报告权限不足错误。
```



##### 演示案例

> 参考 azure/main.tf 演示使用azurerm provider部署一个无服务应用



#### aws provider



##### 配置terraform认证信息

> https://registry.terraform.io/providers/hashicorp/aws/latest/docs

```
# 在aws console中创建IAM帐号并记录access_key_id和secret_access_key

# 创建文件 ~/.aws/config，内容如下:
[default]
aws_access_key_id=xxx
aws_secret_access_key=xxx
```



##### aws_vpc资源

> 参考 aws/aws-vpc
>
> https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc.html



##### aws_ami资源

> 参考 aws/aws-ami
>
> https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/ami
>
> 官方发布ami列表
> https://www.centos.org/download/aws-images/#centos-amazon-ami-images



##### aws instance资源

> 参考 aws/aws-instance
>
> https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance#vpc_security_group_ids



#### google provider



##### google region和zone列表

> https://cloud.google.com/compute/docs/regions-zones



##### socks5和鉴权环境配置

```
# 配置socks5代理
# https://stackoverflow.com/questions/65939427/how-to-set-up-terraform-behind-proxy
export HTTP_PROXY=socks5://192.168.1.55:1080
export HTTPS_PROXY=socks5://192.168.1.55:1080

# 参考配置provider鉴权
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/guides/provider_reference
# 通过google service account控制台在指定的service account下创建key file
# https://console.cloud.google.com/apis/credentials/serviceaccountkey
# https://cloud.google.com/docs/authentication/application-default-credentials#personal
# 在目录中配置service account json文件
~/.config/gcloud/application_default_credentials.json
```



##### google_compute_instance资源

> 参考gcp/main.tf
>
> https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_instance#nested_access_config



#### ucloud provider

> NOTE: 虚拟机一但创建就预收一个小时费用，贵！暂时不使用。
>
> https://registry.terraform.io/providers/ucloud/ucloud/latest/docs



#### 自定义petstore provider

> 参考 demo-petstore-provider



### provisioners



#### provisioner file

> 参考demo-provisioner-file
>
> https://developer.hashicorp.com/terraform/language/resources/provisioners/file

```
# 初始化项目
terraform init

# 运行
terraform apply

# 删除
terraform destroy
```



### functions函数



#### file函数

> 参考demo-function-file.tf
>
> https://developer.hashicorp.com/terraform/language/functions/file



#### length函数

> 参考demo-function-length.tf
>
> https://developer.hashicorp.com/terraform/language/functions/length



#### templatefile函数

> 参考 demo-function-templatefile.tf



### 后端backends状态存储配置

> https://developer.hashicorp.com/terraform/language/settings/backends/configuration



#### local backend

> NOTE: 通过破坏测试，如果本地丢失terraform相关状态文件，terraform无法知道远程服务的resource状态导致terraform plan会提示创建新的资源。
>
> https://developer.hashicorp.com/terraform/language/settings/backends/local



#### s3 backend

> 参考 demo-backend/s3
>
> NOTE: s3 backend在使用terraform init时就自动到aws s3同步状态。
>
> https://developer.hashicorp.com/terraform/language/settings/backends/s3

```
# 演示步骤
# 进入create-s3目录
terraform init
terraform apply
# 复制output相关信息到use-s3-as-backend/main.tf backend "s3"中

# 进入use-s3-as-backend目录
terraform init
terraform apply

# 登录aws console查看s3 bucket: demo-aws-s3-bucket1中会有一个名为 my-s3-key 的文件，这个文件存储了terraform的状态。

```



#### 使用terraform cloud作为backend

> terraform cloud注册
> https://developer.hashicorp.com/terraform/tutorials/cloud-get-started/cloud-sign-up
>
> 配置terraform cloud
> https://developer.hashicorp.com/terraform/cli/cloud/settings
>
> 参考 demo-backend/cloud

```
# 步骤如下

# 1、注册一个terraform cloud帐号
# 2、使用terraform login在本地创建一个token用于backend和cloud通讯并存储状态，NOTE: token会自动保存在目录~/.terraform.d中
# 2、登录cloud并创建一个org，NOTE: 不需要手动创建workspace
# 3、复制如下内容到main.tf
terraform {
  cloud {
    organization = "future-demo-my-org"

    workspaces {
      # 设置拥有指定tag的workspace，可以使用terraform workspace select命令切换workspace
      tags = ["my-tag"]
    }
  }
}
# 4、terraform init，根据提示输入一个新的workspace名称: dev
# 5、terraform apply -auto-approve
# 6、创建另外一个workspace: prod
terraform workspace new prod
terraform apply -auto-approve
```



### 安全和密钥管理



#### 保护terraform状态



##### 从terraform状态删除不必要的密钥

> 参考 demo-security-and-key-management/demo-terraform-state-protection-lambda-function-leak
>
> 这个例子演示意外地使用aws lambda function的环境变量特性暴露rds帐号密码。



##### 使用最小的特权访问

> 参考 demo-security-and-key-management/demo-terraform-state-minimum-iam-s3-backend-policy
>
> NOTE: 这个例子没有实际测试验证



##### 静态加密

> 在使用s3后端时，通过指定一个KMS密钥来使客户端加密或者让s3为服务端加密使用默认加密钥匙。
>
> 在使用terraform cloud或者terraform enterprise则默认情况下会自动静态加密。事实上，数据会被双重加密： 使用kms加密一次，然后再使用Vault加密一次。





#### 保护日志



##### 哪些敏感信息会泄漏？

> 场景: 通过启用TF_LOG=trace后，执行terraform apply输出日志中会泄漏敏感的api调用身份鉴权信息。
> 解决方案: 除非在进行调试，否则始终应该关闭跟踪日志。

```
# main.tf内容如下:
variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}
variable "ssh_user" {
  type    = string
  default = "centos"
}

provider "aws" {
  region = var.my_aws_region
}

# 使用默认参数创建密钥
# https://registry.terraform.io/providers/hashicorp/aws/3.26.0/docs/resources/kms_key
resource "aws_kms_key" "demo_aws_kms_key1" {
  description = "demo-aws-kms-key1"
}

# 创建密钥别名
# https://registry.terraform.io/providers/hashicorp/aws/5.17.0/docs/resources/kms_alias
resource "aws_kms_alias" "demo_aws_kms_alias1" {
    name = "alias/demo-aws-kms-alias1"
    target_key_id = aws_kms_key.demo_aws_kms_key1.key_id
}

# 启用TF_LOG=trace
export TF_LOG=trace

# 执行apply
terraform apply
```



##### local-exec置备程序的危险

> 演示通过local-exec置备程序输出本地环境变量AWS_ACCESS_KEY_ID

```
# main.tf内容如下:
resource "null_resource" "uh_oh" {
  triggers = {
    always = timestamp()
  }
  provisioner "local-exec" {
    command = <<-EOF
      echo "access_key=$AWS_ACCESS_KEY_ID"
    EOF
  }
}

# 执行apply
terraform apply
```



##### 外部数据源的危险

> 演示通过external provider调用未知外部脚本的危害性。

```
# main.tf内容如下:
data "external" "do_bad_stuff" {
  program = ["node", "${path.module}/run.js"]
}
output "my_output_do_bad_stuff" {
  value = data.external.do_bad_stuff.result
}

# run.js内容如下:
console.log(JSON.stringify({
    AWS_ACCESS_KEY_ID: process.env.AWS_ACCESS_KEY_ID
}))

# 执行plan就能够触发数据源external执行脚本
terraform plan
```



##### HTTP提供程序的危险

> NOTE: 没有做实验证明
> 由http provider泄漏敏感信息。



#### 使用动态密钥vault、AWS secrets Manager



##### vault

> NOTE: 暂时不需要不研究



##### AWS secrets Manager

> NOTE: 暂时不需要不研究



#### sentinel和策略即代码

> NOTE: 暂时不需要不研究





### terraform cloud、enterprise

> NOTE: 通过阅读terraform cloud文档初步判断terraform cloud是远程执行的，它会把远程执行结果回传到本地cli stdout中。



#### 注册cloud帐号

> https://developer.hashicorp.com/terraform/tutorials/cloud-get-started/cloud-sign-up



#### terraform cli登录terraform cloud

> https://developer.hashicorp.com/terraform/tutorials/cloud-get-started/cloud-login



#### 创建变量

> https://developer.hashicorp.com/terraform/tutorials/cloud-get-started/cloud-create-variable-set



#### terraform cloud api

>
> 参考 demo-cloud-api
> https://developer.hashicorp.com/terraform/cloud-docs/run/api



##### 鉴权配置

> https://developer.hashicorp.com/terraform/cloud-docs/api-docs
