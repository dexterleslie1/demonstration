export default({app, route}) => {
    app.router.beforeEach(function(to, from, next) {
        console.log(`插件式全局前置守卫`)
        next(true)
    })

    app.router.afterEach(function(to, from) {
        console.log(`插件式全局后置守卫`)
    })
}