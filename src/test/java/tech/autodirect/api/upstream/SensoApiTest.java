package tech.autodirect.api.upstream;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SensoApiTest {

    @Test
    void queryApi() {
        try {
            HashMap<String, Object> responseMap = SensoApi.queryApi(
                "10000",
                "700",
                "300",
                "Honda",
                "Civic",
                "2020",
                "10",
                "1000",
                "1000"
            );
            System.out.println(responseMap);
            assert (int) responseMap.get("status") == 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            assert false;
        }

    }
}