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

import org.springframework.web.server.ResponseStatusException;
import tech.autodirect.api.interfaces.TableCarsInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TableCars extends Table implements TableCarsInterface {
    private final Connection dbConn;
    private final String schemaName = "public";
    private final String tableName = "cars";

    /**
     * Create a new TableCars object with a public database connection object.
     *
     * @param dbName : name of the database to connect to
     */
    public TableCars(String dbName) throws SQLException, ClassNotFoundException {
        this.dbConn = Conn.getConn(dbName);
    }

    @Override
    public List<Map<String, Object>> getAllCars() throws SQLException {
        return getAllEntries(schemaName, tableName, dbConn);
    }

    @Override
    public Map<String, Object> getCarById(int carId) throws SQLException, ResponseStatusException {
        return getEntryById(carId, schemaName, tableName, dbConn, "car");
    }

    public boolean checkCarExists(int carId) throws SQLException {
        return checkEntryExists(carId, schemaName, tableName, dbConn, "car");
    }
}
