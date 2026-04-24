package api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Модель ответа при ошибке API.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

    /**
     * Код ошибки.
     */
    @JsonProperty("error")
    private String error;

    /**
     * Техническое описание ошибки.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Сообщение для пользователя.
     */
    @JsonProperty("message")
    private String message;
}