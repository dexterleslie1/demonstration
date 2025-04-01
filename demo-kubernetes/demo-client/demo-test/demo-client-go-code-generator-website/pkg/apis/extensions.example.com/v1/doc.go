// 告诉deecopy-gen需要为这个包中的所有类型生成对应的深拷贝方法
// +k8s:deepcopy-gen=package

// 定义了API组的全名
// +groupName=extensions.example.com

// 在默认情况下，代码生成器会使用父包名作为标识符的名字，例如clientset生成代码如下:
//
// type Interface interface {
//	Discovery() discovery.DiscoveryInterface
//	ExtensionsV1() extensionsv1.ExtensionsV1Interface
// }
//
// 指定groupGoName=website后，clientset生成代码如下：
//
// type Interface interface {
//	Discovery() discovery.DiscoveryInterface
//	WebsiteV1() websitev1.WebsiteV1Interface
// }
//
// +groupGoName=website

package v1
