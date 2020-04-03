BaseX
=====

The credentials for the BaseXHTTP server are set to default admin:admin.
They can be changed by supplying a startup command to the server in the Dockerfile.
See [here](http://docs.basex.org/wiki/User_Management how) how.

The credentials, hostname and port must also be set in the docker-compose.yml file.
If the credentials are changed in the Dockerfile, docker-compose.yml must be adjusted
accordingly.