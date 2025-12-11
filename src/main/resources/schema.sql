# /* 기존 테이블 삭제 (초기화용) */
# DROP TABLE IF EXISTS follow;
# DROP TABLE IF EXISTS review_like;
# DROP TABLE IF EXISTS vote;
# DROP TABLE IF EXISTS reading_history;
# DROP TABLE IF EXISTS reply;
# DROP TABLE IF EXISTS review;
# DROP TABLE IF EXISTS topic;
# DROP TABLE IF EXISTS book;
# DROP TABLE IF EXISTS member;
#
# /* 1. 회원 (member) */
# CREATE TABLE member (
#                        member_id      VARCHAR(50) NOT NULL PRIMARY KEY,
#                        password     VARCHAR(255) NOT NULL,
#                        name         VARCHAR(50) NOT NULL,
#                        email        VARCHAR(100),
#                        age          INT CHECK (age >= 0),
#                        phone_number VARCHAR(20) NOT NULL,
#                        school       VARCHAR(100) NOT NULL,
#                        created_at   DATETIME DEFAULT CURRENT_TIMESTAMP
# );
#
# /* 2. 도서 (book) */
# CREATE TABLE book (
#                       book_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
#                       title        VARCHAR(200) NOT NULL,
#                       author       VARCHAR(100) NOT NULL,
#                       publisher    VARCHAR(100),
#                       publish_date DATE
# );
#
# /* 3. 오늘의 질문 (topic) */
# CREATE TABLE topic (
#                        topic_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
#                        question     VARCHAR(255) NOT NULL,
#                        option_a     VARCHAR(100) NOT NULL,
#                        option_b     VARCHAR(100) NOT NULL,
#                        created_date DATE DEFAULT (CURRENT_DATE)
# );
#
# /* 4. 리뷰 (review) */
# CREATE TABLE review (
#                         review_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
#                         content      TEXT,
#                         rating       INT NOT NULL CHECK (rating BETWEEN 0 AND 5),
#                         created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
#                         member_id      VARCHAR(50) NOT NULL,
#                         book_id      BIGINT NOT NULL,
#                         FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE ON UPDATE CASCADE,
#                         FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE
# );
#
# /* 5. 답글 (reply) */
# CREATE TABLE reply (
#                        reply_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
#                        content      TEXT NOT NULL,
#                        created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
#                        member_id      VARCHAR(50) NOT NULL,
#                        review_id    BIGINT NOT NULL,
#                        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE ON UPDATE CASCADE,
#                        FOREIGN KEY (review_id) REFERENCES Review(review_id) ON DELETE CASCADE
# );
#
# /* 6. 독서 기록 (reading_history) */
# CREATE TABLE reading_history (
#                                 reading_history_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
#                                 read_start_date DATE,
#                                 read_status     ENUM('READING', 'COMPLETED', 'STOPPED') NOT NULL,
#                                 memo            TEXT,
#                                 member_id         VARCHAR(50) NOT NULL,
#                                 book_id         BIGINT NOT NULL,
#                                 FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE ON UPDATE CASCADE,
#                                 FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE
# );
#
# /* 7. 투표 (vote) */
# CREATE TABLE vote (
#                       vote_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
#                       choice    CHAR(1) NOT NULL CHECK (choice IN ('A', 'B')),
#                       reason    VARCHAR(255),
#                       member_id   VARCHAR(50) NOT NULL,
#                       topic_id  BIGINT NOT NULL,
#                       UNIQUE KEY uk_member_topic (member_id, topic_id),
#                       FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE ON UPDATE CASCADE,
#                       FOREIGN KEY (topic_id) REFERENCES Topic(topic_id) ON DELETE CASCADE
# );
#
# /* 8. 리뷰 좋아요 (review_like) */
# CREATE TABLE review_like (
#                              member_id   VARCHAR(50) NOT NULL,
#                              review_id BIGINT NOT NULL,
#                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
#                              PRIMARY KEY (member_id, review_id),
#                              FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE ON UPDATE CASCADE,
#                              FOREIGN KEY (review_id) REFERENCES Review(review_id) ON DELETE CASCADE
# );
#
# /* 9. 팔로우 (follow) */
# CREATE TABLE follow (
#                         follower_id  VARCHAR(50) NOT NULL,
#                         following_id VARCHAR(50) NOT NULL,
#                         created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
#                         PRIMARY KEY (follower_id, following_id),
#                         FOREIGN KEY (follower_id) REFERENCES member(member_id) ON DELETE CASCADE ON UPDATE CASCADE,
#                         FOREIGN KEY (following_id) REFERENCES member(member_id) ON DELETE CASCADE ON UPDATE CASCADE
# );
#
# /* 인덱스 생성 */
# CREATE INDEX idx_book_title ON book(title);
# CREATE INDEX idx_book_author ON book(author);