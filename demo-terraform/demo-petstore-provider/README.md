## 说明

> petstore-api: 使用spring-boot实现的后端petstore api
> petstore-api-sdk-go: 使用go语言开发的petstore api sdk工具包
> petstore-provider: petstore terraform provider
> petstore-it-test: 用于terraform apply和destroy集成测试使用



## petstore-api-sdk-go单元测试步骤

```
# 启动petstore-api服务

# 切换到 petstore-api-sdk-go 目录

# 运行单元测试
go test -v
```



## petstore-provider单元测试步骤

```
# 启动petstore-api服务

# 导出TF_ACC环境变量，否则执行测试时候会报告错误
export TF_ACC=1

# 运行测试
go test -v ./petstore-provider/
```



## 运行步骤

```
# 运行build.sh编译terraform provider
./build.sh

# 启动petstore-api服务

# 创建~/.terraformrc文件，内容如下:
# NOTE: my-pet/petstore对应main.tf terraform配置中的 required_provider->source配置标识
provider_installation {
	dev_overrides {
		"my-pet/petstore" = "/tmp"
	}
	direct {}
}

# NOTE: 不需要terraform init

# 切换到目录 petstore-it-test运行命令apply、destroy
terraform apply
terraform show
terraform destroy
```

