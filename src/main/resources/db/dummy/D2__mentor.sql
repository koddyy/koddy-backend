INSERT INTO member(email, name, profile_image_url, nationality, introduction, profile_complete, status, role, type)
VALUES ('mentor1@gmail.com', '멘토1', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', '안녕하세요', 1, 'ACTIVE', 'MENTOR', 'MENTOR'),
       ('mentor2@gmail.com', '멘토2', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', null, 0, 'ACTIVE', 'MENTOR', 'MENTOR'),
       ('mentor3@gmail.com', '멘토3', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', '안녕하세요', 0, 'ACTIVE', 'MENTOR', 'MENTOR'),
       ('mentor4@gmail.com', '멘토4', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', null, 0, 'ACTIVE', 'MENTOR', 'MENTOR'),
       ('mentor5@gmail.com', '멘토5', 'https://koddy-upload.s3.ap-northeast-2.amazonaws.com/profiles/koddy.png',
        'KOREA', '안녕하세요', 1, 'ACTIVE', 'MENTOR', 'MENTOR');

INSERT INTO mentor(id, school, major, entered_in, mentoring_start_date, mentoring_end_date, mentoring_time_unit,
                   school_mail, proof_data_upload_url, auth_status)
VALUES (6, '서울대학교', '컴퓨터공학부', 18, '2024-01-01', '2024-12-31', 'HALF_HOUR',
        'mentor1@snu.ac.kr', null, 'SUCCESS'),
       (7, '연세대학교', '컴퓨터공학부', 20, '2024-01-01', '2024-05-31', 'HALF_HOUR',
        null, null, null),
       (8, '고려대학교', '컴퓨터공학부', 21, '2024-01-01', '2024-07-31', 'HALF_HOUR',
        'mentor3@korea.ac.kr', null, 'SUCCESS'),
       (9, '한양대학교', '컴퓨터공학부', 19, '2024-01-01', '2024-03-31', 'HALF_HOUR',
        null, null, null),
       (10, '성균관대학교', '컴퓨터공학부', 16, '2024-01-01', '2024-04-30', 'HALF_HOUR',
        'mentor5@skku.edu', null, 'SUCCESS');

INSERT INTO mentor_schedule(mentor_id, day_of_week, start_time, end_time)
VALUES (6, 'MON', '18:00:00', '22:00:00'),
       (6, 'WED', '17:30:00', '24:00:00'),
       (6, 'FRI', '19:00:00', '24:00:00'),
       (10, 'TUE', '10:00:00', '20:00:00'),
       (10, 'SAT', '13:00:00', '22:00:00');
