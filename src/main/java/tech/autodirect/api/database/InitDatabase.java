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
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class InitDatabase {

    public static void main(String[] args) throws SQLException, IOException, CsvValidationException {
        // parse command-line arguments and obtain the CSV file path
        String csv_path = parseArgs(args);

        // obtain a database connection
        Connection db_conn = Conn.getConn();

        // create a cars table in the database given by db_conn
        createCarsTable(db_conn);

        // ingest the csv file given in the --csvpath parameter to the cars table
        // at the database given by db_conn
        ingestCsv(db_conn, csv_path);
    }

    private static String parseArgs(String[] args) {
        String csv_path = null;

        // cycle through command line arguments and process them
        for(int i = 0; i != args.length - 1; i++) {
            // help message
            if (args[i].equals("-h") || args[i].equals("--help")) {
                System.out.println("Help message. \n");
                System.exit(0);
            }
            // accept a path to the input CSV file
            else if (args[i].equals("--csvfile")) {
                csv_path = args[i+1];
            }
        }

        // report an error if the `--csvfile` option is not provided
        if (csv_path == null) {
            System.out.println("--csvfile option not provided.");
            System.exit(1);
        }
        return csv_path;
    }

    private static void createCarsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
        "CREATE TABLE IF NOT EXISTS public.cars (" +
            "brand character varying(50) NOT NULL," +
            "model character varying(50) NOT NULL," +
            "year integer NOT NULL," +
            "price decimal(12) NOT NULL," +
            "mileage real NULL," +
            "id integer NOT NULL PRIMARY KEY" +
            ");"
        );
        stmt.close();
    }

    private static void ingestCsv(Connection conn, String csv_path) throws IOException, CsvValidationException, SQLException {
        // read the CSV file
        CSVReader reader = new CSVReader(new FileReader(csv_path));

        // iterate through the CSV file
        String [] line = reader.readNext(); // skip header
        while ((line = reader.readNext()) != null) {
            if (line != null) {
                System.out.println(
                        line[0] + "\t" +  // id
                                line[1] + "\t" +  // price
                                line[2] + "\t" +  // brand
                                line[3] + "\t" +  // model
                                line[4] + "\t" +  // year
                                line[6] + "\t"    // mileage
                );

                PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO public.cars (id, brand, model, year, price, mileage)" +
                    "VALUES (?, ?, ?, ?, ?, ?)"
                );

                // populate values from the CSV into the prepared statement
                stmt.setInt(1, Integer.parseInt(line[0]));
                stmt.setString(2, line[2]);
                stmt.setString(3, line[3]);
                stmt.setInt(4, Integer.parseInt(line[4]));
                stmt.setBigDecimal(5, BigDecimal.valueOf(Long.parseLong(line[1])));
                stmt.setDouble(6, Double.parseDouble(line[6]));
                stmt.executeUpdate();

                stmt.close();
            }
        }
    }
}
