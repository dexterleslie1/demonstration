# 引用第三方依赖

## 运行

```shell
# 生成go.mod文件
MacOSdeMBP:demo-reference-3party-module macos$ go mod init main
go: creating new go.mod: module main
go: to add module requirements and sums:
        go mod tidy
# 自动推导项目依赖并在go.mod文件中自动添加require
MacOSdeMBP:demo-reference-3party-module macos$ go mod tidy
go: finding module for package github.com/gin-gonic/gin
go: downloading github.com/gin-gonic/gin v1.8.1
go: found github.com/gin-gonic/gin in github.com/gin-gonic/gin v1.8.1
go: downloading github.com/gin-contrib/sse v0.1.0
go: downloading golang.org/x/net v0.0.0-20210226172049-e18ecbb05110
go: downloading github.com/stretchr/testify v1.7.1
go: downloading google.golang.org/protobuf v1.28.0
go: downloading github.com/goccy/go-json v0.9.7
go: downloading github.com/json-iterator/go v1.1.12
go: downloading github.com/ugorji/go/codec v1.2.7
go: downloading github.com/pelletier/go-toml/v2 v2.0.1
go: downloading github.com/go-playground/validator/v10 v10.10.0
go: downloading golang.org/x/sys v0.0.0-20210806184541-e5e7981a1069
go: downloading gopkg.in/yaml.v3 v3.0.0-20210107192922-496545a6307b
go: downloading github.com/pmezard/go-difflib v1.0.0
go: downloading github.com/davecgh/go-spew v1.1.1
go: downloading github.com/modern-go/concurrent v0.0.0-20180228061459-e0a39a4cb421
go: downloading github.com/modern-go/reflect2 v1.0.2
go: downloading github.com/go-playground/universal-translator v0.18.0
go: downloading github.com/leodido/go-urn v1.2.1
go: downloading golang.org/x/crypto v0.0.0-20210711020723-a769d52b0f97
go: downloading golang.org/x/text v0.3.6
go: downloading github.com/go-playground/locales v0.14.0
go: downloading github.com/go-playground/assert/v2 v2.0.1
go: downloading github.com/google/go-cmp v0.5.5
go: downloading github.com/kr/pretty v0.3.0
go: downloading golang.org/x/xerrors v0.0.0-20191204190536-9bdfabe68543
go: downloading github.com/rogpeppe/go-internal v1.8.0
go: downloading github.com/kr/text v0.2.0
go: downloading github.com/ugorji/go v1.2.7
# 运行main.go
MacOSdeMBP:demo-reference-3party-module macos$ go run main.go 
```

