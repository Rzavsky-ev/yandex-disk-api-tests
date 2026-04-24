package api.dto.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

/**
 * Метаинформация о файле или папке на Диске.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource {

    /**
     * Имя ресурса.
     */
    @JsonProperty("name")
    private String name;

    /**
     * Тип ресурса: "dir" — папка, "file" — файл.
     */
    @JsonProperty("type")
    private String type;

    /**
     * Полный путь к ресурсу на Диске.
     */
    @JsonProperty("path")
    private String path;

    /**
     * Дата и время создания ресурса (ISO 8601).
     */
    @JsonProperty("created")
    private String created;

    /**
     * Дата и время изменения ресурса (ISO 8601).
     */
    @JsonProperty("modified")
    private String modified;

    /**
     * Вложенные ресурсы (только для непустых папок).
     */
    @JsonProperty("_embedded")
    private Embedded embedded;

    /**
     * Пользовательские свойства (ключ-значение).
     */
    @JsonProperty("custom_properties")
    private Map<String, String> customProperties;
}