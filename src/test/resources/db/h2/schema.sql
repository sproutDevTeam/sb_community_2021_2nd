-- H2 내장 인메모리 데이터베이스 사용 (테스트 시)

-- # 게시물 테이블 생성
DROP TABLE IF EXISTS article;
CREATE TABLE article
(
    id         INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    regDate    DATETIME         NOT NULL,
    updateDate DATETIME         NOT NULL,
    title      CHAR(100)        NOT NULL,
    content     TEXT             NOT NULL
);
