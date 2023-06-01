package org.project.bolts;

import com.esotericsoftware.kryo.Kryo;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.project.models.ProtoSimplePublication;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Predicate;

class SerializablePredicate<T> implements Serializable {
    private Predicate<T> rawPredicate;

    public SerializablePredicate(Predicate<T> predicate) {
        this.rawPredicate = predicate;
    }

    public Predicate<T> getRawPredicate() {
        return rawPredicate;
    }

    // Other methods if needed

    // Optional: Override readObject and writeObject methods to customize serialization
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(rawPredicate);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        rawPredicate = (Predicate<T>) in.readObject();
    }
}

public class FilterSimpleAnomalyBolt extends BaseRichBolt {

    public static final String ID = FilterSimpleAnomalyBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(FilterSimpleAnomalyBolt.class);
    final private SerializablePredicate<ProtoSimplePublication.SimplePublication> predicate;
    private OutputCollector collector;

    public FilterSimpleAnomalyBolt(Predicate<ProtoSimplePublication.SimplePublication> predicate) {
        Kryo kryo = new Kryo();
        kryo.register(SerializablePredicate.class);
        this.predicate = new SerializablePredicate<>(predicate);
    }

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector collector) {
        topologyContext.registerCounter(ID + "_received_tuples");

        /*
        topologyContext.registerGauge(ID + "_received_tuples_per_second", () -> {
            var count = topologyContext.getCounter(ID + "_received_tuples").getCount();
            topologyContext.getCounter(ID + "_received_tuples").clear();
            return count;
        });
        */

        topologyContext.registerHistogram(ID + "_received_tuples_per_second");
        topologyContext.registerMeter(ID + "_received_tuples_per_second");

        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        input.getFields().forEach((f) -> {
            var value = input.getValueByField(f);
            if (f.equals("SimplePublication")) {
                var sp = (ProtoSimplePublication.SimplePublication) (value);
                if (this.predicate.getRawPredicate().test(sp)) {
                    this.collector.emit(input, new Values(sp));
                } else {
                    LOG.info("Field <" + f + "> Value <" + sp + "> (Filtered!)");
                }
            } else {
                LOG.info("Field (Unknown!) <" + f + "> Value (Unknown!) <" + value + ">");
            }
        });

        this.collector.ack(input);
    }

    @Override
    public void cleanup() {
        LOG.info("Simple anomaly events received!");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("SimplePublication"));
    }
}
