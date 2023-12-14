package main

import (
	"fmt"
	"k8s.io/apimachinery/pkg/runtime/schema"
	"k8s.io/client-go/discovery"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
	"path/filepath"
	"testing"
)

// https://cloud.tencent.com/developer/article/2214994
func TestDiscoveryClient(t *testing.T) {
	var kubeconfig string
	if home := homedir.HomeDir(); home != "" {
		kubeconfig = filepath.Join(home, ".kube", "config")
	} else {
		kubeconfig = ""
	}

	config, err := clientcmd.BuildConfigFromFlags("", kubeconfig)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	discoveryClient, err := discovery.NewDiscoveryClientForConfig(config)
	// discoveryClient.ServerGroupsAndResources 返回API Server所支持的资源组、资源版本、资源信息
	apiGroupList, apiResourceList, err := discoveryClient.ServerGroupsAndResources()
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// kubectl api-versions等价
	fmt.Println("kubectl api-versions 等价输出: ")
	for _, apiGroup := range apiGroupList {
		// 每个api组中可能有多个版本
		for _, version := range apiGroup.Versions {
			fmt.Println("\t" + version.GroupVersion)
		}
	}

	fmt.Println("\n\nkuebctl api-resources 等价输出: ")
	for _, apiResource := range apiResourceList {
		gv, err := schema.ParseGroupVersion(apiResource.GroupVersion)
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}

		fmt.Printf("\tgroup: %s, version: %s\n", gv.Group, gv.Version)
		for _, resource := range apiResource.APIResources {
			fmt.Printf("\t\tresource: %s, kind: %s\n", resource.Name, resource.Kind)
		}
	}
}
