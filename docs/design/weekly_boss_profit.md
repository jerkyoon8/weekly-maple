# 주간 보스 수익 계산 기능 설계

## 1. 개요
메이플스토리의 주간 보스 처치 시 획득할 수 있는 결정석 가격을 합산하여 총 수익을 계산하는 기능입니다.

## 2. 개발 흐름
1. **DB 구축**: 보스 정보(이름, 난이도, 가격, 이미지)를 저장할 테이블 생성
2. **Backend**: DB에서 보스 목록을 조회하는 API 개발 (Controller -> Service -> Mapper -> DB)
3. **Frontend**: 조회된 보스 목록을 화면에 체크박스 형태로 출력
4. **계산 로직**: 사용자가 선택한 보스들의 결정석 가격 합계를 계산하여 표시

## 3. 클래스 역할 정의
* **Controller (`WeeklyBossController`)**: 화면 요청 처리, 데이터 조회 요청
* **Service (`BossService`)**: 비즈니스 로직 처리 (필요 시 환율 계산 등)
* **Mapper (`BossMapper`)**: SQL 쿼리 실행
* **DTO (`BossDTO`)**: 데이터 전송 객체 (DB 데이터를 담는 그릇)

## 4. DB 설계 (`boss_info`)

| 컬럼명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `id` | INT (PK) | 고유 번호 (Auto Increment) |
| `name` | VARCHAR(50) | 보스 이름 (예: 자쿰) |
| `difficulty` | VARCHAR(20) | 난이도 (예: 카오스) |
| `crystal_price` | BIGINT | 결정석 가격 (메소) |
| `image_path` | VARCHAR(255) | 이미지 파일 경로 |

---
*작성일: 2026-01-21*
