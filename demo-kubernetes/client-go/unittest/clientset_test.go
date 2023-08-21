package main

import (
	"context"
	"flag"
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
	var kubeconfig *string
	if home := homedir.HomeDir(); home != "" {
		kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()

	config, err := clientcmd.BuildConfigFromFlags("", *kubeconfig)
	if err != nil {
		panic(err)
	}
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err)
	}

	// https://stackoverflow.com/questions/44711709/how-do-i-create-a-namespace-using-kubernetes-go-client-from-running-container-in
	namespaceName := "my-test-k7893"
	nsName := &corev1.Namespace{
		ObjectMeta: metav1.ObjectMeta{
			Name: namespaceName,
		},
	}
	clientset.CoreV1().Namespaces().Create(context.Background(), nsName, metav1.CreateOptions{})

	fmt.Println("已经创建命名空间", namespaceName)
	fmt.Println("命名空间", namespaceName, "将会在10秒后被删除")
	time.Sleep(time.Duration(10) * time.Second)

	clientset.CoreV1().Namespaces().Delete(context.Background(), namespaceName, metav1.DeleteOptions{})
}

func TestPodList(t *testing.T) {
	var kubeconfig *string
	if home := homedir.HomeDir(); home != "" {
		kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()

	config, err := clientcmd.BuildConfigFromFlags("", *kubeconfig)
	if err != nil {
		panic(err)
	}
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err)
	}

	podList, err := clientset.CoreV1().Pods("kube-system").
		List(context.TODO(), metav1.ListOptions{})
	for _, value := range podList.Items {
		fmt.Println("namespace=", value.Namespace, ",pod=", value.Name)
	}
}

// 测试GetPod接口
// https://blog.51cto.com/daixuan/5184509
func TestGetPod(t *testing.T) {
	var kubeconfig *string
	if home := homedir.HomeDir(); home != "" {
		kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()

	config, err := clientcmd.BuildConfigFromFlags("", *kubeconfig)
	if err != nil {
		panic(err)
	}
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err)
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
		panic(err)
	}
	// fmt.Printf("成功创建pod %s，等待25秒后自动删除。。。\n", podCreateResult.GetObjectMeta().GetName())
	if podCreateResult.GetObjectMeta().GetName() != podName {
		t.Fatalf("期望%s，实际%s", podName, podCreateResult.GetObjectMeta().GetName())
	}
	t.Logf("等待25秒后自动删除pod\n")
	time.Sleep(time.Duration(25) * time.Second)

	podGetResult, err := clientset.CoreV1().Pods(corev1.NamespaceDefault).Get(context.TODO(), podName, metav1.GetOptions{})
	if podGetResult.GetObjectMeta().GetName() != podName {
		t.Fatalf("期望%s，实际%s", podName, podGetResult.GetObjectMeta().GetName())
	}

	err = clientset.CoreV1().Pods(corev1.NamespaceDefault).Delete(context.TODO(), podName, metav1.DeleteOptions{})
	if err != nil {
		panic(err)
	}
	t.Logf("成功删除pod %s\n", podCreateResult.GetObjectMeta().GetName())
}
