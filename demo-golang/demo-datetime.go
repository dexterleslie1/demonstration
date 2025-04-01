package main

import (
	"fmt"
	"time"
)

func main() {
	// 获取当前系统时间
	// https://blog.csdn.net/skh2015java/article/details/70051512
	startTime := time.Now()
	// 演示Sleep
	// https://blog.csdn.net/weixin_44014995/article/details/113836091
	// time.Duration(2)*time.Second解析: 因为time.Sleep单位是Duration，1 Duration=1 纳秒，time.Duration(2)*time.Second=2秒
	time.Sleep(time.Duration(2) * time.Second)
	endTime := time.Now()
	// 获取两个时间差
	// https://blog.csdn.net/HYZX_9987/article/details/99947688
	fmt.Println("休眠耗时:", endTime.Sub(startTime))
}
