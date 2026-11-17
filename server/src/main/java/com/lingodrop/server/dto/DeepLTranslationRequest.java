package com.lingodrop.server.dto;

import java.util.List;

public record DeepLTranslationRequest(List<String> text, String source_lang, String target_lang) {

}
