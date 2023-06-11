package org.project.bolts.toSubscribers;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.filters.SimplePublicationFilter;
import org.project.models.ProtoSimplePublication;
import org.project.models.ProtoSimpleSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterSimpleSubscriptionBolt extends BaseRichBolt {

    public static final String ID = FilterSimpleSubscriptionBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(FilterSimpleSubscriptionBolt.class);
    private OutputCollector collector;
    final private Map<String, List<ProtoSimpleSubscription.SimpleSubscription>> subscriptions = new HashMap<>();
    final private Map<String, Integer> publicationMatched = new HashMap<>();

    @Override
    public void prepare(Map<String, Object> configuration, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        input.getFields().forEach((f) -> {
            var value = input.getValueByField(f);
            if (f.equals("SimplePublication")) {
                var sp = (ProtoSimplePublication.SimplePublication) value;

                subscriptions.forEach(
                        (k, v) -> {
                            var matched = v.stream().anyMatch((ss) -> SimplePublicationFilter.filter(ss).test(sp));
                            if (matched) {
                                // LOG.info("Simple subscription matched!");
                                // LOG.info("Subscriber ID: " + k);
                                // LOG.info("Simple publication:\n " + sp);

                                if (publicationMatched.containsKey(k)) {
                                    publicationMatched.put(k, publicationMatched.get(k) + 1);
                                } else {
                                    publicationMatched.put(k, 1);
                                }
                            }
                        }
                );
            }
            else if (f.equals("SimpleSubscription")) {
                try {
                    var ss = (ProtoSimpleSubscription.SimpleSubscription) value;
                    var key = ss.getSubscriptionId();
                    if (subscriptions.containsKey(key)) {
                        subscriptions.get(key).add(ss);
                    } else {
                        subscriptions.put(key, new ArrayList<>(List.of(ss)));
                    }
                }
                catch (Exception e) {
                    LOG.error("Error while processing simple subscription: " + e.getMessage());
                }
            }
        });

        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO: nothing to declare
    }

    @Override
    public void cleanup() {
        LOG.info("Simple subscription bolt cleanup");
        LOG.info("Publication matched: " + publicationMatched);
    }
}
