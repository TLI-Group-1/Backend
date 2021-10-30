package tech.autodirect.api.upstream;

/**
 * The SensoRateQuery class represents a single query to the SensoApi/rate api and contains
 * all the information that will be passed as arguments when querying.
 */
public class SensoRateQuery {
    private double loanAmount;
    private int creditScore;
    private double budget;
    private String vehicleMake;
    private String vehicleModel;
    private int vehicleYear;
    private int vehicleKms;

    public SensoRateQuery(double loanAmount, int creditScore, double budget, String vehicleMake,
                          String vehicleModel, int vehicleYear, int vehicleKms) {
        this.loanAmount = loanAmount;
        this.creditScore = creditScore;
        this.budget = budget;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleYear = vehicleYear;
        this.vehicleKms = vehicleKms;
    }
}
