package demo_controller_runtime

import (
	"context"
	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	v12 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/util/retry"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/client/config"
	"testing"
)

func TestControllerRuntimeBuiltinResource(t *testing.T) {
	cl, err := client.New(config.GetConfigOrDie(), client.Options{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	podName := "pod1"

	// 获取pod
	pod := &corev1.Pod{}
	err = cl.Get(context.TODO(), client.ObjectKey{
		Namespace: corev1.NamespaceDefault,
		Name:      podName,
	}, pod, &client.GetOptions{})
	if err == nil {
		// 删除pod
		_ = cl.Delete(context.TODO(), pod, &client.DeleteOptions{GracePeriodSeconds: new(int64)})
	}

	pod = &corev1.Pod{}
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
	err = cl.Create(context.TODO(), pod, &client.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		// 修改pod labels
		pod := &corev1.Pod{}
		err = cl.Get(context.TODO(), client.ObjectKey{Namespace: corev1.NamespaceDefault, Name: podName}, pod, &client.GetOptions{})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
		pod.GetLabels()["env"] = "prod"
		err = cl.Update(context.TODO(), pod, &client.UpdateOptions{})
		return err
	})
	if retryErr != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	pod = &corev1.Pod{}
	err = cl.Get(context.TODO(), client.ObjectKey{Namespace: corev1.NamespaceDefault, Name: podName}, pod, &client.GetOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	actualEnv := pod.GetLabels()["env"]
	if actualEnv != "prod" {
		t.Fatalf("expected prod, got %s", actualEnv)
	}

	podList := &corev1.PodList{}
	err = cl.List(context.TODO(), podList, &client.ListOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	found := false
	for _, podObj := range podList.Items {
		if podObj.GetName() == podName {
			found = true
			break
		}
	}
	if !found {
		t.Fatalf("expected true, got %t", found)
	}
}

func TestControllerRuntimeReplicaSetAndPod(t *testing.T) {
	cl, err := client.New(config.GetConfigOrDie(), client.Options{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	replicaSetName := "replicaset1"

	// 创建 replicaset
	replicaSet := &v1.ReplicaSet{}
	replicaSet.SetNamespace(corev1.NamespaceDefault)
	replicaSet.SetName(replicaSetName)
	replicas := int32(2)
	replicaSet.Spec.Replicas = &replicas
	replicaSet.Spec.Selector = &v12.LabelSelector{
		MatchLabels: map[string]string{
			"app": "nginx-pod",
		},
	}
	replicaSet.Spec.Template = corev1.PodTemplateSpec{
		ObjectMeta: v12.ObjectMeta{
			Labels: map[string]string{
				"app": "nginx-pod",
			},
		},
		Spec: corev1.PodSpec{
			Containers: []corev1.Container{
				{
					Name:            "nginx",
					Image:           "nginx",
					ImagePullPolicy: corev1.PullIfNotPresent,
				},
			},
		},
	}

	_ = cl.Delete(context.Background(), replicaSet, &client.DeleteOptions{GracePeriodSeconds: new(int64)})

	err = cl.Create(context.Background(), replicaSet, &client.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// 查询 replicaset 相关联的 pod 列表
	// https://github.com/kubernetes-sigs/controller-runtime/blob/main/pkg/builder/example_test.go
	podList := corev1.PodList{}
	err = cl.List(context.Background(), &podList, client.MatchingLabels{
		"app": "nginx-pod",
	})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	if len(podList.Items) != 2 {
		t.Fatalf("expected 2, got %d", len(podList.Items))
	}
}
