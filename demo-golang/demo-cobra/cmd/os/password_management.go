/*
Copyright © 2022 NAME HERE <EMAIL ADDRESS>
*/
package osd

import (
	"fmt"
	"log"

	"github.com/spf13/cobra"
)

var passwordCommand = &cobra.Command{
	Use:   "password",
	Short: "密码管理",
	Long:  `密码管理`,
}

var changePasswordCommand = &cobra.Command{
	Use:   "change",
	Short: "修改密码",
	Long:  `修改密码`,
	Run: func(cmd *cobra.Command, args []string) {
		username, err := cmd.Flags().GetString("username")
		if err != nil {
			log.Fatalf("%s", err)
		}
		fmt.Println("成功修改密码，username=" + string(username))
	},
}

func init() {
	OsCommand.AddCommand(passwordCommand)
	passwordCommand.AddCommand(changePasswordCommand)

	changePasswordCommand.Flags().StringP("username", "u", "root", "被修改密码的用户名")

	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// testCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// testCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
