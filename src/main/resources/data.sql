INSERT INTO `users` VALUES (1, 'book_manager@test.com', '$2a$10$napOYav1HTpyfMMOkKi5p.uEa3iJcAHE949s5qx9EIYxth123o54q', 'book_manager');
INSERT INTO `users` VALUES (2, 'book_admin@test.com', '$2a$10$diPnYr3I4O/08qgFcmBFYuNtD/px0qCMq4kl.V24L0yHGCCxi7zSW', 'book_admin');
INSERT INTO `users` VALUES (3, 'order_manager@test.com', '$2a$10$z0Go8hP6Jep/NIR3SLwgveiUCSiwEVHKXM3oNqI6MiEYtkgopk4U2', 'order_manager');
INSERT INTO `users` VALUES (4, 'order_admin@test.com', '$2a$10$YY5o8xhddShcB1MwjPwdiu4UDPWUT7EXWNozXJ6HrhaJG5KKCMuw.', 'order_admin');
INSERT INTO `users` VALUES (5, 'user_1@test.com', '$2a$10$8UNyroE04JBowy4enn3uIe9xGRBti3dAhvEvDgfsQn7UzkQDj73TW', 'user_1');
INSERT INTO `users` VALUES (6, 'user_2@test.com', '$2a$10$xfV2gE4zxKUY/1Z7F6yDkexnG9EAUFeLRc/HQn5EbQx2cPbaoy9KO', 'user_2');

INSERT INTO `permissions` VALUES (1, 'book_view;create;update', 'book_manager');
INSERT INTO `permissions` VALUES (2, 'book_view;create;update;delete', 'book_admin');
INSERT INTO `permissions` VALUES (3, 'order_view;update', 'order_manager');
INSERT INTO `permissions` VALUES (4, 'order_view;update;delete', 'order_admin');
INSERT INTO `permissions` VALUES (5, 'book_view', 'normal_user_book');
INSERT INTO `permissions` VALUES (6, 'order_view;create;update', 'normal_user_order');

INSERT INTO `roles` VALUES (1, 'ROLE_USER');
INSERT INTO `roles` VALUES (2, 'ROLE_ADMIN');

INSERT INTO `user_permissions` VALUES (1, 1);
INSERT INTO `user_permissions` VALUES (2, 2);
INSERT INTO `user_permissions` VALUES (3, 3);
INSERT INTO `user_permissions` VALUES (4, 4);
INSERT INTO `user_permissions` VALUES (5, 5);
INSERT INTO `user_permissions` VALUES (5, 6);

INSERT INTO `user_roles` VALUES (2, 1);
INSERT INTO `user_roles` VALUES (2, 2);
INSERT INTO `user_roles` VALUES (3, 1);
INSERT INTO `user_roles` VALUES (4, 1);
INSERT INTO `user_roles` VALUES (4, 2);
INSERT INTO `user_roles` VALUES (5, 1);
