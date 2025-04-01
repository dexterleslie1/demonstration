package main

import (
	"fmt"
	"os"
)

// 获取windows系统的SystemRoot环境变量
// https://stackoverflow.com/questions/41254463/how-to-get-system-root-directory-for-windows-in-google-golang
func main() {
	// 获取系统跟路径，例如：c:\Windows\
	var systemRoot = os.Getenv("SYSTEMROOT") + string(os.PathSeparator)
	fmt.Println("SystemRoot=" + systemRoot)

	// 获取系统盘盘符，例如：c:\
	var systemDrive = os.Getenv("SYSTEMDRIVE") + string(os.PathSeparator)
	fmt.Println("SystemDrive=" + systemDrive)
}