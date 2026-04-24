package api.dto.diskInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Информация о пользователе Диска.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    /**
     * Логин пользователя.
     */
    @JsonProperty("login")
    private String login;
}