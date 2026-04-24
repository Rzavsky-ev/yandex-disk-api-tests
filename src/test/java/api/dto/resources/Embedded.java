package api.dto.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * Вложенные ресурсы папки.
 * Возвращается только для непустых папок.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Embedded {

    /**
     * Массив вложенных ресурсов (папки и файлы).
     */
    @JsonProperty("items")
    private List<Resource> items;
}