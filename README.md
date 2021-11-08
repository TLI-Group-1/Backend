# Backend

## Running the Development Server
```bash
./gradlew bootRun
```
This command will start the Spring development server on http://localhost:8080

## Environment Variables
This application requires a number of parameters to be specified via environment 
variables. They are listed below:
* `SENSO_API_URL`: the root URL for the Senso API, excluding specific endpoints. 
Do not include a trailing slash.
  * e.g. `https://senso-api.example.com`
* `SENSO_API_KEY`: the access credential for the Senso API, to be supplied in the
`x-api-key` HTTP header.
* `AUTODIRECT_DB_HOST`: FQDN for the AutoDirect PostgreSQL database server
  * e.g. `db.autodirect.tech`
* `AUTODIRECT_DB_PORT`: connection port for the PostgreSQL database server
  * e.g. `5432`
* `AUTODIRECT_DB_SSL`: either `true` or `false` specifying whether the connection
to the database uses SSL encryption or not.
* `AUTODIRECT_DB_USER`: user name used to access the PostgreSQL database
* `AUTODIRECT_DB_PASS`: password associated with the given user for the PostgreSQL database

## Initializing the PostgreSQL Database
The `InitDatabase` class within the `database` package contains a main method that allows it
to be executed independently. During deployment, it can be executed on a one time basis, which
initializes a PostgreSQL database with the necessary schema and populates the `public.cars` 
table with a provided CSV dataset.

Database configuration needs to be specified with environment variables as shown above.
```
Usage: InitDatabase [options] <file> 
	 -h --help	display this help message and exit
	 --csvfile	specify a CSV file path which contains the cars dataset

Example — use 'cars.csv' in the current working directory:
	 InitDatabase --csvfile ./cars.csv
```

## CORS
Hosts allowed to access this API are specified by the CORS annotations (`@CrossOrigin`) in `ApiEndpoints.java`.
Read more about it in issue [#11](https://github.com/TLI-Group-1/Backend/issues/11).
