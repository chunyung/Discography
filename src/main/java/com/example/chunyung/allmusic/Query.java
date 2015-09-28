package com.example.chunyung.allmusic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Query {
    public List<String> keywords;
    public Query() {
        this.keywords = new ArrayList<String>();
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String elem : this.keywords) {
            result.append(elem).append('+');
        }
        return result.toString().substring(0, result.length() - 1);
    }

    public String toJSON() {
        String result = null;
        try {
            result = new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
