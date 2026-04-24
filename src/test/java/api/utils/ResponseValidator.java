package api.utils;

import api.dto.ErrorResponse;
import api.exceptions.UtilityClassException;
import io.qameta.allure.Allure;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Валидатор ответов API.
 * Каждая проверка добавляется в Allure-отчёт как отдельный шаг.
 */
public class ResponseValidator {

    private ResponseValidator() {
        throw new UtilityClassException(getClass());
    }

    /**
     * Проверяет, что статус ответа равен ожидаемому.
     *
     * @param response       ответ сервера
     * @param expectedStatus ожидаемый HTTP-статус
     */
    public static void assertSuccess(Response response, int expectedStatus) {
        Allure.step("Проверить статус ответа " + expectedStatus, () -> {
            assertThat(response.statusCode()).isEqualTo(expectedStatus);
        });
    }

    /**
     * Проверяет статус ответа и поля ошибки.
     *
     * @param response       ответ сервера
     * @param expectedStatus ожидаемый HTTP-статус ошибки
     */
    public static void assertError(Response response, int expectedStatus) {
        assertSuccess(response, expectedStatus);

        Allure.step("Проверить тело ошибки", () -> {
            ErrorResponse error = response.as(ErrorResponse.class);
            assertThat(error.getError()).isNotEmpty();
            assertThat(error.getMessage()).isNotEmpty();
            assertThat(error.getDescription()).isNotEmpty();
        });
    }
}