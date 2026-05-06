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

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Яндекс.Диск API")
@Feature("Resources")
@Story("PATCH /v1/disk/resources")
@DisplayName("Обновление свойств")
public class PatchResourcesTest extends BaseTest {

    private static final String TEST_FOLDER_PREFIX = "/test-folder-";

    @Test
    @DisplayName("Контракт: JSON Schema ответа PATCH /resources")
    @Description("Проверка полной структуры ответа")
    @Tag("contract")
    void testPatchContract() {
        String folderPath = "/contract-test-" + UUID.randomUUID();

        trackForCleanup(folderPath);

        ApiClient.putFolder(folderPath);

        Map<String, Object> body = Map.of("custom_properties", Map.of("foo", "bar"));
        Response response = ApiClient.patchFolder(folderPath, body);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);
        ResponseValidator.assertJsonSchema(response, "schemas/resource.json");
    }

    @Test
    @DisplayName("200 OK: обновление custom_properties")
    @Description("Позитивный тест: добавление пользовательских свойств к папке")
    @Tag("regression")
    @Tag("positive")
    void testUpdateCustomProperties() {
        String folderPath = TEST_FOLDER_PREFIX + UUID.randomUUID();

        trackForCleanup(folderPath);

        Allure.step("Создать тестовую папку");
        ResponseValidator.assertSuccess(ApiClient.putFolder(folderPath), HttpStatus.CREATED);

        Allure.step("Обновить custom_properties");
        Map<String, String> customProps = Map.of(
                "foo", "bar",
                "test", "123");
        Map<String, Object> body = Map.of("custom_properties", customProps);

        Response response = ApiClient.patchFolder(folderPath, body);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);

        Resource resource = response.as(Resource.class);

        Allure.step("Проверить, что свойства применились", () -> {
            assertThat(resource.getCustomProperties()).isNotNull();
            assertThat(resource.getCustomProperties()).containsEntry("foo", "bar");
        });
    }

    @Test
    @DisplayName("400 Bad Request: пустой путь")
    @Description("Негативный тест: обновление с пустым path")
    @Tag("regression")
    @Tag("negative")
    void testPatchEmptyPath() {
        Map<String, Object> body = Map.of("custom_properties", Map.of("foo", "bar"));

        ResponseValidator.assertError(ApiClient.patchFolder("", body), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("401 Unauthorized: с невалидным токеном")
    @Description("Негативный тест: обновление без авторизации")
    @Tag("regression")
    @Tag("negative")
    @Tag("auth")
    void testPatchUnauthorized() {
        Map<String, Object> body = Map.of("custom_properties", Map.of("foo", "bar"));

        ResponseValidator.assertError(ApiClient.patchFolder(TEST_FOLDER_PREFIX, body, RequestSpec.getInvalidTokenSpec()),
                HttpStatus.UNAUTHORIZED);
    }
}