package com.example.jablog.service.security;

import com.example.jablog.DTO.TypesToCompare;
import com.example.jablog.service.CompareElements;
import org.springframework.stereotype.Component;

@Component
public class ComparePost implements CompareElements {


    @Override
    public boolean isSupport(TypesToCompare types){
        return types.getPost()!=null;
    }

    @Override
    public boolean isCompared(String boardName, TypesToCompare types){

    }
}
