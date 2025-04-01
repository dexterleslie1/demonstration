package main

import (
	"fmt"
	"testing"
	"time"
)

func TestDatetime(t *testing.T) {
	//region 时间转换

	// 把时间对象转换为yyyy-MM-dd HH:mm:ss
	// https://stackoverflow.com/questions/20234104/how-to-format-current-time-using-a-yyyymmddhhmmss-format
	fmt.Println("当前时间(yyyy-MM-dd HH:mm:ss): " + time.Now().Format("2006-01-02 15:04:05"))

	timeStr := "07:15:00"
	timeInstance1, err := time.Parse("15:04:05", timeStr)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	formatTimeStr := timeInstance1.Format("15:04:05")
	if formatTimeStr != timeStr {
		t.Fatalf("expected %s, got %s", timeStr, formatTimeStr)
	}

	//endregion

	//region 时间比较和加减

	// 判断当前时间是否07:15:00之前
	timeInstance1, err = time.Parse("15:04:05", timeStr)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	formatTimeStr = "07:14:59"
	timeNow, err := time.Parse("15:04:05", formatTimeStr)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	fmt.Printf("时间 %s 在 %s 之前？ %t\n", timeNow.Format("15:04:05"), timeStr, timeNow.Before(timeInstance1))

	// 当前时间减去一天
	fmt.Println("当前时间的前一天: " + time.Now().AddDate(0, 0, -1).Format("2006-01-02 15:04:05"))

	//endregion
}
