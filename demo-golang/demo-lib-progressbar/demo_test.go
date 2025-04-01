package main

import (
	"io"
	"net/http"
	"os"
	"testing"
	"time"

	"github.com/schollz/progressbar/v3"
)

// https://github.com/schollz/progressbar

func TestBasicUsage(t *testing.T) {
	bar := progressbar.Default(100)
	for i := 0; i < 100; i++ {
		bar.Add(1)
		time.Sleep(40 * time.Millisecond)
	}
}

func TestDownloadProgress(t *testing.T) {
	req, _ := http.NewRequest("GET", "https://bucketxyh.oss-cn-hongkong.aliyuncs.com/jdk/jdk-11.0.19_linux-x64_bin.rpm", nil)
	resp, _ := http.DefaultClient.Do(req)
	defer resp.Body.Close()

	f, _ := os.OpenFile("/tmp/1.rpm", os.O_CREATE|os.O_WRONLY, 0644)
	defer f.Close()

	bar := progressbar.DefaultBytes(
		resp.ContentLength,
		"downloading",
	)
	io.Copy(io.MultiWriter(f, bar), resp.Body)
}
