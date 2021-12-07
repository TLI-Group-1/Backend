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

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class InitDatabase {
    private static final String dbName = "autodirect";

    public static void main(String[] args)
            throws SQLException, IOException, CsvValidationException, ClassNotFoundException {
        // parse command-line arguments and obtain the CSV file path
        String csvPath = parseArgs(args);

        // obtain a database connection
        Connection dbConn = Conn.getConn(dbName);

        // create a cars table in the database given by dbConn
        createCarsTable(dbConn);

        // create a users table in the database given by dbConn
        createUsersTable(dbConn);

        // ingest the csv file given in the --csvpath parameter to the cars table
        // at the database given by dbConn
        ingestCarsCsv(dbConn, csvPath);

        // close the database connection at the end
        dbConn.close();
    }

    private static String parseArgs(String[] args) {
        String csvPath = null;

        // exit immediately if no arguments are provided
        if (args.length == 0) {
            System.out.println("\n[!] no arguments provided.");
            exitWithHelp(1);
        }

        // cycle through command line arguments and process them
        for (int i = 0; i != args.length - 1; i++) {
            // help message
            if (args[i].equals("-h") || args[i].equals("--help")) {
                exitWithHelp(0);
            }
            // accept a path to the input CSV file, if a path is supplied
            else if (args[i].equals("--csvfile") && (i + 1 < args.length)) {
                csvPath = args[i + 1];
            }
        }

        // report an error if the `--csvfile` option is not provided
        if (csvPath == null) {
            System.out.println("\n[!] --csvfile option not provided.");
            exitWithHelp(1);
        }

        return csvPath;
    }

    private static void exitWithHelp(int exitCode) {
        String helpText = "\nUsage: InitDatabase [options] <file> \n" +
                "\t -h --help\tdisplay this help message and exit\n" +
                "\t --csvfile\tspecify a CSV file path which contains the cars dataset\n" +
                "\nExample — use 'cars.csv' in the current working directory:\n" +
                "\t InitDatabase --csvfile ./cars.csv\n";
        System.out.println(helpText);
        System.exit(exitCode);
    }

    private static void createCarsTable(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS public.cars (" +
                        "id         serial      NOT NULL PRIMARY KEY, " +
                        "brand      varchar(50) NOT NULL, " +
                        "model      varchar(50) NOT NULL, " +
                        "year       integer     NOT NULL, " +
                        "price      decimal(12) NOT NULL, " +
                        "mileage    real        NULL" +
                        ");"
        );
        stmt.executeUpdate();
        stmt.close();
    }

    private static void createUsersTable(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS public.users (" +
                        "user_id        varchar(50) NOT NULL PRIMARY KEY, " +
                        "credit_score   integer     NULL, " +
                        "down_payment   decimal(12) NULL, " +
                        "budget_mo      decimal(12) NULL, " +
                        "offers_table   varchar(50) NULL" +
                        ");"
        );
        stmt.executeUpdate();
        stmt.close();
    }

    private static void ingestCarsCsv(Connection conn, String csvPath)
            throws IOException, CsvValidationException, SQLException {
        // read the CSV file
        CSVReader reader = new CSVReader(new FileReader(csvPath));

        // iterate through the CSV file
        String[] line = reader.readNext(); // skip header
        while ((line = reader.readNext()) != null) {
            System.out.println(
                    line[0] + "\t" +  // id
                            line[1] + "\t" +  // price
                            line[2] + "\t" +  // brand
                            line[3] + "\t" +  // model
                            line[4] + "\t" +  // year
                            line[6] + "\t"    // mileage
            );

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO public.cars (brand, model, year, price, mileage)" +
                            "VALUES (?, ?, ?, ?, ?)"
            );

            // populate values from the CSV into the prepared statement
            stmt.setString(1, line[2]);
            stmt.setString(2, line[3]);
            stmt.setInt(3, Integer.parseInt(line[4]));
            stmt.setBigDecimal(4, BigDecimal.valueOf(Long.parseLong(line[1])));
            stmt.setDouble(5, Double.parseDouble(line[6]));
            stmt.executeUpdate();

            stmt.close();
        }
    }
}
