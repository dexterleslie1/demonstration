# 知识库

``` mermaid
sequenceDiagram  
    participant Client  
    participant KongAPIGateway  
    participant BackendServer1  
    participant BackendServer2  
    Client->>KongAPIGateway: 发送TCP请求  
    KongAPIGateway->>BackendServer1: 转发TCP请求  
    BackendServer1-->>KongAPIGateway: 返回TCP响应  
    KongAPIGateway-->>Client: 将TCP响应返回给客户端  
    alt 其他后端服务器  
        KongAPIGateway->>BackendServer2: 转发TCP请求  
        BackendServer2-->>KongAPIGateway: 返回TCP响应  
        KongAPIGateway-->>Client: 将TCP响应返回给客户端  
    end
```