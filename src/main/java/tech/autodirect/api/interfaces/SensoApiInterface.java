package tech.autodirect.api.interfaces;

import java.io.IOException;
import java.util.Map;

public interface SensoApiInterface {
    /**
     * Query the Senso /rate Api to get a loan offer.
     *
     * @return A Map that includes the body of the query result as well as the status number of the https query.
     */
    Map<String, Object> getLoanOffer(
            String loanAmount,
            String creditScore,
            String budget,
            String vehicleMake,
            String vehicleModel,
            String vehicleYear,
            String vehicleKms,
            String listPrice,
            String downPayment
    ) throws IOException, InterruptedException;
}
