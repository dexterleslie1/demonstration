package main

import "testing"

func TestAdd(t *testing.T) {
	result := Add(1, 2)
	if result != 3 {
		t.Fatalf("预期结果:%v,实际结果:%v", 3, result)
	}

	t.Logf("测试成功")
}
