package com.maple.jubo.controller;

import com.maple.jubo.dto.nexon.CharacterBasicResponse;
import com.maple.jubo.service.MapleApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/weekly-boss")
@RequiredArgsConstructor
public class WeeklyBossController {

    private final MapleApiService mapleApiService;

    @GetMapping
    public String index(Model model, HttpSession session) {
        List<CharacterBasicResponse> characterList = (List<CharacterBasicResponse>) session.getAttribute("characterList");
        if (characterList != null) {
            model.addAttribute("characterList", characterList);
        }
        return "index";
    }

    @GetMapping("/search")
    public String searchCharacter(@RequestParam("nickname") String nickname, Model model, HttpSession session) {
        // 세션에서 기존 리스트 가져오기
        List<CharacterBasicResponse> characterList = (List<CharacterBasicResponse>) session.getAttribute("characterList");
        if (characterList == null) {
            characterList = new ArrayList<>();
        }

        // 1. 닉네임으로 OCID 조회
        String ocid = mapleApiService.getOcidByNickname(nickname);

        if (ocid != null) {
            // 2. OCID로 캐릭터 정보 조회
            CharacterBasicResponse characterInfo = mapleApiService.getCharacterBasic(ocid);
            if (characterInfo != null) {
                // 중복 방지 (선택사항이나 UX상 좋음)
                boolean exists = characterList.stream()
                        .anyMatch(c -> c.getCharacterName().equals(characterInfo.getCharacterName()));
                
                if (!exists) {
                    characterList.add(characterInfo);
                }
            }
        } else {
            model.addAttribute("error", "존재하지 않는 캐릭터이거나 검색에 실패했습니다.");
        }

        // 세션 및 모델 업데이트
        session.setAttribute("characterList", characterList);
        model.addAttribute("characterList", characterList);
        model.addAttribute("searchedNickname", nickname);
        
        return "index";
    }
}
