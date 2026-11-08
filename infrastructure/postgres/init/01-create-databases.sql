-- Identity Service
CREATE ROLE identity_user LOGIN PASSWORD 'identity_local_dev';
CREATE DATABASE identity_db OWNER identity_user;

-- Organization Service
CREATE ROLE organization_user LOGIN PASSWORD 'organization_local_dev';
CREATE DATABASE organization_db OWNER organization_user;

-- Account Service
CREATE ROLE account_user LOGIN PASSWORD 'account_local_dev';
CREATE DATABASE account_db OWNER account_user;

-- Transaction Service
CREATE ROLE transaction_user LOGIN PASSWORD 'transaction_local_dev';
CREATE DATABASE transaction_db OWNER transaction_user;

-- Ledger Service
CREATE ROLE ledger_user LOGIN PASSWORD 'ledger_local_dev';
CREATE DATABASE ledger_db OWNER ledger_user;

-- Budget Service
CREATE ROLE budget_user LOGIN PASSWORD 'budget_local_dev';
CREATE DATABASE budget_db OWNER budget_user;

-- Reporting Service
CREATE ROLE reporting_user LOGIN PASSWORD 'reporting_local_dev';
CREATE DATABASE reporting_db OWNER reporting_user;

-- Notification Service
CREATE ROLE notification_user LOGIN PASSWORD 'notification_local_dev';
CREATE DATABASE notification_db OWNER notification_user;