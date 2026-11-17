package com.lingodrop.server.service;

public interface TranslationService {

    String translate(String text, String sourceLanguage, String targetLanguage);

}
