package org.project.bolts.transformers;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.filters.ComplexPublicationFilter;
import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoComplexSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterComplexSubscriptionBolt extends BaseRichBolt {

    public static final String ID = FilterComplexSubscriptionBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(FilterComplexSubscriptionBolt.class);
    private OutputCollector collector;
    final private Map<String, List<ProtoComplexSubscription.ComplexSubscription>> subscriptions = new HashMap<>();
    final private Map<String, Integer> publicationMatched = new HashMap<>();

    @Override
    public void prepare(Map<String, Object> configuration, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        input.getFields().forEach((f) -> {
            var value = input.getValueByField(f);
            if (f.equals("ComplexPublication")) {
                var cp = (ProtoComplexPublication.ComplexPublication) value;

                subscriptions.forEach(
                    (k, v) -> {
                        var matched = v.stream().anyMatch((cs) -> ComplexPublicationFilter.filter(cs).test(cp));
                        if (matched) {
                            LOG.info("Complex subscription matched!");
                            LOG.info("Subscriber ID: " + k);
                            LOG.info("Complex publication:\n " + cp);

                            if (publicationMatched.containsKey(k)) {
                                publicationMatched.put(k, publicationMatched.get(k) + 1);
                            } else {
                                publicationMatched.put(k, 1);
                            }
                        }
                    }
                );
              }
            else if (f.equals("ComplexSubscription")) {
                try {
                    var cs = (ProtoComplexSubscription.ComplexSubscription) value;
                    var key = cs.getSubscriptionId();
                    if (subscriptions.containsKey(key)) {
                        subscriptions.get(key).add(cs);
                    } else {
                        subscriptions.put(key, new ArrayList<>(List.of(cs)));
                    }
                }
                catch (Exception e) {
                    LOG.error("Error while processing complex subscription: " + e.getMessage());
                }
            }
        });

        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public void cleanup() {
        LOG.info("Complex subscription bolt cleanup");
        LOG.info("Publication matched: " + publicationMatched);
    }
}
