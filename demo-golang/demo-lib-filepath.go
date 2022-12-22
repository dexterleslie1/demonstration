package main

import (
	"fmt"
	"path/filepath"

	"k8s.io/client-go/util/homedir"
)

// 演示filepath库用法
func main() {
	homeDir := homedir.HomeDir()
	kubeConfigFile := filepath.Join(homeDir, ".kube", "config")
	fmt.Println("kubeConfigFilePath=" + kubeConfigFile)
}