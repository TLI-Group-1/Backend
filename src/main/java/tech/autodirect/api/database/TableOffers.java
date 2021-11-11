package tech.autodirect.api.database;

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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableOffers {
    public Connection db_conn;
    private String table_name = null;

    public TableOffers(String db_name) throws SQLException {
        // create a database connection object based on supplied database name
        this.db_conn = Conn.getConn(db_name);
    }

    public void newTable(String user_id) throws SQLException {
        // create the "offers" schema if it does not exist yet
        Statement stmt_create_schema = this.db_conn.createStatement();
        stmt_create_schema.executeUpdate(
        "CREATE SCHEMA IF NOT EXISTS offers;"
        );
        stmt_create_schema.close();

        // construct the name of the table following "offers.offer_userid"
        this.table_name = "offers.offer_" + user_id;

        // create and execute the SQL statement that will create an offer table
        Statement stmt = this.db_conn.createStatement();
        stmt.executeUpdate(
        "CREATE TABLE IF NOT EXISTS " + this.table_name + " (" +
                "offer_id       integer     NOT NULL PRIMARY KEY, " +
                "car_id         integer     NOT NULL, " +
                "amount         decimal(12) NOT NULL, " +
                "capital_sum    decimal(12) NOT NULL, " +
                "interest_sum   decimal(12) NOT NULL, " +
                "total_sum      decimal(12) NOT NULL, " +
                "interest_rate  real        NOT NULL, " +
                "term_mo        real        NOT NULL, " +
                "installments   jsonb       NOT NULL, " +
                "claimed        boolean     NOT NULL" +
            ");"
        );
        stmt.close();
    }
}
