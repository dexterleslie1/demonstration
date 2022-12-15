package main

import (
	"net/http"
	"io"
	"os"
	"fmt"
)

// 演示http下载文件
// https://stackoverflow.com/questions/11692860/how-can-i-efficiently-download-a-large-file-using-go
func main() {
	// Create the file
	var filepath = "/tmp/1.txt"
	out, err := os.Create(filepath)
	if err != nil  {
		fmt.Println(err)
	  	return
	}
	defer out.Close()
  
	// Get the data
	var url = "https://www.baidu.com"
	resp, err := http.Get(url)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer resp.Body.Close()
  
	// Check server response
	if resp.StatusCode != http.StatusOK {
		err = fmt.Errorf("bad status: %s", resp.Status)
		fmt.Println(err)
		return
	}
  
	// Writer the body to file
	_, err = io.Copy(out, resp.Body)
	if err != nil  {
		fmt.Println(err)
		return
	}
}
