package main

import (
	"context"
	v13 "demo-client-go-code-generator/pkg/apis/extensions.example.com/v1"
	"demo-client-go-code-generator/pkg/generated/clientset/versioned/scheme"
	v1 "demo-client-go-code-generator/pkg/generated/clientset/versioned/typed/extensions.example.com/v1"
	corev1 "k8s.io/api/core/v1"
	v12 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	typedv1core "k8s.io/client-go/kubernetes/typed/core/v1"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/tools/record"
	"k8s.io/client-go/util/homedir"
	"k8s.io/client-go/util/retry"
	"path/filepath"
	"testing"
	"time"
)

func TestClientsetCrdResource(t *testing.T) {
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
	clientset, err := v1.NewForConfig(config)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	websiteName := "test"
	_, err = clientset.Websites(v12.NamespaceDefault).Get(context.Background(), websiteName, v12.GetOptions{})
	if err == nil {
		// 对象已经存在则删除
		err := clientset.Websites(v12.NamespaceDefault).Delete(context.Background(), websiteName, v12.DeleteOptions{
			GracePeriodSeconds: new(int64),
		})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}

	// 创建website
	website := &v13.Website{}
	website.SetName(websiteName)
	website.Spec.GitRepo = "https://github.com/luksa/kubia-website-example.git"
	_, err = clientset.Websites(v12.NamespaceDefault).Create(context.Background(), website, v12.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// 修改website
	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		websiteNewest, err := clientset.Websites(v12.NamespaceDefault).Get(context.Background(), websiteName, v12.GetOptions{})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}

		// 修改website状态
		websiteNewest.Status.Phase = "ok"
		websiteNewest.Status.MyMessage = "Testing message"

		_, err = clientset.Websites(v12.NamespaceDefault).UpdateStatus(context.Background(), websiteNewest, v12.UpdateOptions{})
		return err
	})
	if retryErr != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	websiteNewest, err := clientset.Websites(v12.NamespaceDefault).Get(context.Background(), websiteName, v12.GetOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if websiteNewest.Status.Phase != "ok" {
		t.Fatalf("expected ok, got %s", websiteNewest.Status.Phase)
	}
	if websiteNewest.Status.MyMessage != "Testing message" {
		t.Fatalf("expected Testing message, got %s", websiteNewest.Status.MyMessage)
	}

	// 添加event到website
	clientsetBuiltin, err := kubernetes.NewForConfig(config)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	eventBroadcaster := record.NewBroadcaster()
	eventBroadcaster.StartStructuredLogging(4)
	eventBroadcaster.StartRecordingToSink(&typedv1core.EventSinkImpl{Interface: clientsetBuiltin.CoreV1().Events("")})
	// EventSource是events列表显示from字段
	eventRecorder := eventBroadcaster.NewRecorder(scheme.Scheme, corev1.EventSource{Component: "demo-test"})
	eventRecorder.Event(websiteNewest, corev1.EventTypeWarning, "Reason Test", "Message Test")
	eventBroadcaster.Shutdown()
	time.Sleep(2 * time.Second)

	// 查询website列表
	list, err := clientset.Websites(v12.NamespaceDefault).List(context.Background(), v12.ListOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	found := false
	for _, obj := range list.Items {
		if obj.GetName() == websiteName {
			found = true
			break
		}
	}
	if !found {
		t.Fatalf("expected true, got %t", found)
	}
}
