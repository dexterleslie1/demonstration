package demo_controller_runtime

import (
	"context"
	websitev1 "demotest/demo-controller-runtime/website/v1"
	v1 "k8s.io/api/core/v1"
	v12 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/kubernetes"
	typedv1core "k8s.io/client-go/kubernetes/typed/core/v1"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/tools/record"
	"k8s.io/client-go/util/homedir"
	"k8s.io/client-go/util/retry"
	"path/filepath"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/client/config"
	"strconv"
	"testing"
	"time"
)

func TestControllerRuntimeCrdResource(t *testing.T) {
	crScheme := runtime.NewScheme()
	err := websitev1.AddToScheme(crScheme)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	err = v1.AddToScheme(crScheme)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	// NOTE: 需要添加kind到scheme否则会报告错误 "no kind is registered for the type v1.CustomResourceDefinition in scheme"
	err = v12.AddToScheme(crScheme)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	cl, err := client.New(config.GetConfigOrDie(), client.Options{
		Scheme: crScheme,
	})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	crdName := "websites.extensions.example.com"
	crd := &v12.CustomResourceDefinition{}
	err = cl.Get(context.Background(), client.ObjectKey{
		Namespace: v1.NamespaceDefault,
		Name:      crdName,
	}, crd, &client.GetOptions{})
	if err == nil {
		err := cl.Delete(context.Background(), crd, &client.DeleteOptions{
			GracePeriodSeconds: new(int64),
		})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}
	crd = &v12.CustomResourceDefinition{}
	crd.SetName(crdName)
	crd.TypeMeta = metav1.TypeMeta{
		Kind:       "CustomResourceDefinition",
		APIVersion: "apiextensions.k8s.io/v1",
	}
	crd.Spec = v12.CustomResourceDefinitionSpec{
		Scope: v12.NamespaceScoped,
		Group: "extensions.example.com",
		Versions: []v12.CustomResourceDefinitionVersion{
			{
				Name:    "v1",
				Storage: true,
				Served:  true,
				Schema: &v12.CustomResourceValidation{
					OpenAPIV3Schema: &v12.JSONSchemaProps{
						Type: "object",
						Properties: map[string]v12.JSONSchemaProps{
							"spec": {
								Type: "object",
								Properties: map[string]v12.JSONSchemaProps{
									"gitRepo": {
										Type: "string",
									},
								},
							},
							"status": {
								Type: "object",
								Properties: map[string]v12.JSONSchemaProps{
									"phase": {
										Type: "string",
									},
									"myMessage": {
										Type: "string",
									},
								},
							},
						},
					},
				},
				Subresources: &v12.CustomResourceSubresources{
					Status: &v12.CustomResourceSubresourceStatus{},
				},
				AdditionalPrinterColumns: []v12.CustomResourceColumnDefinition{
					{
						Name:        "Status",
						Type:        "string",
						Description: "The status of website",
						JSONPath:    ".status.phase",
					}, {
						Name:     "Age",
						Type:     "date",
						JSONPath: ".metadata.creationTimestamp",
					},
				},
			},
		},
		Names: v12.CustomResourceDefinitionNames{
			Kind:     "Website",
			Singular: "website",
			Plural:   "websites",
			ShortNames: []string{
				"ws",
			},
		},
	}
	err = cl.Create(context.Background(), crd, &client.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	websiteName := "test"
	website := &websitev1.Website{}
	website.SetNamespace(v1.NamespaceDefault)
	website.SetName(websiteName)
	err = cl.Get(context.Background(), client.ObjectKey{
		Namespace: v1.NamespaceDefault,
		Name:      websiteName,
	}, website, &client.GetOptions{})
	if err == nil {
		// 如果cr存在则删除
		err := cl.Delete(context.Background(), website, &client.DeleteOptions{
			GracePeriodSeconds: new(int64),
		})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}

	// 创建cr
	website = &websitev1.Website{}
	website.SetNamespace(v1.NamespaceDefault)
	website.SetName(websiteName)
	website.SetLabels(map[string]string{
		"app": "my-website",
	})
	website.Spec = websitev1.WebsiteSpec{
		GitRepo: "https://github.com/luksa/kubia-website-example.git",
	}
	err = cl.Create(context.Background(), website, &client.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// 修改cr
	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		website = &websitev1.Website{}
		err := cl.Get(context.Background(), client.ObjectKey{
			Namespace: v1.NamespaceDefault,
			Name:      websiteName,
		}, website, &client.GetOptions{})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}

		website.Status = websitev1.WebsiteStatus{
			Phase:     "ok",
			MyMessage: "Testing message",
		}

		err = cl.Status().Update(context.Background(), website, &client.SubResourceUpdateOptions{})
		return err
	})
	if retryErr != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	website = &websitev1.Website{}
	err = cl.Get(context.Background(), client.ObjectKey{
		Namespace: v1.NamespaceDefault,
		Name:      websiteName,
	}, website, &client.GetOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if "ok" != website.Status.Phase {
		t.Fatalf("expected ok, got %s", website.Status.Phase)
	}
	if "Testing message" != website.Status.MyMessage {
		t.Fatalf("expected Testing message, got %s", website.Status.MyMessage)
	}

	// 添加event到cr
	var kubeconfig string
	if home := homedir.HomeDir(); home != "" {
		kubeconfig = filepath.Join(home, ".kube", "config")
	} else {
		kubeconfig = ""
	}
	cf, err := clientcmd.BuildConfigFromFlags("", kubeconfig)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	clientset, err := kubernetes.NewForConfig(cf)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	eventBroadcaster := record.NewBroadcaster()
	eventBroadcaster.StartStructuredLogging(4)
	eventBroadcaster.StartRecordingToSink(&typedv1core.EventSinkImpl{Interface: clientset.CoreV1().Events("")})
	// EventSource是events列表显示from字段
	//groupVersion := schema.GroupVersion{Group: "extensions.example.com", Version: "v1"}
	//schemeObj := scheme.Scheme
	//schemeObj.AddKnownTypes(groupVersion, &websitev1.Website{}, &websitev1.WebsiteList{})
	//metav1.AddToGroupVersion(schemeObj, groupVersion)
	eventRecorder := eventBroadcaster.NewRecorder(crScheme, v1.EventSource{Component: "demo-test"})
	eventRecorder.Event(website, v1.EventTypeWarning, "Reason Test", "Message Test")
	eventBroadcaster.Shutdown()
	time.Sleep(2 * time.Second)

	websiteList := &websitev1.WebsiteList{}
	err = cl.List(context.Background(), websiteList, &client.ListOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	found := false
	for _, obj := range websiteList.Items {
		if obj.GetName() == websiteName {
			found = true
			break
		}
	}
	if !found {
		t.Fatalf("expected true, got %t", found)
	}

	//region 演示 pod 列表属于 website

	// 使用 labels 方式匹配并设置 pod 的 ownerreferences
	for i := 0; i < 2; i++ {
		isController := true
		apiVersion := websitev1.SchemeGroupVersion.String()
		pod := v1.Pod{
			ObjectMeta: metav1.ObjectMeta{
				Namespace: v1.NamespaceDefault,
				Name:      "pod-" + strconv.Itoa(i),
				Labels: map[string]string{
					"app": "my-website",
				},
				// 设置 OwnerReferences 后，删除 website 会自动删除其属下的 pod
				OwnerReferences: []metav1.OwnerReference{
					{
						APIVersion: apiVersion,
						Kind:       "Website",
						Name:       website.Name,
						UID:        website.UID,
						Controller: &isController,
					},
				},
			},
			Spec: v1.PodSpec{
				Containers: []v1.Container{
					{
						Name:            "nginx",
						Image:           "nginx",
						ImagePullPolicy: v1.PullIfNotPresent,
					},
				},
			},
		}

		//_ = cl.Delete(context.Background(), &pod, &client.DeleteOptions{GracePeriodSeconds: new(int64)})

		err := cl.Create(context.Background(), &pod, &client.CreateOptions{})
		if err != nil {
			t.Fatalf("expected no err, got %s", err)
		}
	}

	podList := v1.PodList{}
	err = cl.List(context.Background(), &podList, client.MatchingLabels{
		"app": "my-website",
	})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	if len(podList.Items) != 2 {
		t.Fatalf("expected 2, got %d", len(podList.Items))
	}

	//endregion
}
