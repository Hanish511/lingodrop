package com.lingodrop.server.dto;

import java.util.List;

public record DeepLTranslationResponse(List<TranslationItem> translations) {

    public record TranslationItem(String detected_source_language, String text){}

}
