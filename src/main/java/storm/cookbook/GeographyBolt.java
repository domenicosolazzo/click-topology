package storm.cookbook;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * User: domenicosolazzo
 */
public class GeographyBolt extends BaseRichBolt {

    private IPResolver resolver;

    private OutputCollector collector;

    public GeographyBolt(IPResolver resolver){
        this.resolver = resolver;
    }
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String ip = tuple.getStringByField(storm.cookbook.Fields.IP);
        JSONObject json = resolver.resolveIP(ip);
        String city = (String) json.get(Fields.CITY);
        String country = (String) json.get(Fields.COUNTRY_NAME);
        collector.emit(new Values(country, city));

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new backtype.storm.tuple.Fields(Fields.COUNTRY, Fields.CITY));

    }
}
