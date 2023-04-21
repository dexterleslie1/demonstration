package main

import (
	"fmt"
	"io"
	"log"
	"net/http"
	"net/url"
	"os"
	"strings"
)

func TestDownfile() {
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

	fmt.Printf("Downloaded a file %s with size %d", fileName, size)
}

func TestGetResponseHeaderValue() (string, error) {
	url := "https://bucketxyh.oss-cn-hongkong.aliyuncs.com/kubernetes/kubectl-v1.26.0-linux"
	client := http.Client{
		CheckRedirect: func(r *http.Request, via []*http.Request) error {
			r.URL.Opaque = r.URL.Path
			return nil
		},
	}

	resp, err := client.Head(url)
	if err != nil {
		// log.Fatal(err)
		return "", err
	}
	defer resp.Body.Close()

	return resp.Header.Get("ETag"), nil
}

func main() {
	headerValue, err := TestGetResponseHeaderValue()
	if err != nil {
		panic(err)
	}
	fmt.Println("eTag=", headerValue)
}
