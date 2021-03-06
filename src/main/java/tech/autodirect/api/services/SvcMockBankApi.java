package tech.autodirect.api.services;

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

import tech.autodirect.api.interfaces.BankApiInterface;

/**
 * Responsible for calling the fictitious bank api to get a user's credit score.
 */
public class SvcMockBankApi {

    /**
     * Call the fictitious bank api to get a user's credit score.
     */
    public int getCreditScore(BankApiInterface bankApi, String userId) {
        return bankApi.getCreditScore(userId);
    }
}
