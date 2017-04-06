CREATE TABLE task
(
  id bigserial primary key,
  task_title character varying(128) NOT NULL,
  task_description character varying(128) NOT NULL,
  date_added timestamp default NULL
)