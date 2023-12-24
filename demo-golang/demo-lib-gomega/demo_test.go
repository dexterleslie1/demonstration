package demo_lib_gomega

import (
	"github.com/onsi/gomega"
	"testing"
)

// https://github.com/onsi/gomega
// https://onsi.github.io/gomega/
func TestDemo(t *testing.T) {
	g := gomega.NewWithT(t)
	b := true
	g.Expect(b).To(gomega.BeTrue())
}
