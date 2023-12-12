package org.example;

import io.micronaut.runtime.Micronaut;
import io.nats.client.Connection;
import io.nats.client.JetStreamManagement;
import io.nats.client.Nats;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    static List<String> before;
    static List<String> after;

    public static void main(String[] args) throws Exception{
        createOrUpdateStreams();
        before = getSubjects();
        new Thread(Main::printResults).start();
        Micronaut.run(Main.class, args);
    }

    static void createOrUpdateStreams() throws Exception {
        Connection connection = Nats.connect("nats://localhost:4222");
        JetStreamManagement management = connection.jetStreamManagement();
        StreamConfiguration config = StreamConfiguration.builder()
                .storageType(StorageType.Memory)
                .denyDelete(true)
                .name("test-stream")
                .subjects("test1.sub1", "test1.sub2")
                .build();
        if (management.getStreamNames().contains("test-stream")) management.updateStream(config);
        else management.addStream(config);
        connection.close();
    }

    static List<String> getSubjects() throws Exception {
        Connection connection = Nats.connect("nats://localhost:4222");
        StreamInfo info = connection.jetStreamManagement().getStreamInfo("test-stream");
        List<String> subjects = info.getConfiguration().getSubjects();
        connection.close();
        return subjects;
    }

    static void printResults() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {}

        try {
            after = getSubjects();
        } catch (Exception e) {}

        System.out.println("before: " + before);
        System.out.println("after: " + after);
    }



}