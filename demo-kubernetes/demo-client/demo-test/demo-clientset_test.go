package main

import (
	"context"
	"fmt"
	"path/filepath"
	"testing"
	"time"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
)

func TestNamespaceCreateAndDelete(t *testing.T) {
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

	// https://stackoverflow.com/questions/44711709/how-do-i-create-a-namespace-using-kubernetes-go-client-from-running-container-in
	namespaceName := "my-test-k7893"
	nsName := &corev1.Namespace{
		ObjectMeta: metav1.ObjectMeta{
			Name: namespaceName,
		},
	}
	// 先删除命名空间
	_ = clientset.CoreV1().Namespaces().Delete(context.TODO(), namespaceName, metav1.DeleteOptions{
		GracePeriodSeconds: new(int64),
	})
	_, err = clientset.CoreV1().Namespaces().Create(context.Background(), nsName, metav1.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	fmt.Println("已经创建命名空间", namespaceName)
	fmt.Println("命名空间", namespaceName, "将会在5秒后被删除")
	time.Sleep(time.Duration(5) * time.Second)

	err = clientset.CoreV1().Namespaces().Delete(context.Background(), namespaceName, metav1.DeleteOptions{
		GracePeriodSeconds: new(int64),
	})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
}

func TestPodList(t *testing.T) {
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

	podList, err := clientset.CoreV1().Pods("kube-system").
		List(context.TODO(), metav1.ListOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	for _, value := range podList.Items {
		fmt.Println("namespace=", value.Namespace, ",pod=", value.Name)
	}
}

// 测试GetPod接口
// https://blog.51cto.com/daixuan/5184509
func TestGetPod(t *testing.T) {
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

	podName := "demo-pod"
	pod := &corev1.Pod{
		ObjectMeta: metav1.ObjectMeta{
			Name: podName,
		},
		Spec: corev1.PodSpec{
			Containers: []corev1.Container{
				{Name: "container1", Image: "busybox", Command: []string{"sh", "-c", "sleep 7200"}},
			},
		},
	}
	podCreateResult, err := clientset.CoreV1().Pods(corev1.NamespaceDefault).Create(context.TODO(), pod, metav1.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	// fmt.Printf("成功创建pod %s，等待25秒后自动删除。。。\n", podCreateResult.GetObjectMeta().GetName())
	if podCreateResult.GetObjectMeta().GetName() != podName {
		t.Fatalf("expected %s, got %s", podName, podCreateResult.GetObjectMeta().GetName())
	}
	t.Logf("等待25秒后自动删除pod\n")
	time.Sleep(time.Duration(25) * time.Second)

	podGetResult, err := clientset.CoreV1().Pods(corev1.NamespaceDefault).Get(context.TODO(), podName, metav1.GetOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if podGetResult.GetObjectMeta().GetName() != podName {
		t.Fatalf("expected %s, got %s", podName, podGetResult.GetObjectMeta().GetName())
	}

	err = clientset.CoreV1().Pods(corev1.NamespaceDefault).Delete(context.TODO(), podName, metav1.DeleteOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	t.Logf("成功删除pod %s\n", podCreateResult.GetObjectMeta().GetName())
}
