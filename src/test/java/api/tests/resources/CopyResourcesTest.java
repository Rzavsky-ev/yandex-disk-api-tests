package api.tests.resources;

import api.base.BaseTest;
import api.constants.HttpStatus;
import api.dto.resources.Link;
import api.specs.RequestSpec;
import api.utils.ApiClient;
import api.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Яндекс.Диск API")
@Feature("Resources")
@Story("POST /v1/disk/resources/copy")
@DisplayName("Копирование папки")
public class CopyResourcesTest extends BaseTest {

    private static final String SOURCE_PREFIX = "/source-";
    private static final String TARGET_PREFIX = "/target-";
    private static final String NON_EXISTENT_PREFIX = "/non-existent-";

    @Test
    @DisplayName("Контракт: JSON Schema ответа POST /resources/copy")
    @Description("Проверка полной структуры ответа")
    @Tag("contract")
    void testCopyContract() {
        String sourcePath = SOURCE_PREFIX + UUID.randomUUID();
        String targetPath = TARGET_PREFIX + UUID.randomUUID();

        trackForCleanup(sourcePath);
        trackForCleanup(targetPath);

        ApiClient.putFolder(sourcePath);

        Response response = ApiClient.copyFolder(sourcePath, targetPath);

        ResponseValidator.assertSuccess(response, HttpStatus.CREATED);
        Allure.step("Проверить статус 201 Created или 202 Accepted", () -> {
            assertThat(response.statusCode()).isIn(HttpStatus.CREATED, HttpStatus.ACCEPTED);
        });
        ResponseValidator.assertJsonSchema(response, "schemas/link.json");
    }

    @Test
    @DisplayName("201 Created: копирование папки")
    @Description("Позитивный тест: копирование существующей папки")
    @Tag("smoke")
    @Tag("positive")
    void testCopyFolder() {
        String sourcePath = SOURCE_PREFIX + UUID.randomUUID();
        String targetPath = TARGET_PREFIX + UUID.randomUUID();

        trackForCleanup(sourcePath);
        trackForCleanup(targetPath);

        Allure.step("Создать исходную папку");
        ResponseValidator.assertSuccess(ApiClient.putFolder(sourcePath), HttpStatus.CREATED);

        Allure.step("Скопировать папку");
        Response response = ApiClient.copyFolder(sourcePath, targetPath);

        Allure.step("Проверить статус 201 Created или 202 Accepted", () -> {
            assertThat(response.statusCode()).isIn(HttpStatus.CREATED, HttpStatus.ACCEPTED);
        });

        Link link = response.as(Link.class);

        Allure.step("Проверить тело ответа", () -> {
            assertThat(link.getMethod()).isEqualTo("GET");
            assertThat(link.getHref()).isNotEmpty();
            assertThat(link.isTemplated()).isFalse();

        });
    }

    @Test
    @DisplayName("404 Not Found: исходная папка не существует")
    @Description("Негативный тест: копирование несуществующей папки")
    @Tag("regression")
    @Tag("negative")
    void testCopyNonExistentFolder() {
        String sourcePath = NON_EXISTENT_PREFIX + UUID.randomUUID();
        String targetPath = TARGET_PREFIX + UUID.randomUUID();

        Allure.step("Попытаться скопировать несуществующую папку");
        ResponseValidator.assertError(ApiClient.copyFolder(sourcePath, targetPath), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("409 Conflict: целевая папка уже существует")
    @Description("Негативный тест: копирование в уже существующую папку без overwrite")
    @Tag("regression")
    @Tag("negative")
    void testCopyFolderConflict() {
        String sourcePath = SOURCE_PREFIX + UUID.randomUUID();
        String targetPath = TARGET_PREFIX + UUID.randomUUID();

        trackForCleanup(sourcePath);
        trackForCleanup(targetPath);

        Allure.step("Создать исходную и целевую папки");
        ResponseValidator.assertSuccess(ApiClient.putFolder(sourcePath), HttpStatus.CREATED);
        ResponseValidator.assertSuccess(ApiClient.putFolder(targetPath), HttpStatus.CREATED);

        Allure.step("Попытаться скопировать в существующую папку");

        ResponseValidator.assertError(ApiClient.copyFolderWithOverwrite(sourcePath, targetPath, false),
                HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("400 Bad Request: пустой from")
    @Description("Негативный тест: копирование с пустым параметром from")
    @Tag("regression")
    @Tag("negative")
    void testCopyFolderEmptyFrom() {
        Allure.step("Копировать с пустым параметром from");
        ResponseValidator.assertError(ApiClient.copyFolder("", TARGET_PREFIX), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("401 Unauthorized: с невалидным токеном")
    @Description("Негативный тест: копирование без авторизации")
    @Tag("regression")
    @Tag("negative")
    @Tag("auth")
    void testCopyFolderUnauthorized() {
        Allure.step("Копировать с невалидным токеном");
        ResponseValidator.assertError(ApiClient.copyFolder(SOURCE_PREFIX, TARGET_PREFIX, RequestSpec.getInvalidTokenSpec()),
                HttpStatus.UNAUTHORIZED);
    }
}