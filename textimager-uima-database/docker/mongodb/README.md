MongoDB
=======

MongoDB does not require Authentication by default.
Because of that the environment variables for user and password are currently empty and unused,
but exist for the future.

Adding Authentication must be done via a script on docker initialization.
See [here](https://docs.mongodb.com/manual/core/authentication/) for details.