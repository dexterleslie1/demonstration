package demo_lib_ginkgo

import (
	"fmt"
	"github.com/onsi/ginkgo/v2"
)

var _ = ginkgo.Describe("Demo testing", func() {
	ginkgo.It("testing it", func() {
		fmt.Println("+++++88888888")
	})
})
