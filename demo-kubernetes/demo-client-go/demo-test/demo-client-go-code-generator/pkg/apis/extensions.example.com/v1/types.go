package v1

import metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

// NOTE: 下面两个注释之间不能够空行
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
	metav1.TypeMeta   `json:"inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`
	Items             []Website `json:"items"`
}
