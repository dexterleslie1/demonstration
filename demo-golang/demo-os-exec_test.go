package main

import (
	"bufio"
	"fmt"
	"log"
	"os/exec"
	"sync"
	"testing"
)

func TestLookPath(t *testing.T) {
	// exec.LookPath文档
	// https://pkg.go.dev/os/exec#LookPath
	path, err := exec.LookPath("java")
	if err != nil {
		panic(err)
	}
	fmt.Println("which java结果:", path)
}

func TestExecCommand(t *testing.T) {
	// 执行命令
	out, err := exec.Command("date").CombinedOutput()
	if err != nil {
		log.Fatal(err, " ", string(out))
	} else {
		fmt.Println("date命令执行结果:", string(out))
	}
}

func TestExecProgress(t *testing.T) {
	// 显示进度的exec
	// https: //stackoverflow.com/questions/65625672/get-output-of-os-exec-to-to-show-progress-executed-command
	cmd := exec.Command("./demo-os-exec-scripts.sh")

	stdout, err := cmd.StdoutPipe()
	if err != nil {
		log.Fatal("获取stdout pipe错误，原因: ", err)
	}
	stderr, err := cmd.StderrPipe()
	if err != nil {
		log.Fatal("获取stderr pipe错误，原因: ", err)
	}
	err = cmd.Start()
	if err != nil {
		log.Fatal("调用cmd.Start()错误，原因: ", err)
	}

	waitGroup := sync.WaitGroup{}
	waitGroup.Add(1)

	go func() {
		scanner := bufio.NewScanner(stderr)
		for scanner.Scan() {
			m := scanner.Text()
			fmt.Println(m)
		}

		waitGroup.Done()
	}()

	scanner := bufio.NewScanner(stdout)
	for scanner.Scan() {
		m := scanner.Text()
		fmt.Println(m)
	}

	waitGroup.Wait()

	err = cmd.Wait()
	if err != nil {
		log.Fatal("调用cmd.Wait()错误，原因: ", err)
	}
}

func TestExecYumProgress(t *testing.T) {
	//#region 演示exec yum进度效果

	_, err := exec.LookPath("git")
	if err == nil {
		// 如果git命令存在则删除
		cmd := exec.Command("yum", "remove", "git", "-y")

		stdout, err := cmd.StdoutPipe()
		if err != nil {
			log.Fatal("获取stdout pipe错误，原因: ", err)
		}
		stderr, err := cmd.StderrPipe()
		if err != nil {
			log.Fatal("获取stderr pipe错误，原因: ", err)
		}
		err = cmd.Start()
		if err != nil {
			log.Fatal("调用cmd.Start()错误，原因: ", err)
		}

		waitGroup := sync.WaitGroup{}
		waitGroup.Add(1)

		go func() {
			scanner := bufio.NewScanner(stderr)
			for scanner.Scan() {
				m := scanner.Text()
				fmt.Println(m)
			}

			waitGroup.Done()
		}()

		scanner := bufio.NewScanner(stdout)
		for scanner.Scan() {
			m := scanner.Text()
			fmt.Println(m)
		}

		waitGroup.Wait()

		err = cmd.Wait()
		if err != nil {
			log.Fatal("调用cmd.Wait()错误，原因: ", err)
		}
	}

	// 安装git命令
	cmd := exec.Command("yum", "install", "git", "-y")

	stdout, err := cmd.StdoutPipe()
	if err != nil {
		log.Fatal("获取stdout pipe错误，原因: ", err)
	}
	stderr, err := cmd.StderrPipe()
	if err != nil {
		log.Fatal("获取stderr pipe错误，原因: ", err)
	}
	err = cmd.Start()
	if err != nil {
		log.Fatal("调用cmd.Start()错误，原因: ", err)
	}

	waitGroup := sync.WaitGroup{}
	waitGroup.Add(1)

	go func() {
		scanner := bufio.NewScanner(stderr)
		for scanner.Scan() {
			m := scanner.Text()
			fmt.Println(m)
		}

		waitGroup.Done()
	}()

	scanner := bufio.NewScanner(stdout)
	for scanner.Scan() {
		m := scanner.Text()
		fmt.Println(m)
	}

	waitGroup.Wait()

	err = cmd.Wait()
	if err != nil {
		log.Fatal("调用cmd.Wait()错误，原因: ", err)
	}

	//#endregion
}

// 执行带有管道的命令
// https://stackoverflow.com/questions/10781516/how-to-pipe-several-commands-in-go
func TestExecPipe(t *testing.T) {
	out, err := exec.Command("bash", "-c", "cat /etc/passwd|grep root").CombinedOutput()
	if err != nil {
		log.Fatal(err, " ", string(out))
	} else {
		fmt.Println("命令执行结果:", string(out))
	}
}

// 在ubuntu系统中解压文件到/usr/local会提示permission denided错误，需要使用下面方法读取stdout和stderr
func TestUbuntuPermissionDenided(t *testing.T) {
	cmd := exec.Command("bash", "-c", "tar -xvzf /tmp/ideaIU-2023.2.5.tar.gz -C /usr/local/")

	stdout, err := cmd.StdoutPipe()
	if err != nil {
		log.Fatal("获取stdout pipe错误，原因: ", err)
	}
	stderr, err := cmd.StderrPipe()
	if err != nil {
		log.Fatal("获取stderr pipe错误，原因: ", err)
	}
	err = cmd.Start()
	if err != nil {
		log.Fatal("调用cmd.Start()错误，原因: ", err)
	}

	waitGroup := sync.WaitGroup{}
	waitGroup.Add(1)

	go func() {
		scanner := bufio.NewScanner(stderr)
		for scanner.Scan() {
			m := scanner.Text()
			fmt.Println(m)
		}

		waitGroup.Done()
	}()

	scanner := bufio.NewScanner(stdout)
	for scanner.Scan() {
		m := scanner.Text()
		fmt.Println(m)
	}

	waitGroup.Wait()

	err = cmd.Wait()
	if err != nil {
		log.Fatal("调用cmd.Wait()错误，原因: ", err)
	}
}
