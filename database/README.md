# Test postgres container image 
This allows one to build a test database with the `dfs-payment-means` database inserted as a fixture. This can be used 
to have a local database for local running and testing requirements.
Once an app is started that connects to the database, it will migrate the database to the required state using the 
database migration scripts

## Use image locally
To build an image navigate to the test-database directory and run `docker build -t test-db`. This will create an image
available under the image name `test-db`.
To spin it up for usage with a clean database everytime you run it again, use the command
`docker run --rm -p 5432:5432 test-db # start database`. Don't use `--rm` if you would like to retain state and be able
to start it again with `docker start`.