package api.dto.diskInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Системные папки Диска.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemFolders {

    /**
     * Путь к папке "Загрузки".
     */
    @JsonProperty("downloads")
    private String downloads;

    /**
     * Путь к папке "Приложения".
     */
    @JsonProperty("applications")
    private String applications;
}