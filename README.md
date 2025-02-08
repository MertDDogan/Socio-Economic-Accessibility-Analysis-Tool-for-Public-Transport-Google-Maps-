Instruction for database set up

1.Open your database management tool (such as MySQL Workbench, phpMyAdmin, DBeaver or command line for MySQL/MariaDB).(Create the Database)

2.Import the Database Schema
-We have provided a general SQL file (transitorgr28.sql) which contains the schema and initial data for the database.
-Import the transitorgr28.sql file into the newly created database. This can usually be done via an import option in your database tool.
-Alternative Import Method: If you encounter any issues with the SQL file, we have also provided a CSV backup. You can import this using your database tool’s import functionality for CSV files.


3.Update the database connection settings with the following details
(URL,Username,password) in the DatabaseManager.java class. 

4.Verifying database structure
*After importing the data and configuring the connection, verify that the database structure matches the expected schema.
*Compare the tables and their structures with the provided documentation or schema overview. Ensure all tables are correctly named and contain the appropriate columns and
data types.
*If there are any discrepancies or errors encountered during the import, refer to the provided CSV backup or contact support for assistance.