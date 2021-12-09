package tech.autodirect.api.utils;

import java.math.BigDecimal;
import java.security.InvalidParameterException;

/**
 * Responsible for converting between types.
 */
public class TypeConvert {

    /**
     * Convert object to double. If not castable to double, throw InvalidParameterException.
     */
    public static double toDouble(Object object) throws InvalidParameterException {
        if (object instanceof BigDecimal) {
            return ((BigDecimal) object).doubleValue();
        } else if (object instanceof Double) {
            return (double) object;
        } else {
            throw new InvalidParameterException();
        }
    }

}
