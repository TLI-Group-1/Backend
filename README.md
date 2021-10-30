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
* `AUTODIRECT_DB_SSL`: either `true` or `false` specifying whether the connection
to the database uses SSL encryption or not.
* `AUTODIRECT_DB_USER`: user name used to access the PostgreSQL database
* `AUTODIRECT_DB_PASS`: password associated with the given user for the PostgreSQL database

## CORS
Hosts allowed to access this API are specified by the CORS annotations (`@CrossOrigin`) in `ApiEndpoints.java`.
Read more about it in issue [#11](https://github.com/TLI-Group-1/Backend/issues/11).
