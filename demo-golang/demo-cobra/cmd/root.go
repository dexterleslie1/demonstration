/*
Copyright © 2022 NAME HERE <EMAIL ADDRESS>
*/
package cmd

import (
	"os"

	"demo-cobra/cmd/mariadb"
	"demo-cobra/cmd/mysql"
	osd "demo-cobra/cmd/os"

	"github.com/spf13/cobra"
)

// rootCmd represents the base command when called without any subcommands
var rootCmd = &cobra.Command{
	Use:   "dcli",
	Short: "A brief description of your application",
	Long: `A longer description that spans multiple lines and likely contains
examples and usage of using your application. For example:

Cobra is a CLI library for Go that empowers applications.
This application is a tool to generate the needed files
to quickly create a Cobra application.`,
	// Uncomment the following line if your bare application
	// has an action associated with it:
	// Run: func(cmd *cobra.Command, args []string) { },
}

// Execute adds all child commands to the root command and sets flags appropriately.
// This is called by main.main(). It only needs to happen once to the rootCmd.
func Execute() {
	err := rootCmd.Execute()
	if err != nil {
		os.Exit(1)
	}
}

func init() {
	// 子命令关系是和AddCommand时命令的树状关系对应的
	// 目前这个子命令目录组织不是参考官方得来的，是自己自创的
	// 官方推荐添加子命令方法 cobra-cli add create -p 'configCmd'
	// https://github.com/spf13/cobra-cli/blob/main/README.md
	rootCmd.AddCommand(mariadb.MariadbCmd)
	rootCmd.AddCommand(mysql.MysqlCmd)
	rootCmd.AddCommand(osd.OsCommand)
	// Here you will define your flags and configuration settings.
	// Cobra supports persistent flags, which, if defined here,
	// will be global for your application.

	// rootCmd.PersistentFlags().StringVar(&cfgFile, "config", "", "config file (default is $HOME/.cobra.yaml)")

	// Cobra also supports local flags, which will only run
	// when this action is called directly.
	rootCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}
