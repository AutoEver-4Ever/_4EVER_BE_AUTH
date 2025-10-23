-- 확장 설치(없으면)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- UUID 기본값
ALTER TABLE users
    ALTER COLUMN user_id SET DEFAULT gen_random_uuid();
ALTER TABLE modules
    ALTER COLUMN module_id SET DEFAULT gen_random_uuid();
ALTER TABLE permissions
    ALTER COLUMN permission_id SET DEFAULT gen_random_uuid();
ALTER TABLE roles
    ALTER COLUMN role_id SET DEFAULT gen_random_uuid();

COMMIT;
