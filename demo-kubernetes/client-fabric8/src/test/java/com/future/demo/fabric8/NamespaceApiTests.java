package com.future.demo.fabric8;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NamespaceApiTests {
    KubernetesClient client = null;

    @Before
    public void before() {
        client = new KubernetesClientBuilder().build();
    }

    @After
    public void after() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void test() throws InterruptedException {
        String namespaceName = "demo-my-test";
        // 删除已经存在的namespace
        this.client.namespaces().withName(namespaceName).delete();
        this.client.namespaces().withName(namespaceName).waitUntilCondition(Objects::isNull, 30, TimeUnit.SECONDS);

        Namespace namespace = client.namespaces().resource(new NamespaceBuilder()
                .withNewMetadata()
                .withName(namespaceName)
                .endMetadata()
                .build()).create();
        Assert.assertEquals(namespaceName, namespace.getMetadata().getName());
        this.client.namespaces().withName(namespaceName).waitUntilReady(30, TimeUnit.SECONDS);

        namespace = this.client.namespaces().withName(namespaceName).get();
        Assert.assertEquals(namespaceName, namespace.getMetadata().getName());

        NamespaceList namespaceList = this.client.namespaces().list();
        namespace = namespaceList.getItems().stream().filter(o -> o.getMetadata().getName().equals(namespaceName)).collect(Collectors.toList()).get(0);
        Assert.assertEquals(namespaceName, namespace.getMetadata().getName());

        List<StatusDetails> statusDetailsList = this.client.namespaces().withName(namespaceName).delete();
        Assert.assertEquals(1, statusDetailsList.size());
        Assert.assertEquals(namespaceName, statusDetailsList.get(0).getName());
    }
}
