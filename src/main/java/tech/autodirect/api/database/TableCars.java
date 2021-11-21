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

import tech.autodirect.api.interfaces.TableCarsInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TableCars extends Table implements TableCarsInterface {
    private final Connection db_conn;
    private final String table_name = "cars";

    public TableCars(String db_name) throws SQLException {
        this.db_conn = Conn.getConn(db_name);
    }

    @Override
    public List<Map<String, Object>> getAllCars() throws SQLException {
        // Construct and execute a prepared SQL statement selecting all cars
        PreparedStatement stmt = this.db_conn.prepareStatement("SELECT * FROM " + this.table_name);
        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> cars = resultSetToList(rs);
        stmt.close();
        return cars;
    }
}
