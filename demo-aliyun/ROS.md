# 资源编排服务`ROS`

## 介绍

资源编排服务ROS（Resource Orchestration Service）是阿里云提供的一项简化云计算资源管理和采用基础设施即代码（IaC）设计理念的自动化部署服务。开发者和管理员可以编写模板，在模板中定义所需的阿里云资源（例如：ECS实例、RDS数据库实例）、资源间的依赖关系等。ROS的编排引擎将根据模板自动完成所有资源的创建和配置，实现自动化部署及运维。ROS提供了Terraform托管服务，允许用户直接在ROS控制台上使用 [Terraform模板](https://help.aliyun.com/zh/ros/user-guide/overview-2) 进行资源的自动化部署和管理。



## 计费

>[参考官方文档](https://help.aliyun.com/zh/ros/product-overview/billing-overview)

资源编排服务本身不收费。



## 语法

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/template-syntax-1/)

### 结构说明

模板是一个JSON或YAML格式的文本文件，使用UTF-8编码。模板用于创建资源栈，是描述基础设施和架构的蓝图。模板编辑者在模板中定义阿里云资源和配置细节，并说明资源间的依赖关系。

```yaml
ROSTemplateFormatVersion: '2015-09-01'
Description: 模板描述信息，可用于说明模板的适用场景、架构说明等。
# 关于模板的元数据信息，例如存放用于可视化的布局信息。
Metadata: 
# 定义创建资源栈时，用户可以定制化的参数。  
Parameters: 
# 定义映射信息表，映射信息是一种多层的Map结构。
Mappings: 
# 使用内部条件函数定义条件。这些条件确定何时创建关联的资源。
Conditions: 
# 所需资源的详细定义，包括资源间的依赖关系、配置细节等。
Resources: 
# 用于输出一些资源属性等有用信息。可以通过API或控制台获取输出的内容。
Outputs: 
# 用于检验在创建或更新资源栈时传递给模板的参数值是否符合预期。
Rules: 
```

#### ROSTemplateFormatVersion（必选）

ROS支持的模板版本号，当前版本号：2015-09-01。

#### Description（可选）

模板的描述信息。可用于说明模板的适用场景、架构说明等。通常情况下，对模板进行详细描述，有利于用户理解模板的内容。

#### Metadata（可选）

模板编写者可以使用Metadata来存放与模板相关的元数据信息，内容可以为JSON格式。

#### Parameters（可选）

定义创建资源栈时，模板用户可以定制化的参数。通常，模板的编辑者会把ECS的规格设计成一个参数。参数支持默认值。使用参数可以增强模板的灵活性，提高复用性。使用模板创建资源栈时，可以根据实际的评估结果来选择合适的规格。

#### Mappings（可选）

Mappings定义了一个多层的映射表，可以通过Fn::FindInMap函数来选择Key对应的值，或根据不同的输入参数值作为Key来查找映射表。例如，您可以根据Region不同，自动查找Region-镜像映射表，从而找到适用的镜像。

#### Conditions（可选）

Conditions使用Fn::And、Fn::Or、Fn::Not、Fn::Equals定义条件。多个条件之间使用半角逗号（,）隔开。在创建或更新资源栈时，系统先计算模板中的所有条件，然后再创建资源。创建与true条件关联的所有资源，忽略与false条件关联的所有资源。

#### Resources（可选）

用于详细定义使用该模板创建的资源栈所包含的资源，包括资源间的依赖关系、配置细节等。

#### Outputs（可选）

用于输出一些资源属性等有用信息。可以通过API或控制台获取输出的内容。

#### Rules（可选）

用于检验在创建或更新资源栈时传递给模板的参数值是否符合预期。



### 参数（Parameters）

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/parameters/)

在创建模板时，使用参数（Parameters）可提高模板的灵活性和可复用性。创建资源栈时，可根据实际情况，替换模板中的某些参数值。



#### 概览

**示例：为Web应用创建资源栈**

如果您想通过创建资源栈创建1个Web应用，其中包含1个负载均衡实例、2个ECS实例和1个RDS实例。如果该Web应用负载较高，可以在创建资源栈时选择高配的ECS实例；反之可以在创建资源栈时选择低配的ECS实例。您可以按照如下示例，在模板中定义ECS实例规格参数。

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Parameters:
  InstanceType:
    Type: String
    AllowedValues:
      - ecs.t1.small
      - ecs.s1.medium
      - ecs.m1.medium
      - ecs.c1.large
    Default: ecs.t1.small
    Label: ECS规格类型
    Description: 请选择创建ECS示例的配置，默认为ecs.t1.small，可选ecs.t1.small, ecs.s1.medium, ecs.m1.medium，ecs.c1.large。

Outputs:
  InstanceType:
    Value:
      Ref: InstanceType  
```

示例中，定义的InstanceType参数允许用户在使用模板创建资源栈时，对InstanceType进行重新赋值。如果用户不设置参数值，则使用默认值：ecs.t1.small。



#### `AssociationProperty` 和 `AssociationPropertyMetadata`

当您使用ROS创建资源栈管理多种资源时，通常需要打开多个控制台查找资源参数信息。此时您可以在模板的参数配置中指定AssociationProperty以获取所选地域下对应的资源，指定AssociationPropertyMetadata对不同参数添加筛选条件，以便在控制台动态选择参数配置。



##### `ECS` 资源 - 镜像 `ID`

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/associationproperty-and-associationpropertymetadata#7f1c5f03c1r0d)

示例，根据指定的数据中心过滤自定义镜像

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Parameters:
  # 用户选择数据中心
  TemporaryRegionId:
    Type: String
    AssociationProperty: ALIYUN::ECS::RegionId
  # 根据选择的数据中心过滤自定义镜像
  # https://help.aliyun.com/zh/ros/user-guide/associationproperty-and-associationpropertymetadata#7f1c5f03c1r0d
  TemporaryImageId:
    Type: String
    AssociationProperty: ALIYUN::ECS::Image::ImageId
    AssociationPropertyMetadata:
      RegionId: ${TemporaryRegionId}
      SupportedImageOwnerAlias:
        - self
      Architecture: x86_64
```



### 输出（Outputs）

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/outputs)

在输出（Outputs）中，定义在调用查询资源栈接口时返回的值。例如，定义ECS实例ID的输出，然后可在调用查询资源栈的接口时，查看该实例ID。

示例：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Outputs:
  TemporaryOutput:
    Value:
      Fn::Base64Encode: "Hello world!"
```

- 输出函数 `Fn::Base64Encode` 编码字符串 `Hello world!` 的结果。



### 局部变量（Locals）

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/local-variables-locals)

示例，`String` 数组类型的局部变量：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Locals:
  TemporaryVariable:
    Type: Macro
    Value:
      - e1
      - e2
Outputs:
  TemporaryVariable:
    Value:
      Ref: TemporaryVariable
```



### 函数（Functions）

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/functions/)

#### `Fn::Base64Encode`

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/function-base64encode)

调用内部函数Fn::Base64Encode，返回输入字符串的Base64编码结果。

示例：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Outputs:
  TemporaryOutput:
    Value:
      Fn::Base64Encode: "Hello world!"
```



#### `Fn::Select`

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/function-select)

调用内部函数Fn::Select，通过索引返回列表或字典中的数据。

示例，输出 `String` 数组 `TemporaryVariable` 中索引为 `1` 的元素：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Locals:
  TemporaryVariable:
    Type: Macro
    Value:
      - e1
      - e2
Outputs:
  TemporaryVariable[1]:
    Value:
      Fn::Select:
        - 1
        - Ref: TemporaryVariable
```



#### `Fn::Sub`

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/function-sub)

调用内部函数Fn::Sub，将字符串中的变量（key）的值替换为您指定的值。

示例：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Outputs:
  TemporaryVariable:
    Value:
      Fn::Sub:
        - "Hello ${MyName}"
        - MyName: "Dexter"

```



## 开发参考

### `ECS`

#### 创建专有网络

>[参考官方 `VPC` 文档](https://help.aliyun.com/zh/ros/developer-reference/aliyun-ecs-vpc)
>
>[参考官方 `vSwitch` 文档](https://help.aliyun.com/zh/ros/developer-reference/aliyun-ecs-vswitch)

`ALIYUN::ECS::VPC` 类型用于创建专有网络。

`ALIYUN::ECS::VSwitch` 类型用于创建交换机。

例子1：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Resources:
  TemporaryVpc:
    Type: ALIYUN::ECS::VPC
    Properties:
      # 创建的vpc名称
      VpcName: temporary-vpc
      # vpc支持的网段
      CidrBlock: 10.0.0.0/8
      # 不支持ipv6
      EnableIpv6: false
  TemporaryVSwitch:
    Type: ALIYUN::ECS::VSwitch
    Properties:
      # 要创建交换机的专有网络ID
      VpcId: 
        Ref: TemporaryVpc
      # 可用区ID
      ZoneId: cn-hangzhou-i
      # 交换机名称
      VSwitchName: temporary-vswitch
      # 交换机网段，必须是所属专有网络的子网段，并且没有被其他交换机占用。
      CidrBlock: 10.0.1.0/24
```

- 创建一个网段为 `10.0.0.0/8` 的 `vpc`，有一个网段为 `10.0.1.0/24` 的子网。



#### 创建安全组

>[参考官方 `SecurityGroup` 文档](https://help.aliyun.com/zh/ros/developer-reference/aliyun-ecs-securitygroup)

`ALIYUN::ECS::SecurityGroup` 类型用于创建安全组。

例子1：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Parameters:
  # 让用户选择现有的vpc
  VpcId:
    AssociationProperty: 'ALIYUN::ECS::VPC::VPCId'
    Type: String
    Label:
      zh-cn: 现有VPC
      en: Existing VPC

Resources:
  TemporarySecurityGroup:
    Type: ALIYUN::ECS::SecurityGroup
    Properties:
      # 安全组所属的vpc网络
      VpcId:
        Ref: VpcId
      # 安全组名称
      SecurityGroupName: temporary-security-group
      # 安全组的类型，normal：基本安全组，enterprise：高级安全组
      SecurityGroupType: normal
      # 安全组出方向的访问规则。
      # https://help.aliyun.com/zh/ros/developer-reference/aliyun-ecs-securitygroup?#section-cex-usg-xo8
      SecurityGroupIngress:
        - IpProtocol: tcp
          PortRange: 22/22
          SourceCidrIp: 14.0.0.0/8
        - IpProtocol: tcp
          PortRange: 3389/3389
          SourceCidrIp: 14.0.0.0/8
```





#### 创建实例

>[参考官方 `Instance` 文档](https://help.aliyun.com/zh/ros/developer-reference/aliyun-ecs-instance)

`ALIYUN::ECS::Instance` 类型用于创建 `ECS` 实例。

例子1：

```yaml
ROSTemplateFormatVersion: '2015-09-01'
Description: ''
Parameters: {}
Resources:
  TemporaryInstance:
    Type: ALIYUN::ECS::Instance
    Properties:
      # 指定操作系统的主机名称，hostname命令返回的主机名称
      HostName: temporary-instance
      # 指定阿里云中显示的实例名称
      InstanceName: temporary-instance
      InstanceType: ecs.e-c1m2.large
      # 按量付费
      InstanceChargeType: PostPaid
      # ESSD云盘
      SystemDiskCategory: cloud_essd
      SystemDiskSize: 40

      # 网络按流量计费
      AllocatePublicIP: true
      InternetChargeType: PayByTraffic
      InternetMaxBandwidthOut: 5

      # 不指定vpc和vSwitch，则实例自动选择一个可用区域
      # VpcId: vpc-bp1jn21qnh1iyztyvavke
      # 指定 vSwitchId 相当于指定可用区域
      # VSwitchId: vsw-bp13jk0gf19rk881gjxtd
      # SecurityGroupId: sg-bp1j6d9cfplsxf167u7c

      # 创建实例的镜像id
      ImageId: m-bp1h1ap0xtvx1fqec0bq
Mappings: {}
Metadata: {}
Conditions: {}
Outputs: {}
```

- 注意：在创建实例过程中需要注意实例规格和系统盘类型的兼容性，否则会报告不支持创建此类型实例错误。例如：实例规格 `ecs.e-c1m2.large` 不兼容系统盘类型 `cloud_ssd`，只兼容 `cloud_essd` 类型。



#### 查询镜像资源

>[参考官方文档](https://help.aliyun.com/zh/ros/developer-reference/datasource-ecs-images)

`DATASOURCE::ECS::Images` 类型用于查询可用的镜像资源。

示例，查询 `temporary` 开头的自定义镜像：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Resources:
  TemporaryImages:
    Type: DATASOURCE::ECS::Images
    Properties:
      # 镜像的来源，self：您创建的自定义镜像。
      ImageOwnerAlias: self
      # 镜像的体系架构。x86_64
      Architecture: x86_64
      # 镜像名称。支持使用*。例如：centos_8_5*。
      ImageName: temporary*
Outputs:
  TemporaryImages:
    Value:
      Ref: TemporaryImages
```



#### 批量创建实例

>[参考官方 `Count` 文档](https://help.aliyun.com/zh/ros/user-guide/resources?#section-t78-v96-6km)

示例，创建3个实例：

```yaml
ROSTemplateFormatVersion: '2015-09-01'

Resources:
  TemporaryVpc:
    Type: ALIYUN::ECS::VPC
    Properties:
      # 创建的vpc名称
      VpcName: temporary-vpc
      # vpc支持的网段
      CidrBlock: 10.0.0.0/8
      # 不支持ipv6
      EnableIpv6: false
  TemporaryVSwitch:
    Type: ALIYUN::ECS::VSwitch
    Properties:
      # 要创建交换机的专有网络ID
      VpcId: 
        Ref: TemporaryVpc
      # 可用区ID
      ZoneId: cn-hangzhou-i
      # 交换机名称
      VSwitchName: temporary-vswitch
      # 交换机网段，必须是所属专有网络的子网段，并且没有被其他交换机占用。
      CidrBlock: 10.0.1.0/24
  TemporarySecurityGroup:
    Type: ALIYUN::ECS::SecurityGroup
    Properties:
      # 安全组所属的vpc网络
      VpcId:
        Ref: TemporaryVpc
      # 安全组名称
      SecurityGroupName: temporary-security-group
      # 安全组的类型，normal：基本安全组，enterprise：高级安全组
      SecurityGroupType: normal
      # 安全组出方向的访问规则。
      # https://help.aliyun.com/zh/ros/developer-reference/aliyun-ecs-securitygroup?#section-cex-usg-xo8
      SecurityGroupIngress:
        - IpProtocol: tcp
          PortRange: 22/22
          SourceCidrIp: 14.0.0.0/8
        - IpProtocol: tcp
          PortRange: 3389/3389
          SourceCidrIp: 14.0.0.0/8
  TemporaryImages:
    Type: DATASOURCE::ECS::Images
    Properties:
      # 镜像的来源，self：您创建的自定义镜像。
      ImageOwnerAlias: self
      # 镜像的体系架构。x86_64
      Architecture: x86_64
      # 镜像名称。支持使用*。例如：centos_8_5*。
      ImageName: temporary*
  TemporaryInstance:
    Type: ALIYUN::ECS::Instance
    Count: 3
    Properties:
      # 指定操作系统的主机名称，hostname命令返回的主机名称
      HostName:
        Fn::Sub: "temporary-instance${ALIYUN::Index}"
      # 指定阿里云中显示的实例名称
      InstanceName:
        Fn::Sub: "temporary-instance${ALIYUN::Index}"
      InstanceType: ecs.e-c1m2.large
      # 按量付费
      InstanceChargeType: PostPaid
      # ESSD云盘
      SystemDiskCategory: cloud_essd
      SystemDiskSize: 40

      # 网络按流量计费
      AllocatePublicIP: true
      InternetChargeType: PayByTraffic
      InternetMaxBandwidthOut: 5

      VpcId:
        Ref: TemporaryVpc
      VSwitchId:
        Ref: TemporaryVSwitch
      SecurityGroupId:
        Ref: TemporarySecurityGroup

      # 创建实例的镜像id
      ImageId:
        Fn::Select:
          - 0
          - Ref: TemporaryImages

```



## 更改集

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/change-set-management/)

更改集可以帮助您在资源栈更改生效之前预览更改操作对资源栈的影响，您可以对资源栈创建多个更改集，以达到您预期的效果。

更改集是一种管理和执行模板变更的操作，您可以通过更改集对资源进行管理和更新。更改集可以帮助您校验资源是否能够更新，是否对资源栈产生影响，您可以在执行更改集之前预览更改集操作对资源栈的影响。只有当您执行更改集之后，更改集操作才对资源栈生效。查看更改集对资源栈的具体更改。



### 使用更改集更新资源栈

>[参考官方文档](https://help.aliyun.com/zh/ros/user-guide/create-a-change-set)

更改集可以帮助您在更新资源栈生效前预览该更改操作对资源栈的影响，本文为您介绍如何通过更改集更新资源栈。

如果您想在更新资源之前预览该操作对资源栈的影响，例如更新资源栈是否会删除或者替换关键资源，您可以选择使用更改集更新资源栈。您可以通过创建更改集对目标资源栈进行更改，更改集可以帮助您在资源栈更新生效之前预览更新操作对资源栈的影响，只有在更改集执行成功后，资源栈更新操作才会真正生效。在执行更改集之前您可以对更新资源栈的模板进行检查和修改，以达到您满意的效果。

操作步骤如下：

1. 根据需求编辑原来用于创建资源栈的 `ROS` 模板。
2. 在资源栈列表中 `https://ros.console.aliyun.com/cn-hangzhou/stacks?activeTabKey=changesets` 选中指定的资源栈并切换到 `更改集` 标签页。
3. 点击 `创建更改集` 按钮，选中 `选择已有模板` 和 `输入模板`，手动把编辑后的 `ROS` 模板内容粘贴到 `模板内容` 输入框中，点击 `下一步` 按钮，点击 `预览模板资源` 按钮以查看资源栈更改情况，确认无误后点击 `创建更改集` 按钮以创建新的更改集。
4. 更改集创建成功后点击其后面的 `执行` 按钮即可执行更改集修改操作。
