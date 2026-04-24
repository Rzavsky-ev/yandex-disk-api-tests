package api.dto.diskInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Модель ответа API — информация о Диске пользователя.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Disk {

    /**
     * Общий объем диска в байтах.
     */
    @JsonProperty("total_space")
    private Long totalSpace;

    /**
     * Используемый объем диска в байтах.
     */
    @JsonProperty("used_space")
    private Long usedSpace;

    /**
     * Объем файлов в корзине в байтах.
     */
    @JsonProperty("trash_size")
    private Long trashSize;

    /**
     * Информация о пользователе.
     */
    @JsonProperty("user")
    private User user;

    /**
     * Системные папки Диска.
     */
    @JsonProperty("system_folders")
    private SystemFolders systemFolders;
}