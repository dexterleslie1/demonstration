import _ from 'lodash'

/*
remove演示
*/
// 删除指定index
let array = [1, 2, 3]
_.remove(array, function(value, index, array) {
	if(index==1)
		return true
})
console.log(`_.remove index=1元素后数组 ${array}`)

/*
indexOf
NOTE: indexOf不能用于JSON对象类型，需要使用findIndex方法
*/
array = [1, 2, 3]
let element = 2
let index = _.indexOf(array, element)
console.log(`值=2 index=${index}`)

/*
findIndex
*/
array = [{
	userId: 1,
	nickname: 'n1'
}, {
	userId: 2,
	nickname: 'n2'
}, {
	userId: 3,
	nickname: 'n3'
}]
index = _.findIndex(array, function(o) {
	if(o.userId==2)
		return true
	return false
})
console.log(`数值中userId=2的元素index=${index}`)

/*
insert
*/
// 指定index插入元素
array = [1, 3]
array.splice(1, 0, 2)
console.log(`使用splice在index=1插入元素后数组 ${array}`)
