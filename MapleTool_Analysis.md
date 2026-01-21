# MapleTool 프로젝트 상세 기술 분석 (Reverse PRD)

## 1. 프로젝트 개요 및 아키텍처

이 문서는 `MapleTool` 오픈소스 프로젝트의 코드 구조와 구현 방식을 심층 분석한 역기획서입니다.
'주보(Jubo)' 프로젝트 개발 시 참고할 핵심 기술 명세서로 활용됩니다.

### 1.1 기술 스택
- **Frontend (Client):** React 18, TypeScript, Vite, Redux Toolkit (상태 관리), Chakra UI (디자인), Framer Motion (애니메이션)
- **Backend (Server):** Java 21, Spring Boot 3.3.3, Spring Data JPA, Redis (캐싱), OpenFeign (넥슨 API 통신), MySQL 8.4
- **Infrastructure:** Docker, Nginx

### 1.2 아키텍처 패턴 (Hybrid)
이 프로젝트는 기능의 성격에 따라 두 가지 패턴을 혼용하고 있습니다.
1.  **Server-Side Logic (캐릭터, 유니온):** 넥슨 Open API가 필요한 데이터는 **Spring Boot 백엔드**를 통해 조회하고 DB/Redis에 캐싱합니다.
2.  **Client-Side Logic (보스, 강화):** 계산 로직이 복잡하지만 데이터가 정적인 기능은 **React 프론트엔드**에서 순수 자바스크립트로 처리하여 서버 부하를 없앴습니다.

---

## 2. 상세 구현 분석

### 2.1 👤 캐릭터 조회 시스템 (Backend 중심)
캐릭터 정보를 가져오는 흐름은 전형적인 **Proxy + Caching** 패턴입니다.

**[데이터 흐름]**
1.  **API 요청:** `GET /character/{name}` (CharacterController)
2.  **캐시 확인:** Redis/DB에 해당 캐릭터 정보가 최신인지 확인.
3.  **넥슨 API 호출 (OpenFeign):**
    - `feign/maple/MapleClient.java` 인터페이스를 통해 넥슨 API 호출.
    - `api-key`는 설정 파일(`application.yml` 등)에서 관리.
4.  **데이터 저장 (JPA):**
    - `entity/CharacterBasic.java`: 캐릭터 기본 정보 저장.
    - `entity/Potential.java`: 잠재능력 정보 저장.
5.  **응답:** 조회된 데이터를 DTO로 변환하여 프론트엔드에 전달.

**[코드 구조 특징]**
- **Feign Client 사용:** HTTP 요청을 직접 구현하지 않고 인터페이스 선언만으로 처리하여 코드가 깔끔함.
- **DTO 분리:** 넥슨 API 응답용 DTO와 클라이언트 반환용 DTO를 철저히 분리함.

### 2.2 💰 보스 수익 계산기 (Frontend 중심)
서버 통신 없이 브라우저 내에서 완벽하게 동작하는 **독립 앱(Stand-alone App)** 구조입니다.

**[데이터 구조 (Redux Store)]**
- **`userSlice.ts`:** 사용자의 보스 파티 설정을 전역 상태로 관리.
    ```typescript
    interface BossPlan {
        name: string;      // 캐릭터 이름
        boss: {            // 잡을 보스 목록
            type: BOSS_TYPE;
            difficulty: BOSS_DIFFICULTY;
            members: number; // 파티 인원 수
        }[];
    }
    ```

**[핵심 로직]**
- **데이터 소스 (`constants/boss.ts`):** 보스별 결정석 가격이 하드코딩되어 있음. (예: `prices: { HARD: 30000000 }`)
- **계산 로직 (`services/boss.ts`):**
    - `calculateRevenue(plan)`: 캐릭터별 총 수익 계산.
    - `MAX_BOSS_SELECTABLE (12개)` 제한을 로직 내에서 체크하지 않고 UI 레벨에서 필터링하거나, 계산 시 상위 12개만 합산하는 방식 사용 추정.
- **공유 기능:** URL 쿼리 파라미터(`?name=bitmask...`)를 통해 사용자가 자신의 설정을 다른 사람에게 링크로 공유할 수 있는 기능 구현 (`convertPlansToParams`).

### 2.3 🔨 강화 시뮬레이터 (Frontend 중심)
가장 방대한 데이터를 다루지만, 모두 상수(Constant)로 처리하여 백엔드 의존성을 제거했습니다.

**[데이터 관리 방식]**
- **`constants/enhance/equipment/` 폴더:** 아이템 종류별(아케인, 에테르넬, 보스장신구 등)로 파일이 나뉘어 있음.
- **아이템 정의 예시:**
    ```typescript
    // rootabis.ts
    export const ROOTABIS_TOP = {
        name: "하이네스 상의",
        req_level: 150,
        boss_damage: "0%", // 기본 옵션 하드코딩
        ...
    }
    ```
- **확률 데이터:** 스타포스 성공/실패/파괴 확률 또한 상수 파일(`constants/enhance/starforce.ts` 추정)로 관리.

**[시뮬레이션 로직]**
- 사용자가 '강화' 버튼을 누를 때마다 자바스크립트 `Math.random()` 등을 사용하여 확률에 따라 성공/실패 여부를 결정하고, 아이템 상태(Redux 또는 Local State)를 업데이트함.

### 2.4 🧩 유니온 & 아티팩트 (Backend 중심)
복잡한 최적화 알고리즘이 필요한 기능으로 백엔드에서 처리합니다.

**[구현 방식]**
- **`union/service/UnionService.java`:**
    - 유니온 배치 알고리즘이나 최적의 아티팩트 조합을 계산하는 로직이 포함되어 있음.
    - 캐릭터들의 등급(B, A, S, SS, SSS)과 직업 정보를 바탕으로 점령 가능한 칸 수를 계산.

---

## 3. '주보' 프로젝트 적용 시사점

### 3.1 따라해야 할 점 (Best Practices)
1.  **정적 데이터의 프론트엔드 이관:** 보스 가격, 아이템 정보처럼 자주 바뀌지 않는 데이터는 DB에 넣지 않고 프론트엔드 상수로 관리하는 것이 서버 비용 절감과 속도 측면에서 유리합니다.
2.  **Redux Toolkit 활용:** 복잡한 사용자 설정(보스 파티 구성)을 관리하기 위해 전역 상태 관리가 필수적입니다.
3.  **URL 공유 기능:** 별도의 로그인 없이도 자신의 세팅을 저장/공유할 수 있는 URL 파라미터 방식은 사용자 경험(UX)에 매우 좋습니다.

### 3.2 개선할 점 (Opportunities)
1.  **데이터 업데이트 자동화:** 현재 방식은 메이플스토리 패치로 보스 가격이 바뀌면 개발자가 코드를 수정해서 재배포해야 합니다. 이를 **백엔드 관리자 페이지**에서 수정하면 프론트가 API로 받아가는 방식(선택적 Hybrid)으로 개선하면 운영이 편해질 수 있습니다.
2.  **로그인 기능 부재:** 현재는 로컬 스토리지에 의존하므로 기기를 옮기면 데이터가 사라집니다. '주보' 프로젝트의 목표인 **OAuth 2.0 로그인**을 도입하면 사용자 데이터를 DB에 영구 저장하여 경쟁력을 가질 수 있습니다.

---

## 4. 결론
`MapleTool`은 **"서버는 꼭 필요한 데이터(넥슨 API)만 다루고, 나머지는 클라이언트에게 위임한다"**는 효율적인 철학으로 설계되었습니다.
우리는 이 구조를 기반으로 하되, **사용자 개인화(로그인/DB 저장)** 기능을 강화하여 한 단계 더 발전된 서비스를 만들 수 있습니다.
