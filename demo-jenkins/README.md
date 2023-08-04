## 使用docker-compose运行jenkins

> https://hub.docker.com/r/jenkins/jenkins
> https://github.com/jenkinsci/docker/blob/master/README.md

```
# 启动容器
docker-compose up -d

# 修改jenkins插件下载地址指向国内清华镜像
# NOTE：必需要等待jenkins启动完毕生成/var/jenkins_home/updates目录后才能执行替换命令，有时候需要等待稍长时间jenkins才会自动生成此目录
# NOTE: 目前发现使用外国源下载插件也很快，暂时不需要下面配置
# sed -i 's/http:\/\/updates.jenkins-ci.org\/download/https:\/\/mirrors.tuna.tsinghua.edu.cn\/jenkins/g' /var/jenkins_home/updates/default.json && sed -i 's/http:\/\/www.google.com/https:\/\/www.baidu.com/g' /var/jenkins_home/updates/default.json

# 登录jenkins进行配置
http://192.168.1.181:8080

# 初始登录密码位于容器内的文件/var/jenkins_home/secrets/initialAdminPassword
cat /var/jenkins_home/secrets/initialAdminPassword

# 设置 jenkins url 为 http://192.168.1.181:8080/，NOTE: 不要设置为 http://localhost:8080，否则在运行agent.jar时候报错说agent不能连接jenkins master
# 可以初始化完毕jenkins后设置 jenkins url， Manage Jenkins > Config System > Jenkins URL =http://192.168.1.181:8080/

# 选择默认推荐的插件等待安装完成

# 创建admin用户并设置密码

# 关闭容器
docker-compose down
```



## 设置jenkins slave

### 设置windows slave

```
# 在任何虚拟机中使用浏览器访问 http://192.168.1.181:8080/ 并新增一个节点
# Manage Jenkins > Manage Nodes and Clouds > New Node，配置如下:
Name: windows-slave
Remote root directory: c:\jenkins
Labels: windows-slave
Launch method: Launch agent by connecting it to controller
Custom WorkDir path: c:\jenkins_workdir
其他采用默认值

# 使用远程桌面登录windows slave服务器并安装jdk11
# 使用浏览器访问 http://192.168.1.181:8080/ 后，点击刚刚新增的节点(offline状态)
# 点击浏览器中的链接下载agent.jar到windows slave本地并根据提示运行agent.jar
# 稍等一会后刚刚新增的节点就变为online状态

# 新建一个pipeline测试windows slave节点是否正常，脚本如下:
pipeline {
    agent {
        label 'windows-slave'
    }

    stages {
        stage('步骤1') {
            steps {
                bat 'systeminfo'
            }
        }
        
        stage('步骤2') {
            steps {
                echo '步骤2输出'
            }
        }
    }
}
```



### 设置centOS8 slave

```
# 新建一台centOS8虚拟机并使用dcli安装jdk8和docker

# 使用浏览器访问 http://192.168.1.181:8080 jenkins并使用功能 Manage Jenkins > Manage Nodes and Clouds > New Node 新增节点，参数如下:
Name: centos8-slave
Remote root directory: /data/jenkins
Labels: centos8-slave
Launch method: Launch agents via SSH
Host: 192.168.1.xxx
Credentials: 选择新增到jenkins的帐号密码
Host Key Verification Strategy: Non verifying Verifcation Strategy
其他采用默认值

# 点击保存后jenkins master会通过SSH自动配置centOS8的agent，稍等一会后centos8-slave就会online状态

# 新建pipeline测试centOS8 slave是否正常，脚本如下:
pipeline {
    agent {
        label 'centos8-slave'
    }

    stages {
        stage('步骤1') {
            steps {
                sh 'docker --version'
                sh 'docker-compose --version'
            }
        }
    }
}
```



## pipeline用法



### pipeline入门

```
# 登录jenkins控制台创建一个pipeline项目名为 testpipeline

# 在 testpipeline 项目pipeline配置中选择 "Pipeline script"，脚本如下:
pipeline {
    agent any

    stages {
        stage('步骤1') {
            steps {
                echo '步骤1输出'
            }
        }
        
        stage('步骤2') {
            steps {
                echo '步骤2输出'
            }
        }
    }
}

# 保存脚本后点击 testpipeline 项目 "Build Now"

# 查看项目Status就能够看到build状态
```



### 指定agent执行

```
# 指定windows-slave agent执行脚本
pipeline {
    agent {
        // 指定windows-slave执行脚本
        label 'windows-slave'
    }

    stages {
        stage('步骤1') {
            steps {
                bat 'systeminfo'
            }
        }
        
        stage('步骤2') {
            steps {
                echo '步骤2输出'
            }
        }
    }
}
```



### 不同的步骤在不同的agent中执行

> https://stackoverflow.com/questions/42652533/limiting-jenkins-pipeline-to-running-only-on-specific-nodes

```
# windows-slave节点执行systeminfo命令
# centos8-slave节点执行 docker --version、docker-compose --version命令
# 脚本如下:
pipeline {
    // 这个agent不能删除，否则报告缺失agent导致语法错误
    agent any
    
    stages {
        stage('windows执行步骤') {
            agent {
                label 'windows-slave'
            }
        
            steps {
                bat 'systeminfo'
            }
        }
        
        stage('centos8执行步骤') {
            agent {
                label 'centos8-slave'
            }
        
            steps {
                sh 'docker --version'
                sh 'docker-compose --version'
            }
        }
    }
}
```



### 在不同agent间使用stash和unstash传输文件

> https://stackoverflow.com/questions/43050248/correct-usage-of-stash-unstash-into-a-different-directory

```
# 传输windows-slave当前工作目录c:\jenkins\workspace\testpipeline中的test目录到centos8-slave当前工作目录/data/jenkins/workspace/testpipeline中
# 提前在windows-slave c:\jenkins\workspace\testpipeline中创建test目录并放置数据到其中
# 脚本如下:
pipeline {
    // 这个agent不能删除，否则报告缺失agent导致语法错误
    agent any
    
    stages {
        stage('windows执行步骤') {
            agent {
                label 'windows-slave'
            }
        
            steps {
                bat 'dir'
                stash includes: 'test/**/*', name: 'my-archive'
            }
        }
        
        stage('centos8执行步骤') {
            agent {
                label 'centos8-slave'
            }
        
            steps {
                sh 'pwd'
                sh 'rm -rf test'
                unstash 'my-archive'
            }
        }
    }
}
```



### windows平台使用bat函数运行CMD命令

> https://code-maven.com/jenkins-pipeline-running-external-programs

```
# 脚本如下:
pipeline {
    agent {
        // 指定windows-slave执行脚本
        label 'windows-slave'
    }

    stages {
        stage('步骤1') {
            steps {
                // 使用bat函数运行cmd命令
                bat 'systeminfo'
            }
        }
        
        stage('步骤2') {
            steps {
                echo '步骤2输出'
            }
        }
    }
}
```

