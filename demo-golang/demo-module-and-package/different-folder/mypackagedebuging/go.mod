module main

go 1.17

// 指定mypackage模块所在项目的本地路径
replace mypackage1 => ../mypackage

require mypackage1 v0.0.0
