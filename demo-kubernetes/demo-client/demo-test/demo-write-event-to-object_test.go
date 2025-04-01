package main

import (
	"context"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/kubernetes/scheme"
	typedv1core "k8s.io/client-go/kubernetes/typed/core/v1"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/tools/record"
	"k8s.io/client-go/util/homedir"
	"log"
	"path/filepath"
	"testing"
	"time"
)

// https://dev.to/lucassha/writing-events-to-pod-objects-using-client-go-1p1h
func TestWriteEventToObject(t *testing.T) {
	kubeconfig := filepath.Join(homedir.HomeDir(), ".kube", "config")
	config, err := clientcmd.BuildConfigFromFlags("", kubeconfig)
	if err != nil {
		log.Println("connecting with config in-cluster")
		config, err = rest.InClusterConfig()
		if err != nil {
			log.Fatal(err)
		}
	}
	if err != nil {
		log.Fatal(err)
	}

	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		log.Fatal(err)
	}

	podName := "pod1"
	pod, err := clientset.CoreV1().Pods("default").Get(context.TODO(), podName, metav1.GetOptions{})
	if err != nil {
		log.Fatal(err)
	}

	// all the good events stuff is here
	eventBroadcaster := record.NewBroadcaster()
	eventBroadcaster.StartStructuredLogging(4)
	eventBroadcaster.StartRecordingToSink(&typedv1core.EventSinkImpl{Interface: clientset.CoreV1().Events("")})
	// EventSource是events列表显示from字段
	eventRecorder := eventBroadcaster.NewRecorder(scheme.Scheme, corev1.EventSource{Component: "my-source-test"})
	eventRecorder.Event(pod, corev1.EventTypeWarning, "Reason Test", "Message Test")
	eventBroadcaster.Shutdown()
	time.Sleep(2 * time.Second)
}
