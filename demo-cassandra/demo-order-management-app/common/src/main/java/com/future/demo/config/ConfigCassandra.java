package com.future.demo.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCassandra {

    @Value("${cassandra.contact-points}")
    String contactPoints;

    @Bean(destroyMethod = "close")
    public Cluster cluster() {
        Cluster.Builder builder = Cluster.builder();
        String[] contactPointsArrary = contactPoints.split(",");
        for (int i = 0; i < contactPointsArrary.length; i++) {
            String contactPoint = contactPointsArrary[i];
            builder = builder.addContactPoint(contactPoint.split(":")[0])
                    .withPort(Integer.parseInt(contactPoint.split(":")[1]));
        }
        return builder.build();
    }

    @Bean(destroyMethod = "close")
    public Session session(Cluster cluster) {
        return cluster.connect("demo");
    }
}
