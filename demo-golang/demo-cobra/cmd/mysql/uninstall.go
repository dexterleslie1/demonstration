/*
Copyright © 2022 NAME HERE <EMAIL ADDRESS>

*/
package mysql

import (
	"fmt"

	"github.com/spf13/cobra"
)

var uninstallCmd = &cobra.Command{
	Use:   "uninstall",
	Short: "mysql uninstall short.",
	Long: `mysql uninstall long.`,
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Println("mysql uninstall called")
	},
}

func init() {
	MysqlCmd.AddCommand(uninstallCmd)
	
	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// testCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// testCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
