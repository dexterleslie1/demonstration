package main

import (
	"context"
	websitev1 "demotest/website/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/util/retry"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/client/config"
	"testing"
)

func TestControllerRuntimeCrdResource(t *testing.T) {
	crScheme := runtime.NewScheme()
	err := websitev1.AddToScheme(crScheme)
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	cl, err := client.New(config.GetConfigOrDie(), client.Options{
		Scheme: crScheme,
	})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// todo 是否可以使用CustomResourceDefinition创建代替yaml crd
	//crd := &v1.CustomResourceDefinition{}
	//crd.SetName("websites.extensions.example.com")
	//crd.TypeMeta = metav1.TypeMeta{
	//	Kind:       "CustomResourceDefinition",
	//	APIVersion: "apiextensions.k8s.io/v1",
	//}
	//crd.Spec = v1.CustomResourceDefinitionSpec{
	//	Scope: v1.NamespaceScoped,
	//	Group: "extensions.example.com",
	//	Versions: []v1.CustomResourceDefinitionVersion{
	//		{
	//			Name:    "v1",
	//			Storage: true,
	//			Served:  true,
	//			Schema: &v1.CustomResourceValidation{
	//				OpenAPIV3Schema: &v1.JSONSchemaProps{
	//					Type: "object",
	//					Properties: map[string]v1.JSONSchemaProps{
	//						"spec": {
	//							Type: "object",
	//							Properties: map[string]v1.JSONSchemaProps{
	//								"gitRepo": {
	//									Type: "string",
	//								},
	//							},
	//						},
	//						"status": {
	//							Type: "object",
	//							Properties: map[string]v1.JSONSchemaProps{
	//								"phase": {
	//									Type: "string",
	//								},
	//								"myMessage": {
	//									Type: "string",
	//								},
	//							},
	//						},
	//					},
	//				},
	//			},
	//			Subresources: &v1.CustomResourceSubresources{
	//				Status: &v1.CustomResourceSubresourceStatus{},
	//			},
	//			AdditionalPrinterColumns: []v1.CustomResourceColumnDefinition{
	//				{
	//					Name:        "Status",
	//					Type:        "string",
	//					Description: "The status of website",
	//					JSONPath:    ".status.phase",
	//				}, {
	//					Name:     "Age",
	//					Type:     "date",
	//					JSONPath: ".metadata.creationTimestamp",
	//				},
	//			},
	//		},
	//	},
	//	Names: v1.CustomResourceDefinitionNames{
	//		Kind:     "Website",
	//		Singular: "website",
	//		Plural:   "websites",
	//		ShortNames: []string{
	//			"ws",
	//		},
	//	},
	//}
	//err = cl.Create(context.TODO(), crd, &client.CreateOptions{})
	//if err != nil {
	//	t.Fatalf("expected no err, got %s", err)
	//}

	websiteName := "test"
	website := &websitev1.Website{}
	website.SetNamespace(v1.NamespaceDefault)
	website.SetName(websiteName)
	err = cl.Get(context.TODO(), client.ObjectKey{
		Namespace: v1.NamespaceDefault,
		Name:      websiteName,
	}, website, &client.GetOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}
	if err == nil {
		// 如果cr存在则删除
		err := cl.Delete(context.TODO(), website, &client.DeleteOptions{
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
	website.Spec = websitev1.WebsiteSpec{
		GitRepo: "https://github.com/luksa/kubia-website-example.git",
	}
	err = cl.Create(context.TODO(), website, &client.CreateOptions{})
	if err != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	// 修改cr
	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		website = &websitev1.Website{}
		err := cl.Get(context.TODO(), client.ObjectKey{
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

		err = cl.Status().Update(context.TODO(), website, &client.SubResourceUpdateOptions{})
		return err
	})
	if retryErr != nil {
		t.Fatalf("expected no err, got %s", err)
	}

	website = &websitev1.Website{}
	err = cl.Get(context.TODO(), client.ObjectKey{
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

	// todo 添加event到cr
	//var kubeconfig string
	//if home := homedir.HomeDir(); home != "" {
	//	kubeconfig = filepath.Join(home, ".kube", "config")
	//} else {
	//	kubeconfig = ""
	//}
	//cf, err := clientcmd.BuildConfigFromFlags("", kubeconfig)
	//if err != nil {
	//	t.Fatalf("expected no err, got %s", err)
	//}
	//clientset, err := kubernetes.NewForConfig(cf)
	//if err != nil {
	//	t.Fatalf("expected no err, got %s", err)
	//}
	//eventBroadcaster := record.NewBroadcaster()
	//eventBroadcaster.StartStructuredLogging(4)
	//eventBroadcaster.StartRecordingToSink(&typedv1core.EventSinkImpl{Interface: clientset.CoreV1().Events("")})
	//// EventSource是events列表显示from字段
	//eventRecorder := eventBroadcaster.NewRecorder(scheme.Scheme, corev1.EventSource{Component: "demo-test"})
	//eventRecorder.Event(website, corev1.EventTypeWarning, "Reason Test", "Message Test")
	//eventBroadcaster.Shutdown()
	//time.Sleep(2 * time.Second)

	websiteList := &websitev1.WebsiteList{}
	err = cl.List(context.TODO(), websiteList, &client.ListOptions{})
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
}
