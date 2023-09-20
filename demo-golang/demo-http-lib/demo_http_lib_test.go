package main

import (
	"fmt"
	"io"
	"log"
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
