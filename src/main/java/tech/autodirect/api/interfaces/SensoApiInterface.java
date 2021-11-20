package tech.autodirect.api.interfaces;

import java.io.IOException;
import java.util.Map;

public interface SensoApiInterface {
    public Map<String, Object> getLoanOffer() throws IOException, InterruptedException;
}
