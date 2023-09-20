package main

import (
	"encoding/json"
	"testing"
)

type Student struct {
	// 表示序列化的JSON key指定别名 sid、sname等
	StudentId      string `json:"sid"`
	StudentName    string `json:"sname"`
	StudentClass   string `json:"class"`
	StudentTeacher string `json:"teacher"`
}

type StudentNoJson struct {
	// 表示使用默认的JSON配置，JSON key别名沿用原来的属性名称 StudentId、StudentName等
	StudentId      string
	StudentName    string
	StudentClass   string
	StudentTeacher string
}

type StudentWithOption struct {
	StudentId   string
	StudentName string `json:"sname"`
	// 表示在JSON序列化时候如果字符串为空则不会序列化到JSON中
	StudentClass string `json:"class,omitempty"`
	// json:"-"表示属性不会被序列化到JSON中
	StudentTeacher string `json:"-"`
}

// 演示struct json tag用法
// http://t.zoukankan.com/fengxm-p-9917686.html
func TestJson(t *testing.T) {
	student1 := &Student{
		StudentId:      "1",
		StudentName:    "xiaoming",
		StudentClass:   "0903",
		StudentTeacher: "feng",
	}
	JSON, _ := json.Marshal(student1)
	if "{\"sid\":\"1\",\"sname\":\"xiaoming\",\"class\":\"0903\",\"teacher\":\"feng\"}" != string(JSON) {
		t.Fatalf("没有预期值")
	}

	student2 := new(Student)
	json.Unmarshal(JSON, student2)
	if student1.StudentId != student2.StudentId {
		t.Fatalf("没有预期值")
	}
	if student1.StudentName != student2.StudentName {
		t.Fatalf("没有预期值")
	}
	if student1.StudentTeacher != student2.StudentTeacher {
		t.Fatalf("没有预期值")
	}
	if student1.StudentClass != student2.StudentClass {
		t.Fatalf("没有预期值")
	}

	student3 := &StudentNoJson{
		StudentId:      "1",
		StudentName:    "xiaoming",
		StudentClass:   "0903",
		StudentTeacher: "feng",
	}
	JSON, _ = json.Marshal(student3)
	if "{\"StudentId\":\"1\",\"StudentName\":\"xiaoming\",\"StudentClass\":\"0903\",\"StudentTeacher\":\"feng\"}" != string(JSON) {
		t.Fatalf("没有预期值")
	}

	student4 := new(StudentWithOption)
	JSON, _ = json.Marshal(student4)
	if "{\"StudentId\":\"\",\"sname\":\"\"}" != string(JSON) {
		t.Fatalf("没有预期值")
	}

	student5 := &StudentWithOption{
		StudentId:      "1",
		StudentName:    "xiaoming",
		StudentClass:   "0903",
		StudentTeacher: "feng",
	}
	JSON, _ = json.Marshal(student5)
	if "{\"StudentId\":\"1\",\"sname\":\"xiaoming\",\"class\":\"0903\"}" != string(JSON) {
		t.Fatalf("没有预期值")
	}
}
