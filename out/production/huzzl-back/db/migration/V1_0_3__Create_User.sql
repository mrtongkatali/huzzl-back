CREATE TABLE users
(
  id bigserial PRIMARY KEY,
  firstname VARCHAR(50) NOT NULL,
  lastname VARCHAR(50) NOT NULL,
  email_address VARCHAR(50) UNIQUE,
  status SMALLINT NOT NULL DEFAULT 1,
  date_added TIMESTAMP default NULL,
  last_modified TIMESTAMP
)