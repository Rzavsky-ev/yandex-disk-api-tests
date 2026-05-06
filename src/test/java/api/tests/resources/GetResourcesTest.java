package api.tests.resources;

import api.base.BaseTest;
import api.constants.HttpStatus;
import api.dto.resources.Resource;
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
@Story("GET /v1/disk/resources")
@DisplayName("Получение информации о папке")
public class GetResourcesTest extends BaseTest {

    private static final String TEST_FOLDER_PREFIX = "/test-folder-";
    private static final String PARENT_PREFIX = "/parent-";
    private static final String CHILD_PREFIX = "/child-";
    private static final String NON_EXISTENT_PREFIX = "/non-existent-";
    private static final String TYPE_DIR = "dir";
    private static final String FIELDS_NAME_TYPE_CREATED = "name,type,created";

    @Test
    @DisplayName("Контракт: JSON Schema ответа GET /resources")
    @Description("Проверка полной структуры ответа")
    @Tag("contract")
    void testGetResourceContract() {
        String folderPath = "/contract-test-" + UUID.randomUUID();

        trackForCleanup(folderPath);

        Allure.step("Создать папку");
        ApiClient.putFolder(folderPath);

        Allure.step("Получить информацию и проверить схему");
        Response response = ApiClient.getFolder(folderPath);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);
        ResponseValidator.assertJsonSchema(response, "schemas/resource.json");
    }

    @Test
    @DisplayName("200 OK: получение информации о папке")
    @Description("Позитивный тест: получение метаинформации о существующей папке")
    @Tag("smoke")
    @Tag("positive")
    void testGetFolderInfo() {
        String folderPath = TEST_FOLDER_PREFIX + UUID.randomUUID();

        trackForCleanup(folderPath);

        Allure.step("Создать тестовую папку");
        ResponseValidator.assertSuccess(ApiClient.putFolder(folderPath), HttpStatus.CREATED);

        Allure.step("Получить информацию о папке");
        Response response = ApiClient.getFolder(folderPath);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);

        Resource resource = response.as(Resource.class);

        Allure.step("Проверить поля папки", () -> {
            assertThat(resource.getType()).isEqualTo(TYPE_DIR);
            assertThat(resource.getName()).isEqualTo(folderPath.substring(1));
            assertThat(resource.getPath()).contains(folderPath);
        });
    }

    @Test
    @DisplayName("200 OK: с параметром fields")
    @Description("Позитивный тест: проверка работы параметра fields для ограничения возвращаемых полей")
    @Tag("regression")
    @Tag("positive")
    void testGetFolderInfoWithFields() {
        String folderPath = TEST_FOLDER_PREFIX + UUID.randomUUID();

        trackForCleanup(folderPath);

        Allure.step("Создать тестовую папку");
        ResponseValidator.assertSuccess(ApiClient.putFolder(folderPath), HttpStatus.CREATED);

        Allure.step("Получить информацию с параметром fields");
        Response response = ApiClient.getFolderWithFields(folderPath, FIELDS_NAME_TYPE_CREATED);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);

        Resource resource = response.as(Resource.class);

        Allure.step("Проверить, что вернулись только запрошенные поля", () -> {
            assertThat(resource.getName()).isNotEmpty();
            assertThat(resource.getType()).isEqualTo(TYPE_DIR);
            assertThat(resource.getCreated()).isNotNull();
        });

        Allure.step("Проверить, что незапрошенные поля отсутствуют", () -> {
            assertThat(resource.getModified()).isNull();
            assertThat(resource.getPath()).isNull();
        });
    }

    @Test
    @DisplayName("200 OK: непустая папка содержит _embedded")
    @Description("Проверка, что API возвращает вложенные ресурсы")
    @Tag("regression")
    @Tag("positive")
    void testGetNonEmptyFolderInfo() {
        String parentPath = PARENT_PREFIX + UUID.randomUUID();
        String childPath = parentPath + CHILD_PREFIX + UUID.randomUUID();

        trackForCleanup(parentPath);

        Allure.step("Создать родительскую папку");
        ResponseValidator.assertSuccess(ApiClient.putFolder(parentPath), HttpStatus.CREATED);

        Allure.step("Создать вложенную папку");
        ResponseValidator.assertSuccess(ApiClient.putFolder(childPath), HttpStatus.CREATED);

        Allure.step("Получить информацию о родительской папке");
        Response response = ApiClient.getFolder(parentPath);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);

        Resource resource = response.as(Resource.class);

        Allure.step("Проверить, что _embedded содержит вложенную папку", () -> {
            assertThat(resource.getEmbedded()).isNotNull();
            assertThat(resource.getEmbedded().getItems()).hasSize(1);
            assertThat(resource.getEmbedded().getItems().get(0).getName())
                    .isEqualTo(childPath.substring(parentPath.length() + 1));
        });
    }

    @Test
    @DisplayName("400 Bad Request: пустой путь")
    @Description("Негативный тест: запрос с пустым параметром path")
    @Tag("regression")
    @Tag("negative")
    void testGetFolderEmptyPath() {
        Allure.step("Отправить GET запрос с пустым path");
        ResponseValidator.assertError(ApiClient.getFolder(""), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("401 Unauthorized: с невалидным токеном")
    @Description("Негативный тест: запрос без авторизации")
    @Tag("regression")
    @Tag("negative")
    @Tag("auth")
    void testGetFolderUnauthorized() {
        Allure.step("Отправить GET запрос с невалидным токеном");
        ResponseValidator.assertError(ApiClient.getFolder(TEST_FOLDER_PREFIX, RequestSpec.getInvalidTokenSpec()),
                HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("404 Not Found: папка не существует")
    @Description("Негативный тест: запрос информации о несуществующей папке")
    @Tag("regression")
    @Tag("negative")
    void testGetNonExistentFolder() {
        String folderPath = NON_EXISTENT_PREFIX + UUID.randomUUID();

        Allure.step("Отправить GET запрос к несуществующей папке");
        ResponseValidator.assertError(ApiClient.getFolder(folderPath), HttpStatus.NOT_FOUND);
    }
}