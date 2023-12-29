package demo_lib_ginkgo_test

import (
	"fmt"
	"testing"

	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"
)

// 这是测试套件的入口函数
func TestDemoLibGinkgo(t *testing.T) {
	// 连接gomega和ginkgo
	RegisterFailHandler(Fail)
	RunSpecs(t, "DemoLibGinkgo Suite")
}

var _ = BeforeSuite(func() {
	fmt.Println("before suite method called")
})

var _ = AfterSuite(func() {
	fmt.Println("after suite method called")
})
