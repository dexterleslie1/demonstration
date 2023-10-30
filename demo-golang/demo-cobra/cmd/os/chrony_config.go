/*
Copyright © 2022 NAME HERE <EMAIL ADDRESS>
*/
package osd

import (
	"fmt"

	"github.com/spf13/cobra"
)

var chronyCommand = &cobra.Command{
	Use:   "chrony",
	Short: "chrony short.",
	Long:  `chrony long.`,
	// Run: func(cmd *cobra.Command, args []string) {
	// 	fmt.Println("os config called")
	// },
}

var configCommand = &cobra.Command{
	Use:   "config",
	Short: "chrony config short.",
	Long:  `chrony config long.`,
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Println("chrony config called")
	},
}

func init() {
	OsCommand.AddCommand(chronyCommand)
	chronyCommand.AddCommand(configCommand)

	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// testCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// testCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
