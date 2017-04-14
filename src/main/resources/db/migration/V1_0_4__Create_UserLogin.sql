CREATE TABLE user_login
(
    id bigserial PRIMARY KEY,
    user_id INT,
    password_salt VARCHAR(255),
    password_hash VARCHAR(255),
    date_added TIMESTAMP default NULL,
    last_modified TIMESTAMP,
    last_modified_by INT,

    CONSTRAINT fk_user_login_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_login_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users (id)
)