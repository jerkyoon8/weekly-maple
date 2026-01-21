package com.maple.jubo.service;

import com.maple.jubo.dto.nexon.CharacterBasicResponse;
import com.maple.jubo.dto.nexon.OcidResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapleApiService {

    private final RestTemplate restTemplate;

    @Value("${nexon.api.key}")
    private String apiKey;

    @Value("${nexon.api.url}")
    private String apiUrl;

    /**
     * 캐릭터 닉네임으로 OCID 조회
     */
    public String getOcidByNickname(String nickname) {
        String url = apiUrl + "/maplestory/v1/id";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-nxopen-api-key", apiKey);
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("character_name", nickname);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<OcidResponse> response = restTemplate.exchange(
                    builder.build().toUri(),
                    HttpMethod.GET,
                    entity,
                    OcidResponse.class
            );
            
            if (response.getBody() != null) {
                return response.getBody().getOcid();
            }
        } catch (HttpClientErrorException e) {
            log.error("Nexon API Error (getOcid): {}", e.getMessage());
            // 예외 처리는 컨트롤러에서 하거나 여기서 커스텀 예외를 던질 수 있습니다.
            // 일단 null 반환 후 컨트롤러에서 처리
        }
        return null;
    }

    /**
     * OCID로 캐릭터 기본 정보 조회
     */
    public CharacterBasicResponse getCharacterBasic(String ocid) {
        if (ocid == null) return null;

        String url = apiUrl + "/maplestory/v1/character/basic";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-nxopen-api-key", apiKey);

        // 조회 기준일: 어제 날짜 (yyyy-MM-dd)
        String yesterday = java.time.LocalDate.now().minusDays(1)
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("ocid", ocid)
                .queryParam("date", yesterday);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<CharacterBasicResponse> response = restTemplate.exchange(
                    builder.build().toUri(),
                    HttpMethod.GET,
                    entity,
                    CharacterBasicResponse.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Nexon API Error (getCharacterBasic): {}", e.getMessage());
        }
        return null;
    }
}
