package com.example.jablog.service;

import com.example.jablog.DTO.TypesToCompare;

public interface CompareElements {
    public boolean isSupport(TypesToCompare types);
    public boolean isCompared(String boardName, TypesToCompare types);
}
