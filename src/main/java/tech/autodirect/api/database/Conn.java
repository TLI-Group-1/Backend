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
import java.util.Properties;

public class Conn {
    // list of environment variables to fetch
    private static String[] environment_variables = {
        "AUTODIRECT_DB_HOST",
        "AUTODIRECT_DB_PORT",
        "AUTODIRECT_DB_SSL",
        "AUTODIRECT_DB_USER",
        "AUTODIRECT_DB_PASS"
    };
    // database parameters
    private static HashMap<String, String> db_params = new HashMap<>();

    public static Connection getConn(String db_name)
            throws MissingEnvironmentVariableException, SQLException, ClassNotFoundException {
        // get the database configuration parameters from environment variables
        getEnvVars();

        // construct the DB URL according to "jdbc:postgresql://host:port/database"
        String db_url = "jdbc:postgresql://" +
            db_params.get("AUTODIRECT_DB_HOST") + ":" +
            db_params.get("AUTODIRECT_DB_PORT") + "/" +
            db_name;

        // supply the remaining DB parameters as properties
        Properties props = new Properties();
        props.setProperty("ssl", db_params.get("AUTODIRECT_DB_SSL"));
        props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        props.setProperty("user", db_params.get("AUTODIRECT_DB_USER"));
        props.setProperty("password", db_params.get("AUTODIRECT_DB_PASS"));

        // ensure PostgreSQL driver is present
        Class.forName("org.postgresql.Driver");

        // print connection info and return the obtained DB connection object
        Connection conn = DriverManager.getConnection(db_url, props);
        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("\n" + meta.getDatabaseProductName());
        System.out.println(meta.getDatabaseProductVersion() + "\n");

        return conn;
    }

    private static void getEnvVars() throws MissingEnvironmentVariableException {
        // attempt to obtain the relevant environment variables
        for (String var_name : environment_variables) {
            String var_value = System.getenv(var_name);
            if (var_value == null) {
                throw new MissingEnvironmentVariableException(
                    "\n\n\t> \"" + var_name +
                    "\" not specified in environment variables. \n"
                );
            }
            else {
                db_params.put(var_name, var_value);
            }
        }
    }
}
