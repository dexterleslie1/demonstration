package mypackage

type student struct {
	Name string
	Age int
}

// 因为student小写，外面的包不能直接创建这个结构体
// 所以需要借助工厂模式创建此实例
func NewStudent(name string, age int) *student {
	return &student{name, age}
}