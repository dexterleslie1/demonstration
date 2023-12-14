package main

import (
	"context"
	v1 "k8s.io/api/core/v1"
	v12 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime/schema"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
	"k8s.io/client-go/util/retry"
	"path/filepath"
	"testing"
)

// https://github.com/kubernetes/client-go/blob/master/examples/dynamic-create-update-delete-deployment/main.go
func TestDynamicClientBuiltinResource(t *testing.T) {
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

	gvr := schema.GroupVersionResource{
		Group:    "",
		Version:  "v1",
		Resource: "pods",
	}

	podName := "pod1"
	_, err = dynamicClient.Resource(gvr).Namespace(v1.NamespaceDefault).Get(context.TODO(), podName, v12.GetOptions{})
	// err==nil表示pod存在
	if err == nil {
		err := dynamicClient.Resource(gvr).Namespace(v1.NamespaceDefault).Delete(context.TODO(), podName, v12.DeleteOptions{
			// NOTE: 马上删除pod，否则在创建时候报告 object is being deleted: pods "" already exists 错误
			GracePeriodSeconds: new(int64),
		})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}

	// 创建pod
	podObj := unstructured.Unstructured{
		Object: map[string]interface{}{
			"apiVersion": "v1",
			"kind":       "Pod",
			"metadata": map[string]interface{}{
				"name": podName,
				"labels": map[string]interface{}{
					"creation_method": "manual",
					"env":             "dev",
				},
			},
			"spec": map[string]interface{}{
				"containers": []map[string]interface{}{
					{
						"name":  "nginx",
						"image": "nginx",
					},
				},
			},
		},
	}
	podObjCreated, err := dynamicClient.Resource(gvr).Namespace(v1.NamespaceDefault).Create(context.TODO(), &podObj, v12.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	actualPodName, _, err := unstructured.NestedString(podObjCreated.Object, "metadata", "name")
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if podName != actualPodName {
		t.Fatalf("expected %s, got %s", podName, actualPodName)
	}

	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		// 修改pod labels
		podObjNewest, err := dynamicClient.Resource(gvr).Namespace(v1.NamespaceDefault).
			Get(context.TODO(), podName, v12.GetOptions{})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
		labelsObj, _, err := unstructured.NestedMap(podObjNewest.Object, "metadata", "labels")
		err = unstructured.SetNestedField(labelsObj, "prod", "env")
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
		err = unstructured.SetNestedField(podObjNewest.Object, labelsObj, "metadata", "labels")
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
		_, err = dynamicClient.Resource(gvr).Namespace(v1.NamespaceDefault).
			Update(context.TODO(), podObjNewest, v12.UpdateOptions{})
		return err
	})
	if retryErr != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	podObjNewest, err := dynamicClient.Resource(gvr).Namespace(v1.NamespaceDefault).
		Get(context.TODO(), podName, v12.GetOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	actualEnv := podObjNewest.GetLabels()["env"]
	if actualEnv != "prod" {
		t.Fatalf("expected prod, got %s", actualEnv)
	}

	// 查询pod列表
	list, err := dynamicClient.Resource(gvr).Namespace(v1.NamespaceDefault).List(context.TODO(), v12.ListOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	found := false
	for _, podObj := range list.Items {
		if podObj.GetName() == podName {
			found = true
			break
		}
	}
	if !found {
		t.Fatalf("expected true, got %t", found)
	}
}
