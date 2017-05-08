CREATE TABLE jwt_access_token
(
  id bigserial primary key,
  user_id INT,
  token character varying(512) NOT NULL,
  expires timestamp NOT NULL,
  date_added TIMESTAMP default NULL,
  last_modified TIMESTAMP,

  CONSTRAINT fk_jwt_access_token_user_id FOREIGN KEY (user_id) REFERENCES users (id)
)