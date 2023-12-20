package main

import (
	"context"
	v12 "demo-client-go-code-generator/pkg/apis/extensions.example.com/v1"
	"demo-client-go-code-generator/pkg/generated/clientset/versioned"
	"demo-client-go-code-generator/pkg/generated/informers/externalversions"
	"fmt"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/labels"
	"k8s.io/apimachinery/pkg/util/runtime"
	"k8s.io/client-go/tools/cache"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
	"k8s.io/client-go/util/retry"
	"path/filepath"
	"testing"
	"time"
)

func TestInformerCrdResource(t *testing.T) {
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
	clientset, err := versioned.NewForConfig(config)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// 初始化 informer
	factory := externalversions.NewSharedInformerFactory(clientset, 0)
	websiteInformer := factory.Website().V1().Websites()
	informer := websiteInformer.Informer()
	defer runtime.HandleCrash()

	// 启动 informer，list & watch
	factory.Start(nil)

	// 从 apiserver 同步资源，即 list
	if !cache.WaitForCacheSync(nil, informer.HasSynced) {
		runtime.HandleError(fmt.Errorf("Timed out waiting for caches to sync"))
		return
	}

	// 使用自定义 handler
	_, err = informer.AddEventHandler(cache.ResourceEventHandlerFuncs{
		AddFunc: func(obj interface{}) {
			fmt.Println("add not implemented")
		},
		UpdateFunc: func(interface{}, interface{}) { fmt.Println("update not implemented") }, // 此处省略 workqueue 的使用
		DeleteFunc: func(interface{}) { fmt.Println("delete not implemented") },
	})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	websiteName := "test"
	_, err = websiteInformer.Lister().Websites(corev1.NamespaceDefault).Get(websiteName)
	if err == nil {
		// 删除pod
		err := clientset.WebsiteV1().Websites(corev1.NamespaceDefault).Delete(context.Background(), websiteName, v1.DeleteOptions{
			GracePeriodSeconds: new(int64),
		})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}
	time.Sleep(time.Second)
	_, err = websiteInformer.Lister().Websites(corev1.NamespaceDefault).Get(websiteName)
	if err == nil {
		t.Fatalf("expected not found err, got no err")
	}

	website := &v12.Website{}
	website.SetNamespace(corev1.NamespaceDefault)
	website.SetName(websiteName)
	website.Spec = v12.WebsiteSpec{GitRepo: "https://github.com/luksa/kubia-website-example.git"}
	_, err = clientset.WebsiteV1().Websites(corev1.NamespaceDefault).Create(context.Background(), website, v1.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	time.Sleep(time.Second)
	website, err = websiteInformer.Lister().Websites(corev1.NamespaceDefault).Get(websiteName)
	if websiteName != website.GetName() {
		t.Fatalf("expected %s, got %s", websiteName, website.GetName())
	}

	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		websiteNewest, err := clientset.WebsiteV1().Websites(v1.NamespaceDefault).Get(context.Background(), websiteName, v1.GetOptions{})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}

		// 修改website状态
		websiteNewest.Status.Phase = "ok"
		websiteNewest.Status.MyMessage = "Testing message"

		_, err = clientset.WebsiteV1().Websites(v1.NamespaceDefault).UpdateStatus(context.Background(), websiteNewest, v1.UpdateOptions{})
		return err
	})
	if retryErr != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	time.Sleep(time.Second)
	websiteNewest, err := websiteInformer.Lister().Websites(corev1.NamespaceDefault).Get(websiteName)
	if "ok" != websiteNewest.Status.Phase {
		t.Fatalf("expected ok, got %s", websiteNewest.Status.Phase)
	}
	if "Testing message" != websiteNewest.Status.MyMessage {
		t.Fatalf("expected Testing message, got %s", websiteNewest.Status.MyMessage)
	}

	// 查询website列表
	list, err := websiteInformer.Lister().Websites(corev1.NamespaceDefault).List(labels.Everything())
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	found := false
	for _, podObj := range list {
		if podObj.GetName() == websiteName {
			found = true
			break
		}
	}
	if !found {
		t.Fatalf("expected true, got %t", found)
	}
}
