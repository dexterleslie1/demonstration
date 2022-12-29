package main

import (
	"encoding/json"
	"fmt"
)

type Student struct {
	StudentId      string `json:"sid"`
	StudentName    string `json:"sname"`
	StudentClass   string `json:"class"`
	StudentTeacher string `json:"teacher"`
}

type StudentNoJson struct {
	StudentId      string
	StudentName    string
	StudentClass   string
	StudentTeacher string
}

type StudentWithOption struct {
	StudentId      string
	StudentName    string `json:"sname"`
	StudentClass   string `json:"class,omitempty"`
	StudentTeacher string `json:"-"`
}

// 演示struct json tag用法
// http://t.zoukankan.com/fengxm-p-9917686.html
func main() {
	student1 := &Student{
		StudentId:      "1",
		StudentName:    "xiaoming",
		StudentClass:   "0903",
		StudentTeacher: "feng",
	}
	JSON, _ := json.Marshal(student1)
	fmt.Println("student1 JSON=", string(JSON))

	student2 := new(Student)
	json.Unmarshal(JSON, student2)
	fmt.Println("student2=", student2)

	student3 := &StudentNoJson{
		StudentId:      "1",
		StudentName:    "xiaoming",
		StudentClass:   "0903",
		StudentTeacher: "feng",
	}
	JSON, _ = json.Marshal(student3)
	fmt.Println("student3=", string(JSON))

	student4 := new(StudentWithOption)
	JSON, _ = json.Marshal(student4)
	fmt.Println("student4=", string(JSON))

	student5 := &StudentWithOption{
		StudentId:      "1",
		StudentName:    "xiaoming",
		StudentClass:   "0903",
		StudentTeacher: "feng",
	}
	JSON, _ = json.Marshal(student5)
	fmt.Println("student5=", string(JSON))
}
