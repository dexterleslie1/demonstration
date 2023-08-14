## 使用docker-compose运行jenkins

> https://hub.docker.com/r/jenkins/jenkins
> https://github.com/jenkinsci/docker/blob/master/README.md

```
# 编译容器
docker-compose build

# 启动容器，NOTE: 此脚本会启动jenkins、gitlab、harbor等服务用于测试
sh start.sh

# NOTE: 等待一段比较长的时间等待gitlab启动完毕，项目会自动配置jenkins、gitlab

# 登录jenkins进行配置
http://localhost:50001
# 登录gitlab
http://localhost:50002

# 关闭容器jekins、gitlab、harbor服务
sh stop.sh
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



### pipeline语法

> pipeline分为声明式和脚本式语法，NOTE: 推荐使用声明式
> https://www.jenkins.io/doc/book/pipeline/syntax/
> https://zhuanlan.zhihu.com/p/133703916
>
> 可以使用pipeline语法生成器帮助生成语法
> http://localhost:50001/pipeline-syntax/



#### 声明式语法

> 以pipeline {} 开头

```
pipeline {
    // 这个agent不能删除，否则报告缺失agent导致语法错误
    agent any
    
    stages {
        stage('执行步骤') {
            steps {
                sh 'pwd'
            }
        }
    }
}

# 在声明式语法中使用script块嵌入脚本式语法
pipeline {
    agent any
    
    stages {
        stage('测试') {
            steps {
                script {
                    def my_var = 'Hello world!'
                    println(my_var)
                }
            }
        }
    }
}
```



#### 脚本式语法

```
node {
    stage('编译') {
        sh 'uname -a'
    }
    
    stage('发布') {
        sh 'date'
    }
}
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

### pipeline基本steps

> https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/#dir-change-current-directory

#### pwd step

> https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/#pwd-determine-current-directory
> 获取当前工作目录绝对路径

```
pipeline {
    agent any
    
    stages {
        stage('测试') {
            steps {
                // https://stackoverflow.com/questions/46733278/is-it-possible-to-concatenate-string-with-job-parameter-in-pipeline-script
                println "当前目录: ${pwd()}"
            }
        }
    }
}
```

#### dir step

> 切换工作目录

```
pipeline {
    agent any
    
    stages {
        stage('测试') {
            steps {
                sh 'mkdir -p test-temp'
                dir('test-temp') {
                    println "当前目录: ${pwd()}"
                }
            }
        }
    }
}
```



#### sh step

> https://stackoverflow.com/questions/48630765/jenkins-pipeline-sh-adding-new-line

```
# 基本使用
pipeline {
    agent any
    
    stages {
        stage('测试') {
            steps {
                sh 'ls'
            }
        }
    }
}

# 长命令换行
# https://stackoverflow.com/questions/48630765/jenkins-pipeline-sh-adding-new-line
pipeline {
    agent any
    
    stages {
        stage('测试') {
            steps {
                sh """
                	ls;
                	pwd;
                	date;
                """
            }
        }
    }
}
```



#### writeFile step

> https://stackoverflow.com/questions/51233919/create-a-file-with-some-content-using-groovy-in-jenkins-pipeline

```
# 基本使用
pipeline {
    agent any
    
    stages {
        stage('测试') {
            steps {
                writeFile file:'1.txt', text: '123'
                sh 'ls; cat 1.txt;'
            }
        }
    }
}

# writeFile换行
pipeline {
    agent any
    
    stages {
        stage('测试') {
            steps {
                writeFile file:'1.txt', text: '1\r\n2\r\n3\r\n'
                sh 'pwd; ls; cat 1.txt;'
            }
        }
    }
}
```



### pipeline插件



#### git插件

> https://www.jenkins.io/doc/pipeline/steps/git/

```
pipeline {
    agent any
    
    stages {
        stage('测试') {
            steps {
                sh 'rm -rf *'
                sh 'mkdir test-temp'
                dir('test-temp') {
                    // 可以借助语法生成器生成脚本
                    git branch: 'main', changelog: false, credentialsId: 'git-access-token', url: 'http://demo-gitlab-server/root/test.git'
                    sh 'pwd && ls'
                }
                
                sh 'pwd && ls'
            }
        }
    }
}
```



#### docker插件

> 使用sh start.sh启动服务后会自动启动一个harbor服务url: http://ip:50003
> NOTE: 因为调试docker插件需要已安装docker环境，所以需要配置centos8-slave
> https://docs.cloudbees.com/docs/cloudbees-ci/latest/pipelines/docker-workflow
> https://www.jenkins.io/doc/book/pipeline/docker/

```
# 综合测试
pipeline {
    agent any
    
    stages {
        stage('测试') {
            agent {
                label 'centos8-slave'
            }
            steps {
                script {
                    def my_image = docker.image('hello-world')
                    // 拉取hello-world
                    my_image.pull()
                    
                    // 运行容器
                    // https://stackoverflow.com/questions/62533437/running-a-docker-container-from-a-jenkins-pipeline-and-capture-the-output
                    //my_docker.run()
                    
                    // 给hello-world打标签
                    // sh 'docker tag hello-world 192.168.1.181:50003/library/hello-world:1.0.1'
                    
                    // https://www.jenkins.io/doc/book/pipeline/docker/#custom-registry
                    // https://stackoverflow.com/questions/49029379/use-private-docker-registry-with-authentication-in-jenkinsfile
                    docker.withRegistry('https://192.168.1.181:50003', 'harbor-token') {
                        def my_image_1 = docker.image('library/hello-world')
                        my_image_1.push('1.0.0')
                    }
                    
                }
            }
        }
    }
}

# 测试根据Dockerfile build镜像
pipeline {
    agent any
    
    stages {
        stage('测试') {
            agent {
                label 'centos8-slave'
            }
            steps {
                writeFile file: 'Dockerfile', text: 'FROM busybox\r\nRUN echo \'123\' > /1.txt'
                
                script {
                    // 编译镜像
                    // 使用命令测试镜像docker run --rm my_busybox_image:1.0.0 cat /1.txt
                    docker.build('my_busybox_image:1.0.0')
                }
            }
        }
    }
}
```



## 创建git仓库中拉取项目并执行其中的Jenkinsfile

> 演示从git仓库中下载源代码并执行其中的Jenkinsfile

```
# 运行demo
sh start.sh

# 登录jenkins手动触发构建测试Jenkinsfile是否正确
```

