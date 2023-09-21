// 演示数据类型byte array用法
package main

import (
	"crypto/rand"
	"testing"
)

func TestByteArray(t *testing.T) {
	//#region 演示随机生成byte array

	// https://www.practical-go-lessons.com/post/how-to-generate-random-bytes-with-golang-ccc9755gflds70ubqc2g
	buf := make([]byte, 1024*1024)
	length, err := rand.Read(buf)
	if err != nil {
		t.Fatalf("rand.Read调用失败，原因: %s", err)
	}
	if length != len(buf) {
		t.Fatalf("rand.Read调用失败，原因: length != len(buf)")
	}

	//#endregion
}
