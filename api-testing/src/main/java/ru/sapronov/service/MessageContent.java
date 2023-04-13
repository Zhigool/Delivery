package ru.sapronov.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageContent {

    @JsonProperty("Body")
    private String body;
}
