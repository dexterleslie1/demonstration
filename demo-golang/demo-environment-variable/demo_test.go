package main

import (
	"fmt"
	"os"
	"testing"
)

func TestDemo(t *testing.T) {
	home := os.Getenv("HOME")
	homeDrive := os.Getenv("HOMEDRIVE")
	homePath := os.Getenv("HOMEPATH")
	userProfile := os.Getenv("USERPROFILE")
	fmt.Println("home=" + home + ",homeDrive=" + homeDrive + ",homePath=" + homePath + ",userProfile=" + userProfile)
}
