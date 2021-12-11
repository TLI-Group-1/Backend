package tech.autodirect.api.upstream;

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

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tech.autodirect.api.interfaces.BankApiInterface;



public class BankApi implements BankApiInterface {

    /**
     * Get the credit score for userId (uses last 3 digits of userId as credit score).
     */
    public int getCreditScore(String userId) throws ResponseStatusException {
        try {
            return Integer.parseInt(userId.substring(userId.length() - 3));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid credit score");
        }
    }
}
