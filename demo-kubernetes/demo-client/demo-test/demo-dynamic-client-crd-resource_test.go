package main

import (
	"context"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime/schema"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/kubernetes/scheme"
	typedv1core "k8s.io/client-go/kubernetes/typed/core/v1"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/tools/record"
	"k8s.io/client-go/util/homedir"
	"k8s.io/client-go/util/retry"
	"path/filepath"
	"testing"
	"time"
)

// todo client-go使用yaml操作cr
func TestDynamicClientCrdResource(t *testing.T) {
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
	dynamicClient, err := dynamic.NewForConfig(config)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// 创建crd
	crd := unstructured.Unstructured{
		Object: map[string]interface{}{
			"apiVersion": "apiextensions.k8s.io/v1",
			"kind":       "CustomResourceDefinition",
			"metadata": map[string]interface{}{
				"name": "websites.extensions.example.com",
			},
			"spec": map[string]interface{}{
				"scope": "Namespaced",
				"group": "extensions.example.com",
				"versions": []map[string]interface{}{
					{
						"name":    "v1",
						"storage": true,
						"served":  true,
						"schema": map[string]interface{}{
							"openAPIV3Schema": map[string]interface{}{
								"type": "object",
								"properties": map[string]interface{}{
									"spec": map[string]interface{}{
										"type": "object",
										"properties": map[string]interface{}{
											"gitRepo": map[string]interface{}{
												"type": "string",
											},
										},
									},
									"status": map[string]interface{}{
										"type": "object",
										"properties": map[string]interface{}{
											"phase": map[string]interface{}{
												"type": "string",
											},
											"myMessage": map[string]interface{}{
												"type": "string",
											},
										},
									},
								},
							},
						},
						"subresources": map[string]interface{}{
							"status": map[string]interface{}{},
						},
						"additionalPrinterColumns": []map[string]interface{}{
							{
								"name":        "Status",
								"type":        "string",
								"description": "The status of website",
								"jsonPath":    ".status.phase",
							}, {
								"name":     "Age",
								"type":     "date",
								"jsonPath": ".metadata.creationTimestamp",
							},
						},
					},
				},
				"names": map[string]interface{}{
					"kind":     "Website",
					"singular": "website",
					"plural":   "websites",
					"shortNames": []interface{}{
						"ws",
					},
				},
			},
		},
	}

	// todo 是否可以使用discoveryClient获取gvr
	gvr := schema.GroupVersionResource{
		Group:    "apiextensions.k8s.io",
		Version:  "v1",
		Resource: "customresourcedefinitions",
	}
	// todo 如何使用client-go实现kubectl apply
	_, err = dynamicClient.Resource(gvr).Create(context.TODO(), &crd, v1.CreateOptions{})
	//if err != nil {
	//	t.Fatalf("expected no err, got %s", err)
	//}

	gvrCr := schema.GroupVersionResource{
		Group:    "extensions.example.com",
		Version:  "v1",
		Resource: "websites",
	}
	websiteName := "test"

	// 查询website
	_, err = dynamicClient.Resource(gvrCr).Namespace(v1.NamespaceDefault).
		Get(context.TODO(), websiteName, v1.GetOptions{})
	// website对象已经存在，删除
	if err == nil {
		err := dynamicClient.Resource(gvrCr).Namespace(v1.NamespaceDefault).
			Delete(context.TODO(), websiteName, v1.DeleteOptions{
				GracePeriodSeconds: new(int64),
			})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}

	// 创建cr
	cr := unstructured.Unstructured{
		Object: map[string]interface{}{
			"apiVersion": "extensions.example.com/v1",
			"kind":       "Website",
			"metadata": map[string]interface{}{
				"name": websiteName,
			},
			"spec": map[string]interface{}{
				"gitRepo": "https://github.com/luksa/kubia-website-example.git",
			},
		},
	}
	_, err = dynamicClient.Resource(gvrCr).Namespace(v1.NamespaceDefault).
		Create(context.TODO(), &cr, v1.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// 修改cr
	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		crNewest, err := dynamicClient.Resource(gvrCr).Namespace(v1.NamespaceDefault).
			Get(context.TODO(), websiteName, v1.GetOptions{})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}

		// 修改cr状态
		err = unstructured.SetNestedField(crNewest.Object, "ok", "status", "phase")
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
		err = unstructured.SetNestedField(crNewest.Object, "Testing message", "status", "myMessage")
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}

		_, err = dynamicClient.Resource(gvrCr).Namespace(v1.NamespaceDefault).
			UpdateStatus(context.TODO(), crNewest, v1.UpdateOptions{})
		return err
	})
	if retryErr != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	crNewest, err := dynamicClient.Resource(gvrCr).Namespace(v1.NamespaceDefault).
		Get(context.TODO(), websiteName, v1.GetOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	statusStr, _, err := unstructured.NestedString(crNewest.Object, "status", "phase")
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if "ok" != statusStr {
		t.Fatalf("expected ok, got %s", statusStr)
	}
	myMessageStr, _, err := unstructured.NestedString(crNewest.Object, "status", "myMessage")
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if "Testing message" != myMessageStr {
		t.Fatalf("expected Testing message, got %s", myMessageStr)
	}

	// 添加event到cr
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	eventBroadcaster := record.NewBroadcaster()
	eventBroadcaster.StartStructuredLogging(4)
	eventBroadcaster.StartRecordingToSink(&typedv1core.EventSinkImpl{Interface: clientset.CoreV1().Events("")})
	// EventSource是events列表显示from字段
	eventRecorder := eventBroadcaster.NewRecorder(scheme.Scheme, corev1.EventSource{Component: "demo-test"})
	eventRecorder.Event(crNewest, corev1.EventTypeWarning, "Reason Test", "Message Test")
	eventBroadcaster.Shutdown()
	time.Sleep(2 * time.Second)

	// 查询cr列表
	list, err := dynamicClient.Resource(gvrCr).Namespace(v1.NamespaceDefault).
		List(context.TODO(), v1.ListOptions{})
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
