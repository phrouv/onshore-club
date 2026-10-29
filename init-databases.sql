-- =============================================================================
-- PostgreSQL init script: creates one database per microservice.
-- Mounted into /docker-entrypoint-initdb.d/ so PostgreSQL runs it on first startup.
-- =============================================================================

ALTER USER postgres WITH PASSWORD 'arpad5';

DROP DATABASE IF EXISTS onshore_user_db;
CREATE DATABASE onshore_user_db WITH OWNER postgres;
DROP DATABASE IF EXISTS onshore_payment_db;
CREATE DATABASE onshore_payment_db WITH OWNER postgres;
DROP DATABASE IF EXISTS onshore_partner_db;
CREATE DATABASE onshore_partner_db WITH OWNER postgres;
DROP DATABASE IF EXISTS onshore_planning_db;
CREATE DATABASE onshore_planning_db WITH OWNER postgres;
DROP DATABASE IF EXISTS onshore_credit_db;
CREATE DATABASE onshore_credit_db WITH OWNER postgres;
DROP DATABASE IF EXISTS onshore_payout_db;
CREATE DATABASE onshore_payout_db WITH OWNER postgres;
DROP DATABASE IF EXISTS onshore_admin_db;
CREATE DATABASE onshore_admin_db WITH OWNER postgres;
DROP DATABASE IF EXISTS onshore_booking_db;
CREATE DATABASE onshore_booking_db WITH OWNER postgres;
