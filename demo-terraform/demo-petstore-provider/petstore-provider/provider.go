// 包含提供程序定义，资源映射和共享配置对象的初始化
package petstore

import (
	"errors"

	"github.com/hashicorp/terraform-plugin-sdk/v2/helper/schema"
	"github.com/petstore/petstore-api-sdk-go"
)

func Provider() *schema.Provider {
	return &schema.Provider{
		Schema: map[string]*schema.Schema{
			"host": &schema.Schema{
				Type:     schema.TypeString,
				Optional: true,
				// 支持通过环境变量设置 provider host参数
				DefaultFunc: schema.EnvDefaultFunc("PETSTORE_HOST", nil),
			},
		},

		ResourcesMap: map[string]*schema.Resource{
			// resource 资源对应的名称
			"petstore_pet": resourcePSPet(),
		},

		// 创建供资源共享并调用的共享配置对应
		// 这里共享对象指的是 petstore-api-sdk-go 对象，用于和petstore-api通许的go sdk对象
		ConfigureFunc: providerConfigure,
	}
}

func providerConfigure(d *schema.ResourceData) (interface{}, error) {
	host, _ := d.Get("host").(string)
	if host == "" {
		return nil, errors.New("没有指定provider host参数")
	}
	port := 8080
	return petstore.NewPetClient(host, port)
}
