CREATE DATABASE txn_control_db;
CREATE USER dev_user WITH ENCRYPTED PASSWORD 'dev_password';
GRANT ALL PRIVILEGES ON DATABASE txn_control_db TO dev_user;
ALTER DATABASE txn_control_db OWNER TO dev_user;