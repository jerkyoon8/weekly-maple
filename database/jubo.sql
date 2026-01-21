-- 주간 보스 정보 테이블 생성
CREATE TABLE IF NOT EXISTS boss_info (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 번호',
    name VARCHAR(50) NOT NULL COMMENT '보스 이름',
    difficulty VARCHAR(20) NOT NULL COMMENT '난이도',
    crystal_price BIGINT NOT NULL COMMENT '결정석 가격',
    image_path VARCHAR(255) COMMENT '이미지 파일 경로',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주간 보스 정보';

-- 초기 데이터 예시 (카오스 자쿰, 하드 매그너스)
-- 가격은 변동될 수 있으므로 예시 값입니다.
INSERT INTO boss_info (name, difficulty, crystal_price, image_path) VALUES 
('자쿰', '카오스', 16200000, 'zakum_chaos.png'),
('매그너스', '하드', 19000000, 'magnus_hard.png');