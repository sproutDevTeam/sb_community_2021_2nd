-- # 게시물, 테스트 데이터 생성
INSERT INTO article
SET regDate    = NOW(),
    updateDate = NOW(),
    title      = '제목 1',
    content     = '내용 1';

INSERT INTO article
SET regDate    = NOW(),
    updateDate = NOW(),
    title      = '제목 2',
    content     = '내용 2';

INSERT INTO article
SET regDate    = NOW(),
    updateDate = NOW(),
    title      = '제목 3',
    content     = '내용 3';

-- # 계정, 테스트 데이터 생성 (관리자 계정)
INSERT INTO `account`
SET regDate      = NOW(),
    updateDate   = NOW(),
    username     = 'admin',
    `password`   = 'admin',
    authLevel    = 7,
    `name`       = '관리자',
    nickname     = '관리자',
    mobileNumber = '01011112222',
    email        = 'admin@email.com';

-- # 계정, 테스트 데이터 생성 (일반회원 계정)
INSERT INTO `account`
SET regDate      = NOW(),
    updateDate   = NOW(),
    username     = 'user1',
    `password`   = 'user1',
    `name`       = '사용자1',
    nickname     = '사용자1',
    mobileNumber = '01011112222',
    email        = 'user1@email.com';

INSERT INTO `account`
SET regDate      = NOW(),
    updateDate   = NOW(),
    username     = 'user2',
    `password`   = 'user2',
    `name`       = '사용자2',
    nickname     = '사용자2',
    mobileNumber = '01011112222',
    email        = 'user2@email.com';