-- PostgreSQL용 members 테이블에 UUID 컬럼 추가
-- UUID extension 활성화 (이미 활성화되어 있어도 오류 없이 처리)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- members 테이블에 uuid 컬럼 추가
ALTER TABLE members 
ADD COLUMN uuid UUID DEFAULT uuid_generate_v4() NOT NULL;

-- 기존 데이터들에 대해 UUID 생성
UPDATE members SET uuid = uuid_generate_v4() WHERE uuid IS NULL;

-- uuid 컬럼에 UNIQUE 제약조건 추가
ALTER TABLE members ADD CONSTRAINT uk_members_uuid UNIQUE (uuid);

-- uuid 컬럼에 인덱스 추가
CREATE INDEX idx_members_uuid ON members(uuid);

-- 컬럼 코멘트 추가
COMMENT ON COLUMN members.uuid IS '회원 UUID (고유 식별자)'; 