package api.tests.resources;

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
@Story("PUT /v1/disk/resources")
@DisplayName("Создание папки")
public class PutResourcesTest {

    private static final String TEST_FOLDER_PREFIX = "/test-folder-";

    @Test
    @DisplayName("PUT /v1/disk/resources - создание папки")
    @Description("Позитивный тест: создание новой папки в корне Диска")
    @Tag("smoke")
    @Tag("positive")
    void testCreateFolder() {
        String folderPath = TEST_FOLDER_PREFIX + UUID.randomUUID();

        Allure.step("Создать папку");
        Response response = ApiClient.putFolder(folderPath);

        ResponseValidator.assertSuccess(response, HttpStatus.CREATED);

        Link link = response.as(Link.class);

        Allure.step("Проверить тело ответа", () -> {
            assertThat(link.getMethod()).isEqualTo("GET");
            assertThat(link.isTemplated()).isFalse();
            assertThat(link.getHref()).isNotEmpty();
        });

        ApiClient.deleteFolder(folderPath);
    }

    @Test
    @DisplayName("PUT /v1/disk/resources - 400 Bad Request: пустой путь")
    @Description("Негативный тест: создание папки с пустым путём")
    @Tag("regression")
    @Tag("negative")
    void testCreateFolderEmptyPath() {
        Allure.step("Создать папку с пустым путем");

        ResponseValidator.assertError(ApiClient.putFolder(""), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("PUT /v1/disk/resources - 401 Unauthorized: c невалидным токеном")
    @Description("Негативный тест: создание папки без авторизации")
    @Tag("regression")
    @Tag("negative")
    @Tag("auth")
    void testCreateFolderUnauthorized() {
        Allure.step("Отправить PUT запрос c невалидным токеном");

        ResponseValidator.assertError(ApiClient.putFolder(TEST_FOLDER_PREFIX, RequestSpec.getInvalidTokenSpec()),
                HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("PUT /v1/disk/resources - 409 Conflict: папка уже существует")
    @Description("Негативный тест: попытка создать уже существующую папку")
    @Tag("regression")
    @Tag("negative")
    void testCreateFolderConflict() {
        String folderPath = TEST_FOLDER_PREFIX + UUID.randomUUID();

        Allure.step("Создать папку первый раз");
        ResponseValidator.assertSuccess(ApiClient.putFolder(folderPath), HttpStatus.CREATED);

        Allure.step("Попытаться создать папку второй раз");
        ResponseValidator.assertError(ApiClient.putFolder(folderPath), HttpStatus.CONFLICT);

        ApiClient.deleteFolder(folderPath);
    }
}