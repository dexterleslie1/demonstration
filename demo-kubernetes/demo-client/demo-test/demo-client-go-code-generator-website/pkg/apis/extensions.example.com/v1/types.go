package v1

import metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

// NOTE: 下面两个注释之间不能够空行
// TODO 搞懂+genclient和+k8s底层原理
// 这个标签是用于告诉client-gen需要为这个类型创建一个客户端clientset
// informer-gen和lister-gen都会处理client-gen的 // +genclient 标签，
// 所有指定了要生成客户端的类型都会自动生成出相对应的Informer和Lister
// +genclient
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object

type Website struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`
	Spec              WebsiteSpec   `json:"spec,omitempty"`
	Status            WebsiteStatus `json:"status,omitempty"`
}

type WebsiteSpec struct {
	GitRepo string `json:"gitRepo,omitempty"`
}
type WebsiteStatus struct {
	Phase     string `json:"phase,omitempty"`
	MyMessage string `json:"myMessage,omitempty"`
}

// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object

type WebsiteList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata"`
	Items           []Website `json:"items"`
}
