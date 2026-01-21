package com.maple.jubo.controller;

import com.maple.jubo.dto.nexon.CharacterBasicResponse;
import com.maple.jubo.service.MapleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/weekly-boss")
@RequiredArgsConstructor
public class WeeklyBossController {

    private final MapleApiService mapleApiService;

    @GetMapping
    public String index() {
        return "index"; // templates/index.html
    }

    @GetMapping("/search")
    public String searchCharacter(@RequestParam("nickname") String nickname, Model model) {
        // 1. 닉네임으로 OCID 조회
        String ocid = mapleApiService.getOcidByNickname(nickname);

        if (ocid != null) {
            // 2. OCID로 캐릭터 정보 조회
            CharacterBasicResponse characterInfo = mapleApiService.getCharacterBasic(ocid);
            model.addAttribute("character", characterInfo);
        } else {
            model.addAttribute("error", "존재하지 않는 캐릭터이거나 검색에 실패했습니다.");
        }

        model.addAttribute( "searchedNickname", nickname);
        return "index";
    }
}
