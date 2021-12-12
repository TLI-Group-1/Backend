package tech.autodirect.api.utils;

/*
Copyright (c) 2021 Ruofan Chen, Samm Du, Nada Eldin, Shalev Lifshitz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
