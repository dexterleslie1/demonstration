## gateway 3个核心组件

> **route:**  gateway 中可以配置多个 `Route`。一个 `Route` 由路由 id，转发的 uri，多个 `Predicates` 以及多个 `Filters` 构成。处理请求时会按优先级排序，找到第一个满足所有 `Predicates` 的 Route。
> **predicate:** 表示路由的匹配条件，可以用来匹配请求的各种属性，如请求路径、方法、header 等。一个 `Route` 可以包含多个 `Predicates`，多个 `Predicates` 最终会合并成一个。
> **filter:** 过滤器包括了处理请求和响应的逻辑，可以分为 pre 和 post 两个阶段。多个 `Filter` 在 pre 阶段会按优先级高到低顺序执行，post 阶段则是反向执行。

## gateway执行流程

> https://blog.csdn.net/cnm10050/article/details/127261680
>
> 1. 客户端向 Spring Cloud Gateway 发出请求。
> 2. 如果 Gateway Handler Mapping 找到与请求相匹配(通过predicate匹配)的路由，将其发送到 Gateway Web Handler。
> 3. Handler 再通过指定的 过滤器链(全局和局部filter) 来将请求发送到我们实际的服务执行业务逻辑，然后返回。
> 4. 过滤器之间用虚线分开是因为过滤器可能会在发送代理请求之前（“pre”）或之后（“post”）执行业务逻辑。



## gateway用法

> NOTE: 项目maven引用spring-cloud-starter-gateway后不能引用spring-boot-starter-web

### predicate

> Path、Query、Header等谓词

### filter

> NOTE: predicate不通过是不会调用filter（包括全局、局部filter）的

#### 全局filter

> 使用GlobalFilter定义全局filter
>
> 每种全局 `Filter` 在 gateway 中只会有一个实例，会对所有的 `Route` 都生效。
>
> 
>
> 自定义GlobalFilter作为权限验证
> https://www.pianshen.com/article/4176276100/
> https://www.cnblogs.com/h--d/p/12741935.html

#### 局部filter

> 使用AbstractGatewayFilterFactory定义局部filter
>
> 路由 `Filter` 是针对 `Route` 进行配置的，不同的 `Route` 可以使用不同的参数，因此会创建不同的实例。
>
> NOTE: 自定义局部filter命名规则为XxxGatewayFilterFactory

## gateway security集成和用法

> todo
