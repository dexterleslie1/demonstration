package main

import (
	"embed"
	_ "embed"
	"fmt"
	"log"
)

// Embed the file content as string.
//
//go:embed 1.txt
var textFileContent string

// Embed the entire directory.
//
//go:embed templates
var templatesFolder embed.FS

//go:embed static
var staticFolder embed.FS

//go:embed static/2.txt
var static2TextFileContent string

func main() {
	// https://harsimranmaan.medium.com/embedding-static-files-in-a-go-binary-using-go-embed-bac505f3cb9a
	fmt.Println("1.txt文件内容如下: ")
	fmt.Println(textFileContent)
	fmt.Println("--------------------------------------------------------")
	fmt.Println("")

	fmt.Println("static/2.txt文件内容如下: ")
	fmt.Println(static2TextFileContent)
	fmt.Println("--------------------------------------------------------")
	fmt.Println("")

	// embed的文件可以使用相对路径读取
	fileData, err := templatesFolder.ReadFile("templates/index.html.tmpl")
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("templates/index.html.tmpl文件内容如下: ")
	fmt.Println(string(fileData))
	fmt.Println("--------------------------------------------------------")
	fmt.Println("")

	fileData, err = staticFolder.ReadFile("static/2.txt")
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("static/2.txt文件内容如下: ")
	fmt.Println(string(fileData))
	fmt.Println("--------------------------------------------------------")
	fmt.Println("")
}
