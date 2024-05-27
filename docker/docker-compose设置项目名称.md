# 设置`docker compose`项目名称

> 参考`stackoverflow`[链接](https://stackoverflow.com/questions/44924082/set-project-name-in-docker-compose-file)

1. 通过`docker-compose.yaml`中`name`属性指定项目名称

   - 直接在`docker-compose.yaml`中设置`name`属性

     ```yaml
     version: "3.0"
     
     name: test
     
     services:
       my_busybox:
         image: busybox
         command: |
           sh -c "sleep infinity"
     ```

   - 通过变量的方式设置`name`属性

     `.env`文件如下：

     ```properties
     projectName=test
     ```

     `docker-compose.yaml`文件如下：

     ```yaml
     version: "3.0"
     
     name: ${projectName}
     
     services:
       my_busybox:
         image: busybox
         command: |
           sh -c "sleep infinity"
     ```

     

2. 通过`.env`设置项目名称

   > 推荐使用此方式设置项目名称

   ```properties
   COMPOSE_PROJECT_NAME=mytest
   ```

3. 通过`docker compose`命令`-p`参数设置项目名称

   `up`命令

   ```bash
   docker compose -p mytest1 up -d
   ```

   `ps`命令

   ```bash
   docker compose -p mytest1 ps
   ```

   `down`命令

   ```bash
   docker compose -p mytest1 down -v
   ```

   

