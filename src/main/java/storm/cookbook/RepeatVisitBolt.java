package storm.cookbook;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * User: domenicosolazzo
 */
public class RepeatVisitBolt extends BaseRichBolt {

    private OutputCollector collector;

    private Jedis jedis;
    private String host;
    private int port;

    @Override
    public void prepare(Map conf, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = collector;
        host = conf.get(Conf.REDIS_HOST_KEY).toString();
        port = Integer.valueOf(conf.get(Conf.REDIS_PORT_KEY).toString());
        connectToRedis();
    }

    private void connectToRedis(){
        jedis = new Jedis(host, port);
        jedis.connect();
    }

    public boolean isConnected(){
        if(jedis == null){
            return false;
        }
        return jedis.isConnected();
    }


    @Override
    public void execute(Tuple tuple) {
        String ip = tuple.getStringByField(storm.cookbook.Fields.IP);
        String clientKey = tuple.getStringByField(Fields.CLIENT_KEY);
        String url = tuple.getStringByField(storm.cookbook.Fields.URL);
        String key = url + ":" + clientKey;
        String value = jedis.get(key);
        if(value == null){
            jedis.set(key, "visited");
            collector.emit(new Values(clientKey, Boolean.TRUE.toString()));
        }else{
            collector.emit(new Values(clientKey, Boolean.FALSE.toString()));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new backtype.storm.tuple.Fields(Fields.CLIENT_KEY,
                Fields.URL, Fields.UNIQUE));
    }
}
