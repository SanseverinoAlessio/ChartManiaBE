DELETE FROM refresh_tokens WHERE user_id IN (
    SELECT id FROM users WHERE username IN ('test_login', 'existing_user_for_username_check', 'test')
);
DELETE FROM users WHERE username IN ('test_login', 'existing_user_for_username_check', 'existing_user_for_email_check', 'test');
