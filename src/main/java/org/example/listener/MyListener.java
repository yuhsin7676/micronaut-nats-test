package org.example.listener;

import io.micronaut.nats.jetstream.annotation.JetStreamListener;
import io.micronaut.nats.jetstream.annotation.PushConsumer;

@SuppressWarnings("unused")
@JetStreamListener
public class MyListener {

    @PushConsumer(deliverGroup = "test", value = "test-stream", subject = "test1.sub1", durable = "test-listener")
    public void consumer(Object event) {
        System.out.println(event);
    }

}
