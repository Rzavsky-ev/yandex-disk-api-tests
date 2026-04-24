package api.specs;

import api.exceptions.UtilityClassException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Фабрика спецификаций RestAssured для запросов к API Яндекс.Диска.
 */
public class RequestSpec {

    /**
     * Токен авторизации из переменной окружения.
     */
    private static final String TOKEN = System.getenv("YANDEX_DISK_TOKEN");

    /**
     * Базовый URL API Яндекс.Диска.
     */
    private static final String BASE_URL = "https://cloud-api.yandex.net/v1/disk";

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "OAuth ";
    private static final String INVALID_TOKEN = "InvalidToken";
    private static final String TOKEN_NOT_FOUND_MESSAGE =
            "Токен не найден! Установите переменную окружения YANDEX_DISK_TOKEN";

    private RequestSpec() {
        throw new UtilityClassException(getClass());
    }

    /**
     * Спецификация с валидным токеном.
     *
     * @return RequestSpecification с авторизацией
     * @throws IllegalStateException если переменная окружения не установлена
     */
    public static RequestSpecification getSpec() {
        if (TOKEN == null || TOKEN.isEmpty()) {
            throw new IllegalStateException(TOKEN_NOT_FOUND_MESSAGE);
        }
        return buildSpec(AUTH_PREFIX + TOKEN);
    }

    /**
     * Спецификация с невалидным токеном для проверки 401 ошибок.
     *
     * @return RequestSpecification с токеном "InvalidToken"
     */
    public static RequestSpecification getInvalidTokenSpec() {
        return buildSpec(AUTH_PREFIX + INVALID_TOKEN);
    }

    /**
     * Создаёт базовую спецификацию с указанным заголовком авторизации.
     *
     * @param authHeader значение заголовка Authorization
     * @return настроенная RequestSpecification
     */
    private static RequestSpecification buildSpec(String authHeader) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .addHeader(AUTH_HEADER, authHeader)
                .setContentType(ContentType.JSON)
                .build();
    }
}