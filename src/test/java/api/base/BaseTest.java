package api.base;

import api.utils.ApiClient;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для всех тестов, работающих с ресурсами Яндекс.Диска.
 *
 * <p>Предоставляет механизм автоматической очистки созданных папок после каждого теста.
 * Даже если тест упал, папки будут удалены в методе {@code @AfterEach}.
 */
public abstract class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    private final List<String> createdPaths = new ArrayList<>();

    /**
     * Добавляет путь созданной папки в список на автоматическое удаление.
     *
     * @param path путь к папке на Яндекс.Диске
     */
    protected void trackForCleanup(String path) {
        if (path != null && !path.isEmpty()) {
            createdPaths.add(path);
        }
    }

    /**
     * Удаляет все папки, зарегистрированные через trackForCleanup.
     * Выполняется после каждого теста, даже при падении теста.
     */
    @AfterEach
    void cleanUpCreatedFolders() {
        for (String path : createdPaths) {
            try {
                ApiClient.deleteFolder(path);
            } catch (Exception e) {
                log.warn("Не удалось удалить папку при очистке {}", path, e);
            }
        }
        createdPaths.clear();
    }
}