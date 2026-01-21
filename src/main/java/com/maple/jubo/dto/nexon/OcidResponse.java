package com.maple.jubo.dto.nexon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OcidResponse {
    @JsonProperty("ocid")
    private String ocid;
}
