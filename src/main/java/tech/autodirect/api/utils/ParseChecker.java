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

/**
 * Responsible for checking whether certain values are parsable to certain types.
 */
public class ParseChecker {
    /**
     * Return whether a String is parsable to a double.
     */
    public static boolean isParsableToDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Return whether a String is parsable to an int.
     */
    public static boolean isParsableToInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}
