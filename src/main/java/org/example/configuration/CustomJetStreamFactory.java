package org.example.configuration;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.nats.connect.NatsConnectionFactoryConfig;
import io.micronaut.nats.jetstream.JetStreamFactory;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;

@Factory
@Replaces(factory = JetStreamFactory.class)
public class CustomJetStreamFactory {

    private final BeanContext beanContext;

    @Inject
    public CustomJetStreamFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Singleton
    @EachBean(NatsConnectionFactoryConfig.class)
    JetStreamManagement jetStreamManagement(NatsConnectionFactoryConfig config) throws IOException {
        if (config.getJetstream() != null) {
            return getConnectionByName(config.getName()).jetStreamManagement(
                    config.getJetstream().toJetStreamOptions());
        }
        return null;
    }

    @Singleton
    @EachBean(NatsConnectionFactoryConfig.class)
    JetStream jetStream(NatsConnectionFactoryConfig config) throws IOException, JetStreamApiException {
        if (config.getJetstream() != null) {
            Connection connection = getConnectionByName(config.getName());

            final JetStreamManagement jetStreamManagement = getConnectionByName(config.getName()).jetStreamManagement(
                    config.getJetstream().toJetStreamOptions());

            // initialize the given stream configurations
            for (NatsConnectionFactoryConfig.JetStreamConfiguration.StreamConfiguration stream : config.getJetstream()
                    .getStreams()) {
                final StreamConfiguration streamConfiguration = stream.toStreamConfiguration();
                if (jetStreamManagement.getStreamNames().contains(streamConfiguration.getName())) {
                    StreamInfo streamInfo = jetStreamManagement.getStreamInfo(streamConfiguration.getName());

                    // begin added custom code
                    StreamConfiguration current = streamInfo.getConfiguration();
                    for (String sub : current.getSubjects()) {
                        if (!streamConfiguration.getSubjects().contains(sub)) {
                            streamConfiguration.getSubjects().add(sub);
                        }
                    }
                    // end added custom code

                    if (!streamInfo.getConfiguration().equals(streamConfiguration)) {
                        jetStreamManagement.updateStream(streamConfiguration);
                    }
                } else {
                    jetStreamManagement.addStream(streamConfiguration);
                }
            }

            return connection.jetStream(config.getJetstream().toJetStreamOptions());
        }
        return null;
    }

    private Connection getConnectionByName(String connectionName) {
        return beanContext.findBean(Connection.class, Qualifiers.byName(connectionName))
                .orElseThrow(() -> new IllegalStateException(
                        "No nats connection found for " + connectionName));
    }

}
