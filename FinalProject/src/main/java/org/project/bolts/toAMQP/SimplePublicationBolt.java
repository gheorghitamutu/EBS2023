package org.project.bolts.toAMQP;

import com.rabbitmq.client.*;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.models.ProtoSimplePublication;
import org.project.rabbit.ConnectionManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class SimplePublicationBolt extends BaseRichBolt {

    public static final String ID = SimplePublicationBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(SimplePublicationBolt.class);
    private OutputCollector collector;
    private Channel channel;

    void setupChannel() {
        final ConnectionManager cm = ConnectionManager.getInstance();
        this.channel = cm.GetChannel(
                (int) DEFAULT_PREFETCH_COUNT,
                SIMPLE_PUBLICATION_EXCHANGE_NAME,
                SIMPLE_PUBLICATION_QUEUE_NAME,
                SIMPLE_PUBLICATION_ROUTING_KEY);
    }

    @Override
    public void prepare(Map<String, Object> configuration, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        setupChannel();
    }

    @Override
    public void execute(Tuple input) {
        var sp = (ProtoSimplePublication.SimplePublication) input.getValueByField("SimplePublication");
        try {
            channel.basicPublish(
                    SIMPLE_PUBLICATION_EXCHANGE_NAME, SIMPLE_PUBLICATION_ROUTING_KEY,
                    MessageProperties.PERSISTENT_BASIC,
                    sp.toByteArray());
            channel.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);
            // LOG.info(" [x] Sent: " + sp);
        } catch (AlreadyClosedException | InterruptedException e) {
            e.printStackTrace();
            setupChannel();
            this.collector.fail(input);
            return;
        } catch (Exception e) {
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
        }
        catch (AlreadyClosedException e) {
            LOG.warn("Channel already closed", e);
        }
        catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
