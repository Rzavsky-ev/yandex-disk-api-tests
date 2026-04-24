package api.tests.resources;

import api.constants.HttpStatus;
import api.specs.RequestSpec;
import api.utils.ApiClient;
import api.utils.ResponseValidator;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@Epic("Яндекс.Диск API")
@Feature("Resources")
@Story("DELETE /v1/disk/resources")
@DisplayName("Удаление папки")
public class DeleteResourcesTest {

    private static final String TEST_FOLDER_PREFIX = "/test-folder-";
    private static final String NON_EXISTENT_PREFIX = "/non-existent-";

    @Test
    @DisplayName("204 No Content / 202 Accepted: удаление папки")
    @Description("Позитивный тест: удаление существующей папки")
    @Tag("smoke")
    @Tag("positive")
    void testDeleteFolder() {
        String folderPath = TEST_FOLDER_PREFIX + UUID.randomUUID();

        Allure.step("Создать тестовую папку");
        ResponseValidator.assertSuccess(ApiClient.putFolder(folderPath), HttpStatus.CREATED);

        Allure.step("Удалить папку");
        ApiClient.deleteFolder(folderPath);

        Allure.step("Проверить, что папка удалена");
        ResponseValidator.assertError(ApiClient.getFolder(folderPath), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("204 No Content: удаление папки permanently")
    @Description("Позитивный тест: безвозвратное удаление папки")
    @Tag("regression")
    @Tag("positive")
    void testDeleteFolderPermanently() {
        String folderPath = TEST_FOLDER_PREFIX + UUID.randomUUID();

        Allure.step("Создать тестовую папку");
        ResponseValidator.assertSuccess(ApiClient.putFolder(folderPath), HttpStatus.CREATED);

        Allure.step("Удалить папку безвозвратно");
        ApiClient.requestDeleteFolder(folderPath, true);

        Allure.step("Проверить, что папка удалена безвозвратно");
        ResponseValidator.assertError(ApiClient.getFolder(folderPath), HttpStatus.NOT_FOUND);

        Allure.step("Проверить, что папка не в корзине");
        ResponseValidator.assertError(ApiClient.getTrashFolder(folderPath), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("404 Not Found: удаление несуществующей папки")
    @Description("Негативный тест: попытка удалить несуществующую папку")
    @Tag("regression")
    @Tag("negative")
    void testDeleteNonExistentFolder() {
        String folderPath = NON_EXISTENT_PREFIX + UUID.randomUUID();

        Allure.step("Попытаться удалить несуществующую папку");
        ResponseValidator.assertError(ApiClient.requestDeleteFolder(folderPath), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("400 Bad Request: пустой путь")
    @Description("Негативный тест: удаление с пустым параметром path")
    @Tag("regression")
    @Tag("negative")
    void testDeleteFolderEmptyPath() {
        Allure.step("Попытаться удалить с пустым параметром path");
        ResponseValidator.assertError(ApiClient.requestDeleteFolder(""), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("401 Unauthorized: с невалидным токеном")
    @Description("Негативный тест: удаление без авторизации")
    @Tag("regression")
    @Tag("negative")
    @Tag("auth")
    void testDeleteFolderUnauthorized() {
        Allure.step("Попытаться удалить с невалидным токеном");
        ResponseValidator.assertError(ApiClient.requestDeleteFolder(TEST_FOLDER_PREFIX, RequestSpec.getInvalidTokenSpec()),
                HttpStatus.UNAUTHORIZED);
    }
}