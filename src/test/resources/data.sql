INSERT INTO users (username, password) VALUES ('Admin', '$2a$10$zVd5EoYxTPKOoj6sGrYvAud86v5oF/l5TlYg4MAEvNt9Ct.Db91O.'); -- 'adminPassword'
INSERT INTO users (username, password) VALUES ('User 1', '$2a$10$iYOyoRjOzo/X/ceWh/awjezp1mH20M16z56g/DY2bWfKJ5ZPxm82.'); -- 'password1'
INSERT INTO users (username, password) VALUES ('User 2', '$2a$10$3d9AZeygdvg4q3Unwk10f.n1sLzakLpqACZLVYSJShJuudtRl9pu.'); -- 'password2'

INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO users_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (3, 2);