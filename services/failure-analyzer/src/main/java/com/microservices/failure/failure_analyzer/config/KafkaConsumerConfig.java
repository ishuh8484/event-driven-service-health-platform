package com.microservices.failure.failure_analyzer.config;

import com.microservices.failure.failure_analyzer.kafka.DeregistrationEvent;
import com.microservices.failure.failure_analyzer.kafka.FailureEvent;
import com.microservices.failure.failure_analyzer.kafka.HeartbeatEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

// Alag ConsumerFactory har event type ke liye — kyunki har topic ka DTO different hai
@Configuration
public class KafkaConsumerConfig {

    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "failure-analyzer-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    // error handler — 2 retry ke baad skip kar dega, infinite loop nahi hoga
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new FixedBackOff(1000L, 2L)
        );
        errorHandler.addNotRetryableExceptions(
                org.apache.kafka.common.errors.SerializationException.class
        );
        return errorHandler;
    }

    // --- Heartbeat consumer ---

    @Bean
    public ConsumerFactory<String, HeartbeatEvent> heartbeatConsumerFactory() {
        JsonDeserializer<HeartbeatEvent> jsonDeserializer =
                new JsonDeserializer<>(HeartbeatEvent.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                baseProps(),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HeartbeatEvent>
    heartbeatKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HeartbeatEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(heartbeatConsumerFactory());
        factory.setCommonErrorHandler(kafkaErrorHandler());
        return factory;
    }

    // --- Failure consumer ---

    @Bean
    public ConsumerFactory<String, FailureEvent> failureConsumerFactory() {
        JsonDeserializer<FailureEvent> jsonDeserializer =
                new JsonDeserializer<>(FailureEvent.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                baseProps(),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FailureEvent>
    failureKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FailureEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(failureConsumerFactory());
        factory.setCommonErrorHandler(kafkaErrorHandler());
        return factory;
    }

    // --- Deregistration consumer ---

    @Bean
    public ConsumerFactory<String, DeregistrationEvent> deregistrationConsumerFactory() {
        JsonDeserializer<DeregistrationEvent> jsonDeserializer =
                new JsonDeserializer<>(DeregistrationEvent.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                baseProps(),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeregistrationEvent>
    deregistrationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeregistrationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deregistrationConsumerFactory());
        factory.setCommonErrorHandler(kafkaErrorHandler());
        return factory;
    }
}
