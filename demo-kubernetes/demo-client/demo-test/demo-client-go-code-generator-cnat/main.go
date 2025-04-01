/*
Copyright 2017 The Kubernetes Authors.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package main

import (
	"k8s.io/client-go/util/homedir"
	"path/filepath"
	"time"

	"k8s.io/apimachinery/pkg/util/wait"
	kubeinformers "k8s.io/client-go/informers"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/klog"

	clientset "demo-client-go-code-generator-cnat/pkg/generated/clientset/versioned"
	informers "demo-client-go-code-generator-cnat/pkg/generated/informers/externalversions"
)

func main() {
	// 初始化日志组件
	klog.InitFlags(nil)

	// 判断是否在pod内运行
	cfg, err := rest.InClusterConfig()
	if err != nil {
		var kubeconfig string
		if home := homedir.HomeDir(); home != "" {
			kubeconfig = filepath.Join(home, ".kube", "config")
		} else {
			kubeconfig = ""
		}
		cfg, err = clientcmd.BuildConfigFromFlags("", kubeconfig)
		if err != nil {
			klog.Fatalf("Error building kubeconfig: %s", err.Error())
		}
	}

	// 创建内置clientset
	kubeClient, err := kubernetes.NewForConfig(cfg)
	if err != nil {
		klog.Fatalf("Error building kubernetes clientset: %s", err.Error())
	}

	// 创建cnat clientset
	cnatClient, err := clientset.NewForConfig(cfg)
	if err != nil {
		klog.Fatalf("Error building cnat clientset: %s", err.Error())
	}

	// 创建内置Informer
	kubeInformerFactory := kubeinformers.NewSharedInformerFactory(kubeClient, time.Minute*10)
	// 创建cnat Informer
	cnatInformerFactory := informers.NewSharedInformerFactory(cnatClient, time.Minute*10)

	controller := NewController(kubeClient, cnatClient, cnatInformerFactory.Cnat().V1alpha1().Ats(), kubeInformerFactory.Core().V1().Pods())

	// notice that there is no need to run Start methods in a separate goroutine. (i.e. go kubeInformerFactory.Start(stopCh))
	// Start method is non-blocking and runs all registered informers in a dedicated goroutine.
	kubeInformerFactory.Start(wait.NeverStop)
	cnatInformerFactory.Start(wait.NeverStop)

	// 启动controller
	if err = controller.Run(2, wait.NeverStop); err != nil {
		klog.Fatalf("Error running controller: %s", err.Error())
	}
}
