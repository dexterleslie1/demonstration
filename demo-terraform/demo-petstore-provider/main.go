// 提供程序的入口点
package main

import (
	"github.com/hashicorp/terraform-plugin-sdk/v2/plugin"
	petstore "github.com/petstore/petstore-provider"
)

func main() {
	plugin.Serve(&plugin.ServeOpts{
		ProviderFunc: petstore.Provider,
	})
}
