package main

import (
	"encoding/json"
	"fmt"
	"log"
	"os/user"

	"github.com/zcalusic/sysinfo"
)

// ubuntu20.4
// "os": {
//     "name": "Ubuntu 20.04.3 LTS",
//     "vendor": "ubuntu",
//     "version": "20.04",
//     "release": "20.04.3",
//     "architecture": "amd64"
//   },
//   "kernel": {
//     "release": "5.15.0-46-generic",
//     "version": "#49~20.04.1-Ubuntu SMP Thu Aug 4 19:15:44 UTC 2022",
//     "architecture": "x86_64"
//   },

// centOS6
//  "os": {
//     "architecture": "amd64"
//   },
//   "kernel": {
//     "release": "2.6.32-642.el6.x86_64",
//     "version": "#1 SMP Tue May 10 17:27:01 UTC 2016",
//     "architecture": "x86_64"
//   },

// centOS7
// "os": {
//     "name": "CentOS Linux 7 (Core)",
//     "vendor": "centos",
//     "version": "7",
//     "release": "7.5.1804",
//     "architecture": "amd64"
//   },
//   "kernel": {
//     "release": "3.10.0-862.el7.x86_64",
//     "version": "#1 SMP Fri Apr 20 16:44:24 UTC 2018",
//     "architecture": "x86_64"
//   },

// centOS8
// "os": {
//     "name": "CentOS Stream 8",
//     "vendor": "centos",
//     "version": "8",
//     "architecture": "amd64"
//   },
//   "kernel": {
//     "release": "4.18.0-373.el8.x86_64",
//     "version": "#1 SMP Tue Mar 22 15:11:47 UTC 2022",
//     "architecture": "x86_64"
//   },

func main() {
	current, err := user.Current()
	if err != nil {
		log.Fatal(err)
	}

	if current.Uid != "0" {
		log.Fatal("requires superuser privilege")
	}

	var si sysinfo.SysInfo

	si.GetSysInfo()

	data, err := json.MarshalIndent(&si, "", "  ")
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(string(data))
}
