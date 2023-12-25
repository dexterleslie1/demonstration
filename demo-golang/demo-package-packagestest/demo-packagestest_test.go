package demo_package_packagestest

import (
	"golang.org/x/tools/go/packages/packagestest"
	"os/exec"
	"testing"
)

func TestDemoPackagestest(t *testing.T) {
	// TestGoList exercises the 'go list' command in module mode and in GOPATH mode.
	packagestest.TestAll(t, testGoList)
}

func testGoList(t *testing.T, x packagestest.Exporter) {
	// 创建模块到/tmp目录
	e := packagestest.Export(t, x, []packagestest.Module{
		{
			Name: "gopher.example/repoa",
			Files: map[string]interface{}{
				"a/a.go": "package a",
			},
		},
		{
			Name: "gopher.example/repob",
			Files: map[string]interface{}{
				"b/b.go": "package b",
			},
		},
	})
	defer e.Cleanup()

	t.Logf("config dir: %s", e.Config.Dir)

	cmd := exec.Command("go", "list", "gopher.example/...")
	cmd.Dir = e.Config.Dir
	cmd.Env = e.Config.Env
	out, err := cmd.Output()
	if err != nil {
		t.Fatal(err)
	}
	t.Logf("'go list gopher.example/...' with %s mode layout:\n%s", x.Name(), out)
}
