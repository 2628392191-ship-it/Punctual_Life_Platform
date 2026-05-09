package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessSuggestionVO implements Serializable {

    private String suggestionCode;

    private String priority;

    private String title;

    private String content;

    private String relatedRiskCode;
}
