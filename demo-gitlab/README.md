## 说明

> gitlab可以通过gitlab-ctl、gitlab-rails console、gitlab-rails runner、gitlab REST api、gitlab-rake控制gitlab



## 使用docker-compose运行gitlab

> https://docs.gitlab.com/omnibus/docker/

```
# 启动gitlab服务
docker-compose up -d

# 需要等待一段时间比较长时间（NOTE:可能10几分钟）启动后访问和配置gitlab，在Admin Area > Settings设置禁止用户sign-up
http://192.168.1.60

# 如果没有使用环境变量修改root密码，可以通过以下方法手动登录并重置root密码
# 登录帐号: root，密码位于容器内的/etc/gitlab/initial_root_password，NOTE: 如果未启动完毕登录会报告帐号和密码错误，导致root无法成功登录
cat /etc/gitlab/initial_root_password

# 关闭gitlab服务
docker-compose down
```



## gitlab-rails console和runner使用

> https://docs.gitlab.com/ee/administration/operations/rails_console.html



### gitlab-rails console

```
# 进入console
gitlab-rails console

# 打印root用户信息
u = User.find_by_username("root")
pp u.attributes

# 或者一行代码打印
pp User.find_by_username('root').attributes

# 退出console
exit

```



### gitlab-rails runner

```
# 使用runner打印用户
gitlab-rails runner "pp User.find_by_username('root').attributes"

# 或者
gitlab-rails runner "u=User.find_by_username('root');pp u.attributes"
```



### 使用gitlab-rails禁用signup

> https://gitlab.com/gitlab-org/omnibus-gitlab/-/issues/2837
> https://docs.gitlab.com/ee/api/settings.html

```
# 禁用注册功能
gitlab-rails runner "ApplicationSetting.last.update_attribute(:signup_enabled, false)"

# 禁用admin审核注册功能
gitlab-rails runner "ApplicationSetting.last.update_attribute(:require_admin_approval_after_user_signup, false)"
```



### 使用gitlab-rails创建和回收access token

> https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html

```
# 创建token
gitlab-rails runner "token = User.find_by_username('root').personal_access_tokens.create(scopes: ['admin_mode','api'], name: 'Automation token', expires_at: 365.days.from_now); token.set_token('token-string-here123456'); token.save!"

# 回收token
gitlab-rails runner "PersonalAccessToken.find_by_token('token-string-here123456').revoke!"
```



## gitlab REST api调用

> https://docs.gitlab.com/ee/api/rest/



### 基础: 提供token、HTTP响应调试、HTTP失败退出

> https://docs.gitlab.com/ee/api/rest/

```
# HTTP响应调试，使用--include参数
curl --include http://192.168.1.181/api/v4/projects

# HTTP失败退出
curl --fail http://192.168.1.181/api/v4/does-not-exist

# 提供token
gitlab-rails runner "token = User.find_by_username('root').personal_access_tokens.create(scopes: ['admin_mode','api'], name: 'My Token', expires_at: 365.days.from_now); token.set_token('token-string-here12345678'); token.save!"

curl --header "Authorization: Bearer token-string-here12345678" http://192.168.1.181/api/v4/projects
```



### 项目相关api

> https://docs.gitlab.com/ee/api/projects.html

```
# 查询所有项目
curl --fail --header "Authorization: Bearer token-string-here12345678" -X GET http://192.168.1.181/api/v4/projects

# 创建项目
curl --fail --header "Authorization: Bearer token-string-here12345678" \
     --header "Content-Type: application/json" --data '{
        "name": "demo-devops", "description": "用于协助演示devops流程项目",
        "namespace": "root", "initialize_with_readme": "true", "visibility": "public"}' \
     -X POST http://192.168.1.181/api/v4/projects
     
```

