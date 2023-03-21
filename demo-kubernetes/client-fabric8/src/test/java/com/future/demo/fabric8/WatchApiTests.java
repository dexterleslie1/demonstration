package com.future.demo.fabric8;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class WatchApiTests {
    KubernetesClient client = null;
    KubernetesClient clientWatch = null;

    @Before
    public void before() {
        this.client = new KubernetesClientBuilder().build();
        this.clientWatch = new KubernetesClientBuilder().build();
    }

    @After
    public void after() {
        if (this.client != null) {
            this.client.close();
        }

        if(this.clientWatch != null) {
            this.clientWatch.close();
        }
    }

    @Test
    public void test() {
        String namespaceName = "default";
        String podName = "demo-my-test-pod";

        this.clientWatch.pods().inNamespace(namespaceName).watch(new Watcher<Pod>() {
            @Override
            public void eventReceived(Action action, Pod resource) {
                System.out.println(action.toString() + "," + resource.toString());
            }

            @Override
            public void onClose(WatcherException cause) {

            }
        });

        this.client.pods().inNamespace(namespaceName).withName(podName).delete();
        this.client.pods().inNamespace(namespaceName).withName(podName).waitUntilCondition(Objects::isNull, 2, TimeUnit.MINUTES);

        Assert.assertEquals(0, this.client.pods().inNamespace("default").list().getItems().stream().filter(o -> o.getMetadata().getName().equals(podName)).count());

        Pod pod = this.client.pods().inNamespace(namespaceName).resource(
                new PodBuilder().withNewMetadata().withName(podName).endMetadata()
                        .withNewSpec()
                        .addNewContainer()
                        .withName("container-" + podName)
                        .withImage("busybox")
                        .withCommand("sh", "-c", "sleep 7200")
                        .endContainer()
                        .endSpec()
                        .build()
        ).create();
        Assert.assertEquals(podName, pod.getMetadata().getName());
        this.client.pods().inNamespace(namespaceName).withName(podName).waitUntilReady(2, TimeUnit.MINUTES);

        // 编辑pod添加label
        pod = this.client.pods().inNamespace(namespaceName).withName(podName).edit(o -> new PodBuilder(o).editMetadata().addToLabels("foo", "bar").endMetadata().build());
        Assert.assertEquals(podName, pod.getMetadata().getName());
        Assert.assertEquals(1, pod.getMetadata().getLabels().size());
        Assert.assertTrue(pod.getMetadata().getLabels().containsKey("foo"));
        Assert.assertEquals("bar", pod.getMetadata().getLabels().get("foo"));
        pod = this.client.pods().inNamespace(namespaceName).withName(podName).get();
        Assert.assertEquals(podName, pod.getMetadata().getName());
        Assert.assertEquals(1, pod.getMetadata().getLabels().size());
        Assert.assertTrue(pod.getMetadata().getLabels().containsKey("foo"));
        Assert.assertEquals("bar", pod.getMetadata().getLabels().get("foo"));

        Assert.assertEquals(1, this.client.pods().inNamespace(namespaceName).withName(podName).delete().size());
    }
}
