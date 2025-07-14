-- PostgreSQL용 members 테이블 생성 (soft delete 포함)
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY, -- 회원 고유 ID
    member_id VARCHAR(50) UNIQUE NOT NULL, -- 사용자 아이디 (로그인 ID)
    password VARCHAR(255), -- 비밀번호 (소셜 로그인만 사용 시 NULL)
    name VARCHAR(100) NOT NULL, -- 사용자 이름
    phone VARCHAR(20), -- 휴대폰 번호
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL, -- 삭제 여부 (soft delete)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 수정 일시
    deleted_at TIMESTAMP NULL -- 삭제 일시
);

-- 소셜 로그인 정보 테이블 (다중 소셜 로그인 지원)
CREATE TABLE member_social_logins (
    id BIGSERIAL PRIMARY KEY, -- 소셜 로그인 고유 ID
    member_pk BIGINT NOT NULL, -- 회원 PK (FK)
    social_provider VARCHAR(20) NOT NULL CHECK (social_provider IN ('naver', 'kakao')), -- 소셜 로그인 제공자
    social_id VARCHAR(255) NOT NULL, -- 소셜 로그인 고유 ID
    social_email VARCHAR(255), -- 소셜 로그인 이메일
    social_name VARCHAR(100), -- 소셜 로그인 이름
    is_active BOOLEAN DEFAULT TRUE NOT NULL, -- 활성 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 연결 일시
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 수정 일시
);

-- members 테이블 인덱스
CREATE INDEX idx_members_member_id ON members(member_id);
CREATE INDEX idx_members_created_at ON members(created_at);
CREATE INDEX idx_members_is_deleted ON members(is_deleted);
CREATE INDEX idx_members_deleted_at ON members(deleted_at);

-- member_social_logins 테이블 인덱스
CREATE INDEX idx_social_logins_member_pk ON member_social_logins(member_pk);
CREATE INDEX idx_social_logins_provider ON member_social_logins(social_provider);
CREATE INDEX idx_social_logins_social_id ON member_social_logins(social_id);
CREATE INDEX idx_social_logins_created_at ON member_social_logins(created_at);
CREATE INDEX idx_social_logins_is_active ON member_social_logins(is_active);

-- 같은 소셜 제공자의 같은 소셜 ID는 한 번만 등록 가능
CREATE UNIQUE INDEX idx_social_logins_unique ON member_social_logins(social_provider, social_id);

-- 한 회원이 같은 소셜 제공자를 중복 연결할 수 없도록 제한
CREATE UNIQUE INDEX idx_social_logins_member_provider ON member_social_logins(member_pk, social_provider) 
WHERE is_active = TRUE;

-- 테이블 및 컬럼 코멘트 추가
COMMENT ON TABLE members IS '회원 정보 테이블';
COMMENT ON COLUMN members.id IS '회원 고유 ID';
COMMENT ON COLUMN members.member_id IS '사용자 아이디 (로그인 ID)';
COMMENT ON COLUMN members.password IS '비밀번호 (소셜 로그인만 사용 시 NULL)';
COMMENT ON COLUMN members.name IS '사용자 이름';
COMMENT ON COLUMN members.phone IS '휴대폰 번호';
COMMENT ON COLUMN members.is_deleted IS '삭제 여부 (soft delete)';
COMMENT ON COLUMN members.created_at IS '생성 일시';
COMMENT ON COLUMN members.updated_at IS '수정 일시';
COMMENT ON COLUMN members.deleted_at IS '삭제 일시';

COMMENT ON TABLE member_social_logins IS '회원 소셜 로그인 정보 테이블';
COMMENT ON COLUMN member_social_logins.id IS '소셜 로그인 고유 ID';
COMMENT ON COLUMN member_social_logins.member_pk IS '회원 PK (FK)';
COMMENT ON COLUMN member_social_logins.social_provider IS '소셜 로그인 제공자';
COMMENT ON COLUMN member_social_logins.social_id IS '소셜 로그인 고유 ID';
COMMENT ON COLUMN member_social_logins.social_email IS '소셜 로그인 이메일';
COMMENT ON COLUMN member_social_logins.social_name IS '소셜 로그인 이름';
COMMENT ON COLUMN member_social_logins.is_active IS '활성 여부';
COMMENT ON COLUMN member_social_logins.created_at IS '연결 일시';
COMMENT ON COLUMN member_social_logins.updated_at IS '수정 일시'; 