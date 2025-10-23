## 访问控制`RAM` - 概念

>[什么是访问控制（RAM）_访问控制(RAM)-阿里云帮助中心](https://help.aliyun.com/zh/ram/product-overview/what-is-ram)

访问控制RAM（Resource Access Management）是阿里云提供的管理用户身份与资源访问权限的服务。



## 访问控制`RAM` - `STS`概念

>[什么是STS_访问控制(RAM)-阿里云帮助中心](https://help.aliyun.com/zh/ram/product-overview/what-is-sts)

阿里云STS（Security Token Service）是阿里云提供的一种临时访问权限管理服务。RAM提供RAM用户和RAM角色两种身份。其中，RAM角色不具备永久身份凭证，而只能通过STS获取可以自定义时效和访问权限的临时身份凭证，即安全令牌（STS Token）。



## 创建`RAM`用户

>[创建RAM用户_访问控制(RAM)-阿里云帮助中心](https://help.aliyun.com/zh/ram/user-guide/create-a-ram-user)

您可以在阿里云账号（主账号）下创建RAM用户并为其授权，实现不同RAM用户拥有不同资源访问权限的目的。

为了账号安全，建议您只选择以下访问方式中的一种，将人员用户和应用程序用户分离，避免混用。

- **控制台访问**

  如果RAM用户代表人员，建议启用控制台访问，使用用户名和登录密码访问阿里云。您需要设置以下参数：

  - **设置密码**：设置登录控制台的密码，您可以选择自动生成密码或者自定义密码。自定义登录密码时，密码必须满足密码复杂度规则。更多信息，请参见[设置RAM用户密码策略](https://help.aliyun.com/zh/ram/user-guide/configure-a-password-policy-for-ram-users#task-188785)。
  - **需要重置密码**：选择RAM用户在下次登录时是否需要重置密码。
  - **MFA多因素认证**：选择是否为当前RAM用户启用MFA。启用MFA后，还需要绑定MFA设备。更多信息，请参见[为RAM用户绑定MFA设备](https://help.aliyun.com/zh/ram/user-guide/bind-an-mfa-device-to-a-ram-user#task-268585)。

- **使用永久AccessKey访问**

  如果RAM用户代表应用程序，您可以使用永久访问密钥（AccessKey）访问阿里云。启用后，系统会自动为RAM用户生成一个AccessKey ID和AccessKey Secret。更多信息，请参见[创建AccessKey](https://help.aliyun.com/zh/ram/user-guide/create-an-accesskey-pair#task-2245479)。



## 为`RAM`用户授权

>[为RAM用户授权_访问控制(RAM)-阿里云帮助中心](https://help.aliyun.com/zh/ram/user-guide/grant-permissions-to-the-ram-user)

为RAM用户授予RAM的系统策略或自定义策略后，RAM用户就能以策略中对应的权限访问阿里云资源。建议您遵循最小化原则，按需授予RAM用户必要的权限。



## 查看`RAM`用户的权限

>[如何查看RAM用户的权限_访问控制(RAM)-阿里云帮助中心](https://help.aliyun.com/zh/ram/user-guide/view-the-permissions-of-a-ram-user)

本文为您介绍如何查看RAM用户的权限，包括查看RAM用户的个人权限和查看RAM用户继承用户组的权限。



## 阿里云`SDK` - 管理访问凭证

>[通过配置Credentials实现OpenAPI安全调用（JAVA SDK）_阿里云SDK(Alibaba Cloud SDK)-阿里云帮助中心](https://help.aliyun.com/zh/sdk/developer-reference/v2-manage-access-credentials)

在应用开发中使用**环境变量**方式配置访问凭证：通过`ALIBABA_CLOUD_ACCESS_KEY_ID`和`ALIBABA_CLOUD_ACCESS_KEY_SECRET`配置环境变量。

### 默认凭据链

>[通过配置Credentials实现OpenAPI安全调用（JAVA SDK）_阿里云SDK(Alibaba Cloud SDK)-阿里云帮助中心](https://help.aliyun.com/zh/sdk/developer-reference/v2-manage-access-credentials#b031e67396a5e)