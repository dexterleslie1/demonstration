startTime = Date.now()

setTimeout(() => {
 endTime = Date.now()
 console.log("开始时间和当前时间相隔" + (endTime - startTime) + "毫秒")
}, 2000)
