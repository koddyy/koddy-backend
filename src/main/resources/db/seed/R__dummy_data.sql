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

INSERT INTO member(email, name, profile_image_url, nationality, introduction, profile_complete, status, role, type)
VALUES ('mentor1@gmail.com', '멘토1', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', '안녕하세요', 1, 'ACTIVE', 'MENTOR', 'MENTOR'),
       ('mentor2@gmail.com', '멘토2', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', NULL, 0, 'ACTIVE', 'MENTOR', 'MENTOR'),
       ('mentor3@gmail.com', '멘토3', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', '안녕하세요', 0, 'ACTIVE', 'MENTOR', 'MENTOR'),
       ('mentor4@gmail.com', '멘토4', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', NULL, 0, 'ACTIVE', 'MENTOR', 'MENTOR'),
       ('mentor5@gmail.com', '멘토5', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', '안녕하세요', 1, 'ACTIVE', 'MENTOR', 'MENTOR');

INSERT INTO mentor(id, school, major, entered_in, mentoring_start_date, mentoring_end_date, mentoring_time_unit,
                   school_mail, proof_data_upload_url, auth_status)
VALUES (6, '서울대학교', '컴퓨터공학부', 18, '2024-01-01', '2024-12-31', 'HALF_HOUR',
        'mentor1@snu.ac.kr', NULL, 'SUCCESS'),
       (7, '연세대학교', '컴퓨터공학부', 20, '2024-01-01', '2024-05-31', 'HALF_HOUR',
        NULL, NULL, NULL),
       (8, '고려대학교', '컴퓨터공학부', 21, '2024-01-01', '2024-07-31', 'HALF_HOUR',
        'mentor3@korea.ac.kr', NULL, 'SUCCESS'),
       (9, '한양대학교', '컴퓨터공학부', 19, '2024-01-01', '2024-03-31', 'HALF_HOUR',
        NULL, NULL, NULL),
       (10, '성균관대학교', '컴퓨터공학부', 16, '2024-01-01', '2024-04-30', 'HALF_HOUR',
        'mentor5@skku.edu', NULL, 'SUCCESS');

INSERT INTO mentor_schedule(mentor_id, day_of_week, start_time, end_time)
VALUES (6, 'MON', '18:00:00', '22:00:00'),
       (6, 'WED', '17:30:00', '24:00:00'),
       (6, 'FRI', '19:00:00', '24:00:00'),
       (10, 'TUE', '10:00:00', '20:00:00'),
       (10, 'SAT', '13:00:00', '22:00:00');

INSERT INTO member_language(member_id, language_category, language_type)
VALUES (1, 'EN', 'MAIN'),
       (1, 'KR', 'SUB'),
       (2, 'JP', 'MAIN'),
       (2, 'KR', 'SUB'),
       (2, 'EN', 'SUB'),
       (3, 'EN', 'MAIN'),
       (4, 'CN', 'MAIN'),
       (4, 'EN', 'SUB'),
       (5, 'VN', 'MAIN'),
       (5, 'EN', 'SUB'),
       (5, 'KR', 'SUB'),
       (6, 'KR', 'MAIN'),
       (6, 'EN', 'SUB'),
       (6, 'CN', 'SUB'),
       (6, 'JP', 'SUB'),
       (6, 'VN', 'SUB'),
       (7, 'KR', 'MAIN'),
       (7, 'EN', 'SUB'),
       (7, 'CN', 'SUB'),
       (7, 'JP', 'SUB'),
       (8, 'KR', 'MAIN'),
       (8, 'EN', 'SUB'),
       (8, 'JP', 'SUB'),
       (9, 'KR', 'MAIN'),
       (9, 'EN', 'SUB'),
       (9, 'VN', 'SUB'),
       (10, 'KR', 'MAIN'),
       (10, 'EN', 'SUB');
