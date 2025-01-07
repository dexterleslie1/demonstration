package com.future.demo.openfeign;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

import java.io.File;
import java.util.List;

public interface Api {
    @RequestLine("GET /api/v1/test1?name={name}")
    ObjectResponse<MyVO> test1(@Param("name") String name) throws BusinessException;

    @RequestLine("POST /api/v1/testPost")
    // 因为body提交json参数，所以需要指定请求头Content-Type: application/json
    @Headers(value = {"Content-Type: application/json"})
    ObjectResponse<String> testPost(List<MyPostVO> myPostVOList) throws BusinessException;

    @RequestLine("POST /api/v1/postWwwFormUrlencoded")
    @Headers(value = {"Content-Type: application/x-www-form-urlencoded"})
    ObjectResponse<String> postWwwFormUrlencoded(@Param("parameter1") String parameter1) throws BusinessException;

    @RequestLine("PUT {uri}")
    // 因为body提交json参数，所以需要指定请求头Content-Type: application/json
    @Headers(value = {"Content-Type: application/json"})
    ObjectResponse<String> testPut(@Param("uri") String uri,
                                   List<MyPostVO> myPostVOList) throws BusinessException;

    @RequestLine("GET /api/v1/testHeaderWithToken")
    ObjectResponse<String> testHeaderWithToken() throws BusinessException;

    /**
     * Using @Headers with dynamic values in Feign client + Spring Cloud (Brixton RC2)
     * https://stackoverflow.com/questions/37066331/using-headers-with-dynamic-values-in-feign-client-spring-cloud-brixton-rc2
     *
     * @param dynamicToken
     * @return
     */
    @Headers("dynamicToken: {dynamicToken}")
    @RequestLine("GET /api/v1/testHeaderWithDynamicToken")
    ObjectResponse<String> testHeaderWithDynamicToken(@Param("dynamicToken") String dynamicToken);

    @RequestLine("GET /api/v1/testResponseWithHttpStatus400")
    ObjectResponse<String> testResponseWithHttpStatus400() throws BusinessException;

    /**
     * spring + Open Feign upload file 文件上传
     * https://blog.csdn.net/wtopps/article/details/78191953
     *
     * @param file
     * @return
     */
    @RequestLine("POST /api/v1/upload")
    @Headers("Content-Type: multipart/form-data")
    ObjectResponse<String> upload(@Param("file") File file);

    /**
     * feign for downloading file
     * https://stackoverflow.com/questions/59765206/feign-for-downloading-file
     * @param filename
     * @return
     */
    @RequestLine("GET /api/v1/download/{filename}")
    Response download(@Param("filename") String filename);

    @RequestLine("DELETE /api/v1/delete?param1={param1}")
    ObjectResponse<String> delete(@Param("param1") String param1) throws BusinessException;
}
