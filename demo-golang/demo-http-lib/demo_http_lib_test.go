package main

import (
	"bytes"
	"crypto/rand"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"mime/multipart"
	"net/http"
	"net/url"
	"os"
	"strings"
	"testing"
)

// 测试下载文件
func TestDownloadFile(t *testing.T) {
	// https://golangdocs.com/golang-download-files

	// 使用net/http库下载文件
	fullURLFile := "https://bucketxyh.oss-cn-hongkong.aliyuncs.com/kubernetes/kubectl-v1.26.0"

	// Build fileName from fullPath
	fileURL, err := url.Parse(fullURLFile)
	if err != nil {
		log.Fatal(err)
	}
	path := fileURL.Path
	segments := strings.Split(path, "/")
	fileName := segments[len(segments)-1]

	// Create blank file
	file, err := os.Create(fileName)
	if err != nil {
		log.Fatal(err)
	}
	client := http.Client{
		CheckRedirect: func(r *http.Request, via []*http.Request) error {
			r.URL.Opaque = r.URL.Path
			return nil
		},
	}
	// Put content on file
	resp, err := client.Get(fullURLFile)
	if err != nil {
		log.Fatal(err)
	}
	defer resp.Body.Close()

	size, err := io.Copy(file, resp.Body)

	defer file.Close()

	resultStr := fmt.Sprintf("Downloaded a file %s with size %d", fileName, size)
	if "Downloaded a file kubectl-v1.26.0 with size 309" != resultStr {
		t.Fatalf("没有预期值")
	}
}

// 测试获取http响应头
func TestGetResponseHeaderValue(t *testing.T) {
	url := "https://bucketxyh.oss-cn-hongkong.aliyuncs.com/kubernetes/kubectl-v1.26.0-linux"
	client := http.Client{
		CheckRedirect: func(r *http.Request, via []*http.Request) error {
			r.URL.Opaque = r.URL.Path
			return nil
		},
	}

	resp, err := client.Head(url)
	if err != nil {
		t.Fatalf("%s", err)
	}
	defer resp.Body.Close()

	resultStr := resp.Header.Get("ETag")
	if "\"FE8CA6F9EF6DEC468DBFD0A6E1C9D63C\"" != resultStr {
		t.Fatalf("没有预期值")
	}
}

// 演示http下载文件
// https://stackoverflow.com/questions/11692860/how-can-i-efficiently-download-a-large-file-using-go
func TestDownloadLargeFile(t *testing.T) {
	// Create the file
	var filepath = "/tmp/1.txt"
	out, err := os.Create(filepath)
	if err != nil {
		t.Fatalf("%s", err)
	}
	defer out.Close()

	// Get the data
	var url = "https://www.baidu.com"
	resp, err := http.Get(url)
	if err != nil {
		t.Fatalf("%s", err)
	}
	defer resp.Body.Close()

	// Check server response
	if resp.StatusCode != http.StatusOK {
		err = fmt.Errorf("bad status: %s", resp.Status)
		t.Fatalf("%s", err)
	}

	// Writer the body to file
	_, err = io.Copy(out, resp.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}
}

const (
	Host = "localhost"
	Port = 8080
)

// 测试http get方法
// https://pkg.go.dev/net/http
func TestGetMethod(t *testing.T) {
	client := &http.Client{}
	url := fmt.Sprintf("http://%s:%d/api/v1/testGetSubmitParamByUrl1?param1=%s", Host, Port, "v1")
	request, err := http.NewRequest("GET", url, nil)
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err := client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err := io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	url = fmt.Sprintf("http://%s:%d/api/v1/testGetSubmitParamByUrl2?param1=%s", Host, Port, "v1")
	request, err = http.NewRequest("GET", url, nil)
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}
}

// 测试http delete方法
func TestDeleteMethod(t *testing.T) {
	client := &http.Client{}
	myUrl := fmt.Sprintf("http://%s:%d/api/v1/testDeleteSubmitParamByUrl1?param1=%s", Host, Port, "v1")
	request, err := http.NewRequest("DELETE", myUrl, nil)
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err := client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err := io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testDeleteSubmitParamByUrl2?param1=%s", Host, Port, "v1")
	request, err = http.NewRequest("DELETE", myUrl, nil)
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	params := make(map[string]string)
	params["param1"] = "v1"
	datum, err = json.Marshal(params)
	if err != nil {
		t.Fatalf("%s", err)
	}
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testDeleteSubmitParamByJSON", Host, Port)
	request, err = http.NewRequest("DELETE", myUrl, bytes.NewReader(datum))
	if err != nil {
		t.Fatalf("%s", err)
	}
	request.Header.Add("Content-Type", "application/json")
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}
}

// 测试http post方法
func TestPostMethod(t *testing.T) {
	client := &http.Client{}
	myUrl := fmt.Sprintf("http://%s:%d/api/v1/testPostSubmitParamByUrl1?param1=%s", Host, Port, "v1")
	request, err := http.NewRequest("POST", myUrl, nil)
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err := client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err := io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	// https://golangbyexample.com/http-client-urlencoded-body-go/
	values := url.Values{}
	values.Set("param1", "v1")
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostSubmitParamByFormUrlencoded1", Host, Port)
	request, err = http.NewRequest("POST", myUrl, strings.NewReader(values.Encode()))
	if err != nil {
		t.Fatalf("%s", err)
	}
	request.Header.Add("Content-Type", "application/x-www-form-urlencoded")
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	values = url.Values{}
	values.Set("param1", "v1")
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostSubmitParamByFormUrlencoded2", Host, Port)
	request, err = http.NewRequest("POST", myUrl, strings.NewReader(values.Encode()))
	if err != nil {
		t.Fatalf("%s", err)
	}
	request.Header.Add("Content-Type", "application/x-www-form-urlencoded")
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	// 提交form-data请求
	// https://pkg.go.dev/net/http
	values = url.Values{}
	values.Set("param1", "v1")
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostSubmitParamByMultipartFormData", Host, Port)
	response, err = http.PostForm(myUrl, values)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	values = url.Values{}
	values.Set("param1", "v1")
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostSubmitParamByMultipartFormData2", Host, Port)
	response, err = http.PostForm(myUrl, values)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	params := make(map[string]string)
	params["param1"] = "v1"
	datum, err = json.Marshal(params)
	if err != nil {
		t.Fatalf("%s", err)
	}
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostSubmitParamByJSON", Host, Port)
	request, err = http.NewRequest("POST", myUrl, bytes.NewReader(datum))
	if err != nil {
		t.Fatalf("%s", err)
	}
	request.Header.Add("Content-Type", "application/json")
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostAndResponseWithString?name=%s", Host, Port, "Dexter")
	request, err = http.NewRequest("POST", myUrl, bytes.NewReader(datum))
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "{\"dataObject\":\"你好，Dexter\"}" != string(datum) {
		t.Fatalf("非预期值")
	}

	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostAndResponseWithJSONObject?name=%s", Host, Port, "Dexter")
	request, err = http.NewRequest("POST", myUrl, bytes.NewReader(datum))
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "{\"dataObject\":{\"greeting\":\"你好，Dexter\"}}" != string(datum) {
		t.Fatalf("非预期值")
	}

	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostAndResponseWithException?name=%s", Host, Port, "Dexter")
	request, err = http.NewRequest("POST", myUrl, bytes.NewReader(datum))
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "{\"errorMessage\":\"测试预期异常是否出现\",\"errorCode\":50000}" != string(datum) {
		t.Fatalf("非预期值")
	}

	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPostAndResponseWithJSONArray?name=%s", Host, Port, "dexter")
	request, err = http.NewRequest("POST", myUrl, bytes.NewReader(datum))
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "{\"dataObject\":[\"你好，dexter#0\",\"你好，dexter#1\",\"你好，dexter#2\",\"你好，dexter#3\",\"你好，dexter#4\",\"你好，dexter#5\",\"你好，dexter#6\",\"你好，dexter#7\",\"你好，dexter#8\",\"你好，dexter#9\"]}" != string(datum) {
		t.Fatalf("非预期值")
	}

	//#region 演示上传文件

	// 准备临时文件
	randomBytes := make([]byte, 5)
	_, err = rand.Read(randomBytes)
	if err != nil {
		t.Fatalf("rand.Read调用失败，原因: %s", err)
	}
	tempDirectory := os.TempDir()
	tempFile := tempDirectory + "/.1.bin"
	err = os.WriteFile(tempFile, randomBytes, 0755)
	if err != nil {
		t.Fatalf("写入文件%s失败，原因: %s", tempFile, err)
	}

	// 上传文件
	// https://gist.github.com/SLonger/34030e57fc49ff74f62c1b1b2f591ff0

	// 创建request
	file, err := os.Open(tempFile)
	if err != nil {
		t.Fatalf("调用op.Open(%s)失败，原因: %s", tempFile, err)
	}
	defer file.Close()

	body := &bytes.Buffer{}
	writer := multipart.NewWriter(body)
	part, err := writer.CreateFormFile("file1", "test.bin")
	if err != nil {
		t.Fatalf("调用writer.CreateFormFile(\"file\", \"test.bin\")失败，原因: %s", err)
	}
	_, err = io.Copy(part, file)
	params = make(map[string]string)
	for k, v := range params {
		_ = writer.WriteField(k, v)
	}
	err = writer.Close()
	if err != nil {
		t.Fatalf("调用writer.Close()失败，原因: %s", err)
	}

	// 发送请求
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/upload?name=%s", Host, Port, "dexter")
	request, err = http.NewRequest("POST", myUrl, body)
	if err != nil {
		t.Fatalf("调用http.NewRequest(\"POST\", \"%s\", body)", myUrl)
	}
	request.Header.Set("Content-Type", writer.FormDataContentType())
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("调用client.Do(request)失败，原因: %s", err)
	}

	// 读取响应
	body = &bytes.Buffer{}
	_, err = body.ReadFrom(response.Body)
	if err != nil {
		t.Fatalf("调用body.ReadFrom失败，原因: %s", err)
	}
	response.Body.Close()
	if "{\"file\":\"test.bin\",\"name\":\"你好，dexter\"}" != string(body.Bytes()) {
		t.Fatalf("上传文件响应%s和预期不相符合", string(body.Bytes()))
	}

	// 下载文件对比字节
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/download?filename=%s", Host, Port, "test.bin")
	request, err = http.NewRequest("POST", myUrl, nil)
	if err != nil {
		t.Fatalf("调用http.NewRequest失败，原因: %s", err)
	}
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("下载文件失败url: %s", myUrl)
	}

	tempBody := &bytes.Buffer{}
	_, err = tempBody.ReadFrom(response.Body)
	if err != nil {
		t.Fatalf("调用body.ReadFrom失败，原因: %s", err)
	}

	defer response.Body.Close()

	if !bytes.Equal(randomBytes, tempBody.Bytes()) {
		t.Fatalf("上传和下载的文件二进制内容不匹配")
	}

	//#endregion

}

// #region 测试Put方法
func TestPutMethod(t *testing.T) {
	client := &http.Client{}
	myUrl := fmt.Sprintf("http://%s:%d/api/v1/testPutSubmitParamByUrl1?param1=%s", Host, Port, "v1")
	request, err := http.NewRequest("PUT", myUrl, nil)
	if err != nil {
		t.Fatalf("%s", err)
	}
	response, err := client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err := io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	values := &url.Values{}
	values.Set("param1", "v1")
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPutSubmitParamByFormUrlencoded1", Host, Port)
	request, err = http.NewRequest("PUT", myUrl, strings.NewReader(values.Encode()))
	if err != nil {
		t.Fatalf("%s", err)
	}
	request.Header.Add("Content-Type", "application/x-www-form-urlencoded")
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	values = &url.Values{}
	values.Set("param1", "v1")
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPutSubmitParamByFormUrlencoded2", Host, Port)
	request, err = http.NewRequest("PUT", myUrl, strings.NewReader(values.Encode()))
	if err != nil {
		t.Fatalf("%s", err)
	}
	request.Header.Add("Content-Type", "application/x-www-form-urlencoded")
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}

	params := make(map[string]string)
	params["param1"] = "v1"
	datum, err = json.Marshal(params)
	if err != nil {
		t.Fatalf("调用json.Marshal失败，原因: %s", err)
	}
	myUrl = fmt.Sprintf("http://%s:%d/api/v1/testPutSubmitParamByJSON", Host, Port)
	request, err = http.NewRequest("PUT", myUrl, bytes.NewReader(datum))
	if err != nil {
		t.Fatalf("%s", err)
	}
	request.Header.Add("Content-Type", "application/json")
	response, err = client.Do(request)
	if err != nil {
		t.Fatalf("%s", err)
	}

	defer response.Body.Close()

	datum, err = io.ReadAll(response.Body)
	if err != nil {
		t.Fatalf("%s", err)
	}

	if "提交参数param1=v1" != string(datum) {
		t.Fatalf("非预期值")
	}
}

//#endregion
