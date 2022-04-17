package com.capitolis.monitor.common.util;

import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;

@Slf4j
public final class ProducerPropsLoader {

    public static Properties loadProperties(String producerPropsFile, String bootstrapServer) {
        Properties props = loadPropertiesFile(producerPropsFile);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return props;
    }

    private static Properties loadPropertiesFile(String resourceName) {
        Properties props = new Properties();
        try {
            props.load(Resources.getResource(resourceName).openStream());
        } catch (IOException e) {
            log.error("Failed to parse properties file [{}]", resourceName);
            System.exit(1);
        }
        return props;
    }
}
