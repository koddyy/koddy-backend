INSERT INTO member(email, name, profile_image_url, nationality, introduction, profile_complete, status, role, type)
VALUES ('mentee1@gmail.com', '멘티1', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'USA', '안녕하세요', 1, 'ACTIVE', 'MENTEE', 'MENTEE'),
       ('mentee2@gmail.com', '멘티2', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'JAPAN', NULL, 0, 'ACTIVE', 'MENTEE', 'MENTEE'),
       ('mentee3@gmail.com', '멘티3', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'OTHERS', '안녕하세요', 1, 'ACTIVE', 'MENTEE', 'MENTEE'),
       ('mentee4@gmail.com', '멘티4', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'CHINA', NULL, 0, 'ACTIVE', 'MENTEE', 'MENTEE'),
       ('mentee5@gmail.com', '멘티5', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'VIETNAM', '안녕하세요', 1, 'ACTIVE', 'MENTEE', 'MENTEE');

INSERT INTO mentee(id, interest_school, interest_major)
VALUES (1, '서울대학교', '컴퓨터공학부'),
       (2, '연세대학교', '컴퓨터공학부'),
       (3, '고려대학교', '컴퓨터공학부'),
       (4, '한양대학교', '컴퓨터공학부'),
       (5, '성균관대학교', '컴퓨터공학부');
