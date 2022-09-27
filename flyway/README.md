# Flyway

Flyway is a database migration tool for creating versioning of your database.

The `resources/db/migration` directory contains the database migrations.

It is possible to do things like 
- V1__SOMENAME.sql (run to create)
- U1__SOMENAME.sql (undo creation of SOMENAME)
- R1__OTHERTHING.sql (repeatable script)

## Resources

[How flyway works](https://flywaydb.org/documentation/getstarted/how)
[Migration concepts](https://flywaydb.org/documentation/concepts/migrations#naming)