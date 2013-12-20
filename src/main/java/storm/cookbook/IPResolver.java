package storm.cookbook;

import org.json.simple.JSONObject;

/**
 * User: domenicosolazzo
 */
public interface IPResolver {
    public JSONObject resolveIP(String ip);
}
