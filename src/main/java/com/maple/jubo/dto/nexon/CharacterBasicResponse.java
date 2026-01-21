package com.maple.jubo.dto.nexon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CharacterBasicResponse {
    @JsonProperty("character_name")
    private String characterName;

    @JsonProperty("world_name")
    private String worldName;

    @JsonProperty("character_class")
    private String characterClass;

    @JsonProperty("character_level")
    private Integer characterLevel;

    @JsonProperty("character_image")
    private String characterImage;
    
    // 필요한 필드가 더 있다면 여기에 추가하면 됩니다.
    // 예: character_guild_name, character_gender 등
}
