

INSERT INTO accounts (username, password_hash, is_locked, logged_in, role_id) VALUES ('luis', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', false, false, 'Authors');
INSERT INTO accounts (username, password_hash, is_locked, logged_in, role_id) VALUES ('raquel', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', false, false, 'Authors');

INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES ('Admins', -1, 'CREATE_PAGE');

INSERT INTO page (page_id, user_id, page_title, page_pic) VALUES (1, 'root', 'Root page', 'page.png');
INSERT INTO page (page_id, user_id, page_title, page_pic) VALUES (2, 'luis', 'Luis page', 'page.png');
INSERT INTO page (page_id, user_id, page_title, page_pic) VALUES (3, 'raquel', 'Raquel page', 'page.png');

UPDATE pagekey SET page_id = 4;

INSERT INTO post (post_id, page_id, post_date, post_text) VALUES (1, 1, '2018-01-01', 'Welcome to my page :)');
INSERT INTO post (post_id, page_id, post_date, post_text) VALUES (2, 1, '2018-01-01', 'Dudeee.... people are so laaaame');
INSERT INTO post (post_id, page_id, post_date, post_text) VALUES (3, 1, '2018-01-01', 'Random post!');

UPDATE postkey SET post_id = 4;

INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES ('Authors', 1, 'READ_PAGE');
INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES ('Authors', 2, 'READ_PAGE');
INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES ('Authors', 3, 'READ_PAGE');

INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES ('Admins', 1, 'READ_PAGE');
INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES ('Admins', 2, 'READ_PAGE');
INSERT INTO role_permissions (role_id, resource_id, operation_id) VALUES ('Admins', 3, 'READ_PAGE');

INSERT INTO account_permissions (username, resource_id, operation_id) VALUES ('root', 1, 'READ_POST');
INSERT INTO account_permissions (username, resource_id, operation_id) VALUES ('root', 1, 'CREATE_POST');
INSERT INTO account_permissions (username, resource_id, operation_id) VALUES ('root', 1, 'LIKE_UNLIKE_POST');


