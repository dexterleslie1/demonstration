const { Person } = require("my-package")

let person = new Person('Dexterleslie', 31)
person.display()

let myPackage = require('my-package')

let a = 1
let b = 5
console.log(`${a}+${b}=${myPackage.add(a, b)}`)

let myAdd = require('my-package').add
a = 5
b = 9
console.log(`${a}+${b}=${myAdd(a, b)}`)

let { add } = require('my-package')
a = 8
b = 9
console.log(`${a}+${b}=${add(a, b)}`)

const HelloMessageObject = require('my-package').HelloMessage

console.log(HelloMessageObject)

const { HelloMessage } = require('my-package')
console.log(HelloMessage)