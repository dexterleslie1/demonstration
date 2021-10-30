const moment = require('moment')

let formatStr = 'YYYY-MM-DD HH:mm:ss'

let dateStr = moment().format(formatStr)
console.log(`当前moment时间对象转换为YYYY-MM-DD HH:mm:ss字符串结果：${dateStr}`)

let dateObject = moment().toDate()
console.log(`当前moment时间对象转换为Date对象结果：${dateObject}`)

let timeNow = new Date()
dateStr = moment(timeNow).format(formatStr)
console.log(`new Date()对象使用moment转换日期字符串结果：${dateStr}`)

dateObject = moment(dateStr, formatStr).toDate()
console.log(`日期字符串使用moment转换为Date对象结果：${dateObject}`)

let momentObject = moment(dateStr, formatStr)
momentObject.add(2,'seconds')
console.log(`原来时间${dateStr}加2秒后结果：${momentObject.format(formatStr)}`)

momentObject = moment(dateStr, formatStr)
momentObject.add(2,'minutes')
console.log(`原来时间${dateStr}加2分钟后结果：${momentObject.format(formatStr)}`)

momentObjectOrigin = moment(dateStr, formatStr)
momentObject = moment(dateStr, formatStr)
momentObject.add(3600, "seconds")
interval = momentObject.diff(momentObjectOrigin, "seconds")
console.log(`时间${momentObject.format(formatStr)}和${momentObjectOrigin.format(formatStr)}相差${interval}秒`)

interval = momentObject.diff(momentObjectOrigin, "minutes")
console.log(`时间${momentObject.format(formatStr)}和${momentObjectOrigin.format(formatStr)}相差${interval}分钟`)

interval = momentObject.diff(momentObjectOrigin, "hours")
console.log(`时间${momentObject.format(formatStr)}和${momentObjectOrigin.format(formatStr)}相差${interval}小时`)