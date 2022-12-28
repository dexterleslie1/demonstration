package main

import (
	_ "database/sql"
	// f "fmt"
	. "fmt"
)

func main() {
	// import的.和 _以及别名
	// https://blog.csdn.net/qq_29667149/article/details/124630183

	// 导入fmt包是使用 . 别名，代表调用包内函数不需要写包前缀
	// import . "fmt"
	Println("Hello world!")

	// 使用 import f "fmt" 别名导入包
	// import f "fmt"
	// f.Println("Hello world!f")
}
