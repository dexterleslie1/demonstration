package demo_lib_gomega

import (
	"errors"
	"github.com/onsi/gomega"
	"testing"
)

// https://github.com/onsi/gomega
// https://onsi.github.io/gomega/
func TestDemo(t *testing.T) {
	g := gomega.NewWithT(t)
	b := true
	g.Expect(b).To(gomega.BeTrue())

	// error测试
	g.Expect(myFun1()).To(gomega.Succeed())
	g.Expect(myFun1()).NotTo(gomega.HaveOccurred())
	g.Expect(myFun2()).ToNot(gomega.Succeed())
}

func myFun1() error {
	return nil
}
func myFun2() error {
	return errors.New("测试错误")
}
