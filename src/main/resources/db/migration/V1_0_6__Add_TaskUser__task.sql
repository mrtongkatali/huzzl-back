ALTER TABLE task
ADD status SMALLINT NOT NULL DEFAULT 1,
ADD user_id INT,
ADD CONSTRAINT fk_task_user_id FOREIGN KEY (user_id) REFERENCES users (id)