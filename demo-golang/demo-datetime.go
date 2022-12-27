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
	// time.Duration(2)*time.Second解析: 因为time.Sleep单位是Duration，1 Duration=1 纳秒，time.Duration(2)*time.Second=2秒
	time.Sleep(time.Duration(2) * time.Second)
	endTime := time.Now()
	fmt.Println("休眠耗时:", endTime.Sub(startTime))
}
