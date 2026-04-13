-- User 1
INSERT INTO users (id, email, password, username)
VALUES (1, 'user_controller_test@test.it', '$2a$10$hiczwcaBRS5EIXXdRMhlPuaOzr0wUe8iRrdBPs1GUm7AUtL.JHZ.i', 'user_controller_test');

-- User 2
INSERT INTO users (id, email, password, username)
VALUES (2, 'user2_controller_test@test.it', '$2a$10$hiczwcaBRS5EIXXdRMhlPuaOzr0wUe8iRrdBPs1GUm7AUtL.JHZ.i', 'user2_controller_test');

-- Chart 1 → User 1
INSERT INTO charts (id, filename, name, type, user_id, created_at, updated_at, deleted)
VALUES (1, 'test.jpg', 'Test Chart', 'line', 1, NOW(), NOW(), false);

INSERT INTO chart_datasets (id, name, chart_id, created_at, updated_at, deleted)
VALUES (1, 'Dataset 1', 1, NOW(), NOW(), false);

INSERT INTO chart_data (label, x_value, y_value, color, chart_dataset_id, created_at, updated_at, deleted)
VALUES ('Point 1', 10, 10, '#cd2b2b', 1, NOW(), NOW(), false);

-- Chart 2 → User 2
INSERT INTO charts (id, filename, name, type, user_id, created_at, updated_at, deleted)
VALUES (2, 'test2.jpg', 'Test Chart 2', 'bar', 2, NOW(), NOW(), false);

INSERT INTO chart_datasets (id, name, chart_id, created_at, updated_at, deleted)
VALUES (2, 'Dataset 2', 2, NOW(), NOW(), false);

INSERT INTO chart_data (label, x_value, y_value, color, chart_dataset_id, created_at, updated_at, deleted)
VALUES ('Point 1', 5, 15, '#2bcd2b', 2, NOW(), NOW(), false);