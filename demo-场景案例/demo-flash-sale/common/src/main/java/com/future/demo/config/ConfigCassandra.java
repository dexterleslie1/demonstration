package com.future.demo.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCassandra {

    @Value("${cassandra.contact-points}")
    private String contactPoints;

    @Bean(destroyMethod = "close")
    public Cluster cluster() {
        Cluster.Builder builder = Cluster.builder();
        String[] contactPointsArr = this.contactPoints.split(",");
        for (int i = 0; i < contactPointsArr.length; i++) {
            String[] contactPointArr = contactPointsArr[i].split(":");
            builder.addContactPoint(contactPointArr[0]).withPort(Integer.parseInt(contactPointArr[1]));

        }
        return builder.build();
    }

    @Bean(destroyMethod = "close")
    public Session session(Cluster cluster) {
        return cluster.connect("demo");
    }
}
