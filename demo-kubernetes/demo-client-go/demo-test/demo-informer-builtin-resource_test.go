package main

import (
	"context"
	"fmt"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/labels"
	"k8s.io/apimachinery/pkg/util/runtime"
	"k8s.io/client-go/informers"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/cache"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
	"k8s.io/client-go/util/retry"
	"path/filepath"
	"testing"
	"time"
)

// https://blog.tianfeiyu.com/2019/05/17/client-go_informer/
func TestInformerBuiltinResource(t *testing.T) {
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
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// 初始化 informer
	factory := informers.NewSharedInformerFactory(clientset, 0)
	podInformer := factory.Core().V1().Pods()
	informer := podInformer.Informer()
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
			//pod := obj.(*corev1.Pod)
			//fmt.Println("add a node:", pod.Name)
		},
		UpdateFunc: func(interface{}, interface{}) { fmt.Println("update not implemented") }, // 此处省略 workqueue 的使用
		DeleteFunc: func(interface{}) { fmt.Println("delete not implemented") },
	})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	podName := "pod1"
	_, err = podInformer.Lister().Pods(corev1.NamespaceDefault).Get(podName)
	if err == nil {
		// 删除pod
		err := clientset.CoreV1().Pods(corev1.NamespaceDefault).Delete(context.TODO(), podName, v1.DeleteOptions{
			GracePeriodSeconds: new(int64),
		})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}
	time.Sleep(time.Second)
	_, err = podInformer.Lister().Pods(corev1.NamespaceDefault).Get(podName)
	if err == nil {
		t.Fatalf("expected not found err, got no err")
	}

	pod := &corev1.Pod{}
	pod.SetNamespace(corev1.NamespaceDefault)
	pod.SetName(podName)
	pod.SetLabels(map[string]string{
		"creation_method": "manual",
		"env":             "dev",
	})
	pod.Spec = corev1.PodSpec{
		Containers: []corev1.Container{
			{
				Name:  "nginx",
				Image: "nginx",
			},
		},
	}
	_, err = clientset.CoreV1().Pods(corev1.NamespaceDefault).Create(context.TODO(), pod, v1.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	time.Sleep(time.Second)
	pod, err = podInformer.Lister().Pods(corev1.NamespaceDefault).Get(podName)
	if podName != pod.GetName() {
		t.Fatalf("expected %s, got %s", podName, pod.GetName())
	}

	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		// 修改pod labels
		podNewest, err := podInformer.Lister().Pods(corev1.NamespaceDefault).Get(podName)
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
		podNewest.GetLabels()["env"] = "prod"

		_, err = clientset.CoreV1().Pods(corev1.NamespaceDefault).Update(context.TODO(), podNewest, v1.UpdateOptions{})
		return err
	})
	if retryErr != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	podNewest, err := podInformer.Lister().Pods(corev1.NamespaceDefault).Get(podName)
	actualEnv := podNewest.GetLabels()["env"]
	if actualEnv != "prod" {
		t.Fatalf("expected prod, got %s", actualEnv)
	}

	// 查询pod列表
	list, err := podInformer.Lister().Pods(corev1.NamespaceDefault).List(labels.Everything())
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	found := false
	for _, podObj := range list {
		if podObj.GetName() == podName {
			found = true
			break
		}
	}
	if !found {
		t.Fatalf("expected true, got %t", found)
	}
}
