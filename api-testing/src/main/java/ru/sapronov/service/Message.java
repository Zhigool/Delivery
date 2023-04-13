package ru.sapronov.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    @JsonProperty("ID")
    private String id;
    @JsonProperty("Content")
    private MessageContent content;
    @JsonProperty("Created")
    private LocalDateTime created;

}
