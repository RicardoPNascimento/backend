insert into users (id_user, full_name, cpf, email,zip, password,
                                   account_non_expired, account_non_locked, credentials_non_expired, enabled, created_at)
VALUES ('5da6e429-3557-4540-ba6a-7603f5f4b9b3', 'Sara Padoin', '0', 'financeiro@simbioseventures.com','0',
        '{pbkdf2}f79c0ab988cae33d291bf4d34cb82670955be40651405bb39a1e53621ce0f859470b516b2bf92ddf', true, true, true,
        true, '2022-11-22 11:44:47.946000'),
        ('6e22fcf3-c31f-452d-b73a-38ead0df6c59', 'Maria Conde', '1', 'maria.conde@simbioseventures.com','0',
    '{pbkdf2}f79c0ab988cae33d291bf4d34cb82670955be40651405bb39a1e53621ce0f859470b516b2bf92ddf', true, true, true,
    true, '2022-11-22 11:44:47.946000');


INSERT INTO users_permission (id_user, id_permission)
VALUES ('5da6e429-3557-4540-ba6a-7603f5f4b9b3', '1'),
 ('6e22fcf3-c31f-452d-b73a-38ead0df6c59', '1');


