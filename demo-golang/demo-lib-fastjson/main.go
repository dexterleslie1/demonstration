package main

import (
	"encoding/json"
	"fmt"
	"log"

	"github.com/valyala/fastjson"
)

// https://github.com/valyala/fastjson
// https://pkg.go.dev/github.com/valyala/fastjson#pkg-examples
func main() {
	// 读取数据
	b1 := []byte(`{"foo": [123, "bar"]}`)
	fmt.Printf("foo.0=%d\n", fastjson.GetInt(b1, "foo", "0"))

	var p fastjson.Parser
	v, err := p.Parse(`{
                "str": "bar",
                "int": 123,
                "float": 1.23,
                "bool": true,
                "arr": [1, "foo", {}]
        }`)
	if err != nil {
		log.Fatal(err)
	}
	fmt.Printf("foo=%s\n", v.GetStringBytes("str"))
	fmt.Printf("int=%d\n", v.GetInt("int"))
	fmt.Printf("float=%f\n", v.GetFloat64("float"))
	fmt.Printf("bool=%v\n", v.GetBool("bool"))
	fmt.Printf("arr.1=%s\n", v.GetStringBytes("arr", "1"))

	// 判断key是否存在
	data := []byte(`{"foo": [1.23,{"bar":33,"baz":null}]}`)

	fmt.Printf("exists(data.foo) = %v\n", fastjson.Exists(data, "foo"))
	fmt.Printf("exists(data.foo[0]) = %v\n", fastjson.Exists(data, "foo", "0"))
	fmt.Printf("exists(data.foo[1].baz) = %v\n", fastjson.Exists(data, "foo", "1", "baz"))
	fmt.Printf("exists(data.foobar) = %v\n", fastjson.Exists(data, "foobar"))
	fmt.Printf("exists(data.foo.bar) = %v\n", fastjson.Exists(data, "foo", "bar"))

	// 设置值
	v = fastjson.MustParse(`{"foo":1,"bar":[2,3]}`)

	// Replace `foo` value with "xyz"
	v.Set("foo", fastjson.MustParse(`"xyz"`))
	// Add "newv":123
	v.Set("newv", fastjson.MustParse(`123`))
	fmt.Printf("%s\n", v)

	// Replace `bar.1` with {"x":"y"}
	v.Get("bar").Set("1", fastjson.MustParse(`{"x":"y"}`))
	// Add `bar.3="qwe"
	v.Get("bar").Set("3", fastjson.MustParse(`"qwe"`))
	fmt.Printf("%s\n", v)

	// 遍历所有keys
	s := `{
		"obj": { "foo": 1234 },
		"arr": [ 23,4, "bar" ],
		"str": "foobar"
	}`

	v, err = p.Parse(s)
	if err != nil {
		log.Fatalf("cannot parse json: %s", err)
	}
	o, err := v.Object()
	if err != nil {
		log.Fatalf("cannot obtain object from json value: %s", err)
	}

	o.Visit(func(k []byte, v *fastjson.Value) {
		switch string(k) {
		case "obj":
			fmt.Printf("object %s\n", v)
		case "arr":
			fmt.Printf("array %s\n", v)
		case "str":
			fmt.Printf("string %s\n", v)
		}
	})

	// 转换JSON为 pretty格式
	// https://stackoverflow.com/questions/19038598/how-can-i-pretty-print-json-using-go
	var obj map[string]interface{}
	json.Unmarshal([]byte(fmt.Sprintf("%s", v)), &obj)
	b, err := json.MarshalIndent(obj, "", "  ")
	fmt.Println(string(b))

}
