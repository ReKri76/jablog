package com.example.jablog.DTO;

import lombok.Data;

import java.util.LinkedList;

@Data
public class CleanPostRecord {
    private long number;
    private LinkedList<String> urls;
}
