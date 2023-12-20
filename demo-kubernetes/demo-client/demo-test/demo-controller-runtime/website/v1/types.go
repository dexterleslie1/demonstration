package websitev1

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
)

// Website enables declarative updates for Pods and ReplicaSets.
type Website struct {
	metav1.TypeMeta `json:",inline"`
	// Standard object's metadata.
	// More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#metadata
	// +optional
	metav1.ObjectMeta `json:"metadata,omitempty" protobuf:"bytes,1,opt,name=metadata"`

	// Specification of the desired behavior of the Deployment.
	// +optional
	Spec WebsiteSpec `json:"spec,omitempty"`

	Status WebsiteStatus `json:"status,omitempty"`
}

// WebsiteSpec is the specification of the desired behavior of the Deployment.
type WebsiteSpec struct {
	GitRepo string `json:"gitRepo,omitempty"`
}

// WebsiteStatus is the most recently observed status of the Deployment.
type WebsiteStatus struct {
	Phase string `json:"phase,omitempty"`

	MyMessage string `json:"myMessage,omitempty"`
}

// WebsiteList is a list of Deployments.
type WebsiteList struct {
	metav1.TypeMeta `json:",inline"`
	// Standard list metadata.
	// +optional
	metav1.ListMeta `json:"metadata,omitempty"`

	// Items is the list of Deployments.
	Items []Website `json:"items"`
}

func (w WebsiteList) DeepCopyObject() runtime.Object {
	//TODO implement me
	panic("implement me")
}

func (w Website) DeepCopyObject() runtime.Object {
	//TODO implement me
	panic("implement me")
}
