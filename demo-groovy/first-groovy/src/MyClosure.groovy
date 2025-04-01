//// 闭包基础
//def myClosure = {
//    println "Hello Groovy"
//}
//// 调用方式1
//myClosure.call()
//// 调用方式2
//myClosure()

//// 带参数闭包
//def myClosure = {
//    String name ->
//        println("Hello ${name}")
//}
//myClosure "Groovy!"

//// 多个参数闭包
//def myClosure = {
//    String name, int age ->
//        println "My name is ${name} and age is ${age}"
//}
//myClosure("Dexterleslie", 18)

//// 返回值
//def myClosure = {
//    String name ->
//        return "Hello ${name}"
//}
//def result = myClosure "Dexterleslie"
//println result

//// 闭包作为参数
//def myClosure = {
//    a, Closure c ->
//        return a*3 + " " + c("test")
//}
//// NOTE：大括号为闭包做为参数传递写法(常用写法)
//def result = myClosure(3) {
//    message ->
//        return "message: " + message
//}
//println result

//// 字符串与闭包使用
//def str = "2 plus 3 is 5"
//def result = str.find {
//    s -> return s.isNumber()
//}
//println result