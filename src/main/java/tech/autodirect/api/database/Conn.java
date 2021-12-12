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

import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Responsible for connecting to the database give the database name.
 */
public class Conn {
    // list of environment variables to fetch
    private static final String[] ENVIRONMENT_VARIABLES = {
        "AUTODIRECT_DB_HOST",
        "AUTODIRECT_DB_PORT",
        "AUTODIRECT_DB_SSL",
        "AUTODIRECT_DB_USER",
        "AUTODIRECT_DB_PASS"
    };
    // database parameters
    private static final Map<String, String> DB_PARAMS = new HashMap<>();

    /**
     * Get a Connection object connecting to the database given by dbName.
     */
    public static Connection getConn(String dbName)
            throws MissingEnvironmentVariableException, SQLException, ClassNotFoundException {
        // get the database configuration parameters from environment variables
        getEnvVars();

        // construct the DB URL according to "jdbc:postgresql://host:port/database"
        String dbUrl = "jdbc:postgresql://" +
            DB_PARAMS.get("AUTODIRECT_DB_HOST") + ":" +
            DB_PARAMS.get("AUTODIRECT_DB_PORT") + "/" +
            dbName;

        // supply the remaining DB parameters as properties
        Properties props = new Properties();
        props.setProperty("ssl", DB_PARAMS.get("AUTODIRECT_DB_SSL"));
        props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        props.setProperty("user", DB_PARAMS.get("AUTODIRECT_DB_USER"));
        props.setProperty("password", DB_PARAMS.get("AUTODIRECT_DB_PASS"));

        // ensure PostgreSQL driver is present
        Class.forName("org.postgresql.Driver");

        // print connection info and return the obtained DB connection object
        Connection conn = DriverManager.getConnection(dbUrl, props);
        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("\n" + meta.getDatabaseProductName());
        System.out.println(meta.getDatabaseProductVersion() + "\n");

        return conn;
    }

    /**
     * Get necessary environment variables.
     */
    private static void getEnvVars() throws MissingEnvironmentVariableException {
        // attempt to obtain the relevant environment variables
        for (String varName : ENVIRONMENT_VARIABLES) {
            String varValue = System.getenv(varName);
            if (varValue == null) {
                throw new MissingEnvironmentVariableException(
                    "\n\n\t> \"" + varName +
                    "\" not specified in environment variables. \n"
                );
            }
            else {
                DB_PARAMS.put(varName, varValue);
            }
        }
    }
}
