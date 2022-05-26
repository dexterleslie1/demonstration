# 演示mybatis-plus使用

# 运行demo

```shell script
# 编译镜像
sh build.sh

# 运行容器
docker-compose up -d

# 运行单元测试

```

## mybatis-plus官方文档
https://mp.baomidou.com/guide/quick-start.html#%E5%88%9D%E5%A7%8B%E5%8C%96%E5%B7%A5%E7%A8%8B

## mybatis-plus.configuration.map-underscore-to-camel-case开启下划线转化为驼峰
https://www.cnblogs.com/zhaixingzhu/p/12731664.html
https://blog.csdn.net/weixin_43314519/article/details/109351688
为true时：java bean驼峰转换为数据库字段下划线，例如：clientId转换为client_id
为false时：不作出任何转换

## mybatis-plus执行自定义SQL
https://cloud.tencent.com/developer/article/1531517

## 指定entity id auto_increment类型
https://blog.csdn.net/lzhcoder/article/details/112860695