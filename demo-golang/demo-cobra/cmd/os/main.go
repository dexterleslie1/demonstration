/*
Copyright © 2022 NAME HERE <EMAIL ADDRESS>
*/
package osd

import (
	_ "fmt"

	"github.com/spf13/cobra"
)

var OsCommand = &cobra.Command{
	Use:   "os",
	Short: "os short.",
	Long:  `os long.`,
	// Run: func(cmd *cobra.Command, args []string) {
	// 	fmt.Println("mysql called")
	// },
}

func init() {
	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// testCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// testCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
