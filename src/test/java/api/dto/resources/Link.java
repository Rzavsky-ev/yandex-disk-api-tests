package api.dto.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Ссылка на ресурс или асинхронную операцию.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {

    /**
     * URL для получения информации о ресурсе или статусе операции.
     */
    @JsonProperty("href")
    private String href;

    /**
     * HTTP-метод для запроса по href.
     */
    @JsonProperty("method")
    private String method;

    /**
     * Признак того, что URL содержит шаблон и требует подстановки параметров.
     */
    @JsonProperty("templated")
    private boolean templated;
}