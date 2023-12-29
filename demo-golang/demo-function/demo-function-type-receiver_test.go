package main

import (
	"fmt"
	"net/http"
	"testing"
)

// https://pgillich.medium.com/method-declaration-with-function-receiver-in-golang-7f5531ded97d
func TestDemoFunctionReceiver(t *testing.T) {
	var handler authHandlerValidatorInterface
	// 使用handleRequest1作为参数构造myAuthHandlerValidator实例
	handler = myAuthHandlerValidator(handleRequest1)
	http.HandleFunc("/1", authNeeded(handler))
	http.HandleFunc("/2", authNeeded(myAuthHandlerValidator(handleRequest2)))
	fmt.Println(http.ListenAndServe(":8080", nil))
}

type authHandlerValidatorInterface interface {
	getHandler() func(writer http.ResponseWriter, request *http.Request)
	getHeaderKey() string
	isValid(token string) bool
}

// 函数类型
type myAuthHandlerValidator func(writer http.ResponseWriter, request *http.Request)

// 函数类型receiver实现getHandler接口并且返回自身作为函数返回值
func (handler myAuthHandlerValidator) getHandler() func(writer http.ResponseWriter, request *http.Request) {
	return handler
}

func (myAuthHandlerValidator) getHeaderKey() string {
	return "Authorization"
}
func (myAuthHandlerValidator) isValid(token string) bool {
	return true
}

// 使用装饰模式添加请求处理前的授权认证流程
func authNeeded(handler authHandlerValidatorInterface) http.HandlerFunc {
	return func(writer http.ResponseWriter, request *http.Request) {
		if !handler.isValid(request.Header.Get(handler.getHeaderKey())) {
			writer.WriteHeader(http.StatusUnauthorized)
			return
		}

		handler.getHandler()(writer, request)
	}
}

// 专注于编写处理逻辑
func handleRequest1(writer http.ResponseWriter, request *http.Request) {
	_, _ = writer.Write([]byte("handle request 1 biz logic"))
	writer.WriteHeader(http.StatusOK)
}

func handleRequest2(writer http.ResponseWriter, request *http.Request) {
	_, _ = writer.Write([]byte("handle request 2 biz logic"))
	writer.WriteHeader(http.StatusOK)
}
