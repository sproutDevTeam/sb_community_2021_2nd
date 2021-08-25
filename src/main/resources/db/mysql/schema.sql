-- # 데이터베이스 접속 URL 에 데이터베이스명이 포함되어 있기 때문에
-- # 우선 클라이언트 툴이나 터미널을 통해 최소 한번은 DB 생성 DDL 을 실행해야
-- # schema.sql 스크립트를 통한 초기화 시 오류가 발생하지 않습니다.

-- # DB 생성
DROP DATABASE IF EXISTS c_2021_2nd;
CREATE DATABASE c_2021_2nd;
USE c_2021_2nd;

-- # 게시물 테이블 생성
DROP TABLE IF EXISTS article;
CREATE TABLE article
(
    id         INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    regDate    DATETIME         NOT NULL,
    updateDate DATETIME         NOT NULL,
    title      CHAR(100)        NOT NULL,
    content    TEXT             NOT NULL
);

-- # 계정 테이블 생성
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
    id           INT(10) UNSIGNED    NOT NULL PRIMARY KEY AUTO_INCREMENT,
    regDate      DATETIME            NOT NULL,
    updateDate   DATETIME            NOT NULL,
    username     CHAR(20) UNIQUE     NOT NULL COMMENT '로그인 ID',
    `password`   CHAR(60)            NOT NULL COMMENT '로그인 PW',
    authLevel    SMALLINT(2) UNSIGNED         DEFAULT 3 COMMENT '권한레벨(3:일반, 7:관리자)',
    `name`       CHAR(20)            NOT NULL,
    nickname     CHAR(20)            NOT NULL,
    mobileNumber CHAR(20)            NOT NULL,
    email        CHAR(50) UNIQUE     NOT NULL,
    delStatus    TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '탈퇴여부(0:탈퇴전, 1:탈퇴)',
    delDate      DATETIME COMMENT '탈퇴일자'
);