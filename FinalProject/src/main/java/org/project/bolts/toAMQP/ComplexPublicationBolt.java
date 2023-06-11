package org.project.bolts.toAMQP;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoSimplePublication;
import org.project.rabbit.ConnectionManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class ComplexPublicationBolt extends BaseRichBolt {

    public static final String ID = ComplexPublicationBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(ComplexPublicationBolt.class);
    private OutputCollector collector;
    private Channel channel;

    void setupChannel() {
        final ConnectionManager cm = ConnectionManager.getInstance();
        this.channel = cm.GetChannel(
                (int) DEFAULT_PREFETCH_COUNT,
                COMPLEX_PUBLICATION_EXCHANGE_NAME,
                COMPLEX_PUBLICATION_QUEUE_NAME,
                COMPLEX_PUBLICATION_ROUTING_KEY);
    }

    @Override
    public void prepare(Map<String, Object> configuration, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        setupChannel();
    }

    @Override
    public void execute(Tuple input) {
        var cp = (ProtoComplexPublication.ComplexPublication) input.getValueByField("ComplexPublication");
        try {
            channel.basicPublish(
                    COMPLEX_PUBLICATION_EXCHANGE_NAME, COMPLEX_PUBLICATION_ROUTING_KEY,
                    MessageProperties.PERSISTENT_BASIC,
                    cp.toByteArray());
            channel.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);
            // LOG.info(" [x] Sent: " + cp);
        } catch (AlreadyClosedException | InterruptedException e) {
            collector.reportError(e);
            e.printStackTrace();
            setupChannel();
            this.collector.fail(input);
            return;
        } catch (Exception e) {
            collector.reportError(e);
            e.printStackTrace();
            this.collector.fail(input);
            return;
        }
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO: nothing to declare
    }

    @Override
    public void cleanup() {
        try {
            this.channel.close();
        } catch (AlreadyClosedException e) {
            collector.reportError(e);
            LOG.warn("Channel already closed", e);
        } catch (IOException | TimeoutException e) {
            collector.reportError(e);
            throw new RuntimeException(e);
        }
    }
}
