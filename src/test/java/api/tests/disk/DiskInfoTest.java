package api.tests.disk;

import api.constants.HttpStatus;
import api.dto.diskInfo.Disk;
import api.specs.RequestSpec;
import api.utils.ApiClient;
import api.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@Epic("Яндекс.Диск API")
@Feature("Disk")
@Story("GET /v1/disk")
@DisplayName("Информация о Диске")
class DiskInfoTest {

    private static final String FIELDS_DISK_BASIC = "total_space,used_space,user.login";
    private static final String INVALID_FIELD = "invalid_field_xyz";
    private static final String INVALID_FIELDS_SYNTAX = "total_space, , used_space";

    @Test
    @DisplayName("GET /v1/disk - 200 OK: получение информации о Диске")
    @Description("Позитивный тест: проверка базовых полей ответа")
    @Tag("smoke")
    @Tag("positive")
    void testGetDiskInfo() {
        Allure.step("Отправить GET запрос на /v1/disk");
        Response response = ApiClient.getDiskInfo();

        ResponseValidator.assertSuccess(response, HttpStatus.OK);

        Disk disk = response.as(Disk.class);

        Allure.step("Проверить основные поля ответа", () -> {
            assertThat(disk.getTotalSpace()).isPositive();
            assertThat(disk.getUsedSpace()).isNotNull();
            assertThat(disk.getTrashSize()).isNotNull();
        });

        Allure.step("Проверить информацию о пользователе", () -> {
            assertThat(disk.getUser()).isNotNull();
            assertThat(disk.getUser().getLogin()).isNotEmpty();
        });

        Allure.step("Проверить системные папки", () -> {
            assertThat(disk.getSystemFolders()).isNotNull();
            assertThat(disk.getSystemFolders().getDownloads()).isNotEmpty();
            assertThat(disk.getSystemFolders().getApplications()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("GET /v1/disk - 200 OK: с параметром fields")
    @Description("Проверка работы параметра fields для ограничения возвращаемых полей")
    @Tag("regression")
    @Tag("positive")
    void testGetDiskInfoWithFields() {
        Allure.step("Отправить GET запрос с параметром fields");

        Response response = ApiClient.getDiskInfoWithFields(FIELDS_DISK_BASIC);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);

        Disk disk = response.as(Disk.class);

        Allure.step("Проверить, что вернулись только запрошенные поля", () -> {
            assertThat(disk.getTotalSpace()).isPositive();
            assertThat(disk.getUsedSpace()).isNotNull();
            assertThat(disk.getUser().getLogin()).isNotEmpty();
        });

        Allure.step("Проверить, что незапрошенные поля отсутствуют", () -> {
            assertThat(disk.getTrashSize()).isNull();
            assertThat(disk.getSystemFolders()).isNull();
        });
    }

    @Test
    @DisplayName("GET /v1/disk - игнорирует неизвестное поле в fields")
    @Description("API возвращает полный ответ, если указано неизвестное поле")
    @Tag("regression")
    @Tag("positive")
    void testGetDiskInfoIgnoresUnknownField() {
        Allure.step("Отправить GET запрос с неизвестным полем в fields");
        Response response = ApiClient.getDiskInfoWithFields(INVALID_FIELD);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);

        Disk disk = response.as(Disk.class);

        Allure.step("Проверить, что вернулся полный ответ", () -> {
            assertThat(disk.getTotalSpace()).isPositive();
            assertThat(disk.getUsedSpace()).isNotNull();
            assertThat(disk.getUser()).isNotNull();
        });
    }

    @Test
    @DisplayName("GET /v1/disk - устойчивость к некорректному синтаксису fields")
    @Description("API парсит валидные поля и игнорирует синтаксические ошибки")
    @Tag("regression")
    @Tag("positive")
    void testGetDiskInfoWithInvalidFieldsSyntax() {
        Allure.step("Отправить GET запрос с битым синтаксисом в fields");
        Response response = ApiClient.getDiskInfoWithFields(INVALID_FIELDS_SYNTAX);

        ResponseValidator.assertSuccess(response, HttpStatus.OK);

        Disk disk = response.as(Disk.class);

        Allure.step("Проверить, что валидные поля вернулись", () -> {
            assertThat(disk.getTotalSpace()).isPositive();
            assertThat(disk.getUsedSpace()).isNotNull();
        });
    }

    @Test
    @DisplayName("GET /v1/disk - 401 Unauthorized: с невалидным токеном")
    @Description("Негативный тест: запрос без авторизации возвращает 401")
    @Tag("regression")
    @Tag("negative")
    void testGetDiskInfoUnauthorized() {
        Allure.step("Отправить GET запрос без токена авторизации");

        ResponseValidator.assertError(ApiClient.getDiskInfo(RequestSpec.getInvalidTokenSpec()),
                HttpStatus.UNAUTHORIZED);
    }
}