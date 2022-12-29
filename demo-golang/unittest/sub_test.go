package main

import "testing"

func TestSub(t *testing.T) {
	result := Sub(2, 1)
	if result != 1 {
		t.Fatalf("预期结果:%v,实际结果:%v", 1, result)
	}

	t.Logf("测试成功")
}
