package com.future.demo.fabric8;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

import java.util.Arrays;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        KubernetesClient client = null;
        try {
            client = new KubernetesClientBuilder().build();

            // list pods in the default namespace
            PodList podList = client.pods().inNamespace("default").list();
            podList.getItems().forEach(o -> System.out.println("Found pod: " + o.getMetadata().getName()));

            // create a pod
            System.out.println("Creating a pod");
            Pod pod = client.pods().inNamespace("default")
                    .create(new PodBuilder().withNewMetadata().withName("programmatically-created-pod")
                            .endMetadata().withNewSpec().addNewContainer().withName("main").withImage("busybox")
                            .withCommand(Arrays.asList("sh", "-c", "sleep 7200")).endContainer().endSpec().build());
            System.out.println("Created pod: " + pod);

            // edit the pod (add a label to it)
            client.pods().inNamespace("default")
                    .withName("programmatically-created-pod")
                    .edit(o -> new PodBuilder(o).editMetadata().addToLabels("foo", "bar").endMetadata().build());
            System.out.println("Added label foo=bar to pod");

            System.out.println("Waiting 60 seconds before deleting pod...");
            Thread.sleep(60000);

        } finally {
            if(client != null) {
                client.pods().inNamespace("default").withName("programmatically-created-pod").delete();
                System.out.println("Deleted the pod");

                client.close();
            }
        }
    }
}
