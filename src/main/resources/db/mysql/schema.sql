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
    content     TEXT             NOT NULL
);
