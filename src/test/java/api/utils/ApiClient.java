package api.utils;

import api.constants.HttpStatus;
import api.exceptions.UtilityClassException;
import api.specs.RequestSpec;
import io.qameta.allure.Allure;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Клиент для отправки запросов к API Яндекс.Диска.
 *
 * <p>Содержит методы для работы с ресурсами (папки, файлы) и информацией о Диске.
 * Каждый метод добавляет шаги и вложения в Allure-отчёт.
 */
public class ApiClient {

    private static final String RESOURCES_ENDPOINT = "/resources";
    private static final String COPY_ENDPOINT = "/resources/copy";
    private static final String TRASH_ENDPOINT = "/trash/resources";

    private static final String PATH_PARAM = "path";
    private static final String FROM_PARAM = "from";
    private static final String FIELDS_PARAM = "fields";
    private static final String OVERWRITE_PARAM = "overwrite";
    private static final String PERMANENTLY_PARAM = "permanently";
    private static final String RESPONSE_ATTACHMENT = "application/json";
    private static final String RESPONSE_PREFIX = "Response ";
    private static final String DELETE_RESPONSE = "Delete Response";
    private static final String EMPTY_BODY = "<empty>";

    private ApiClient() {
        throw new UtilityClassException(getClass());
    }

    // ==================== PUT ====================

    /**
     * Создаёт новую папку с валидным токеном.
     *
     * @param folderPath путь к создаваемой папке (например, "/test-folder")
     * @return ответ с ожидаемым статусом 201
     */
    public static Response putFolder(String folderPath) {
        return putFolder(folderPath, RequestSpec.getSpec());
    }

    /**
     * Создаёт новую папку с указанной спецификацией.
     *
     * @param folderPath путь к создаваемой папке
     * @param spec       спецификация запроса (с валидным или невалидным токеном)
     * @return ответ сервера
     */
    public static Response putFolder(String folderPath, RequestSpecification spec) {
        Allure.step("Отправить PUT запрос на /resources: " + folderPath);
        Response response = given()
                .spec(spec)
                .queryParam(PATH_PARAM, folderPath)
                .when()
                .put(RESOURCES_ENDPOINT);

        Allure.addAttachment(RESPONSE_PREFIX + response.statusCode(),
                RESPONSE_ATTACHMENT, response.asPrettyString());

        return response;
    }

    // ==================== GET ====================

    /**
     * Получает метаинформацию о папке с валидным токеном.
     *
     * @param folderPath путь к папке
     * @return ответ с ожидаемым статусом 200
     */
    public static Response getFolder(String folderPath) {
        return getFolder(folderPath, null, RequestSpec.getSpec());
    }

    /**
     * Получает метаинформацию о папке с указанной спецификацией.
     *
     * @param folderPath путь к папке
     * @param spec       спецификация запроса (для проверки 401 ошибок)
     * @return ответ сервера
     */
    public static Response getFolder(String folderPath, RequestSpecification spec) {
        return getFolder(folderPath, null, spec);
    }

    /**
     * Получает метаинформацию о папке с ограниченным набором полей.
     *
     * @param folderPath путь к папке
     * @param fields     список запрашиваемых полей через запятую
     * @return ответ с запрошенными полями
     */
    public static Response getFolderWithFields(String folderPath, String fields) {
        return getFolder(folderPath, fields, RequestSpec.getSpec());
    }

    /**
     * Получает информацию о папке в корзине.
     *
     * @param folderPath путь к папке в корзине
     * @return ответ сервера
     */
    public static Response getTrashFolder(String folderPath) {
        Allure.step("Получить информацию о папке в корзине: " + folderPath);
        Response response = given()
                .spec(RequestSpec.getSpec())
                .queryParam(PATH_PARAM, folderPath)
                .when()
                .get(TRASH_ENDPOINT);

        Allure.addAttachment(RESPONSE_PREFIX + response.statusCode(),
                RESPONSE_ATTACHMENT, response.asPrettyString());

        return response;
    }

    /**
     * Получает метаинформацию о папке.
     *
     * @param folderPath путь к папке
     * @param fields     список полей через запятую (null — все поля)
     * @param spec       спецификация запроса
     * @return ответ сервера
     */
    private static Response getFolder(String folderPath, String fields, RequestSpecification spec) {
        Allure.step("Получить информацию о папке: " + folderPath
                + (fields != null ? " (fields: " + fields + ")" : ""));
        Response response = given()
                .spec(spec)
                .queryParam(PATH_PARAM, folderPath)
                .queryParam(FIELDS_PARAM, fields)
                .when()
                .get(RESOURCES_ENDPOINT);

        Allure.addAttachment(RESPONSE_PREFIX + response.statusCode(),
                RESPONSE_ATTACHMENT, response.asPrettyString());

        return response;
    }

    // ==================== POST (copy) ====================

    /**
     * Копирует папку с валидным токеном.
     *
     * @param fromPath путь к исходной папке
     * @param toPath   путь для создаваемой копии
     * @return ответ с ожидаемым статусом 201 или 202
     */
    public static Response copyFolder(String fromPath, String toPath) {
        return copyFolder(fromPath, toPath, RequestSpec.getSpec());
    }

    /**
     * Копирует папку с указанной спецификацией.
     *
     * @param fromPath путь к исходной папке
     * @param toPath   путь для создаваемой копии
     * @param spec     спецификация запроса
     * @return ответ сервера
     */
    public static Response copyFolder(String fromPath, String toPath, RequestSpecification spec) {
        Allure.step("Скопировать папку: " + fromPath + " в " + toPath);
        Response response = given()
                .spec(spec)
                .queryParam(FROM_PARAM, fromPath)
                .queryParam(PATH_PARAM, toPath)
                .when()
                .post(COPY_ENDPOINT);

        Allure.addAttachment(RESPONSE_PREFIX + response.statusCode(),
                RESPONSE_ATTACHMENT, response.asPrettyString());

        return response;
    }

    /**
     * Копирует папку с явным указанием перезаписи.
     *
     * @param fromPath  путь к исходной папке
     * @param toPath    путь для создаваемой копии
     * @param overwrite true — перезаписать существующий ресурс
     * @return ответ сервера
     */
    public static Response copyFolderWithOverwrite(String fromPath,
                                                   String toPath,
                                                   boolean overwrite) {
        Allure.step("Скопировать папку (overwrite=" + overwrite + "): " + fromPath + " в " + toPath);
        Response response = given()
                .spec(RequestSpec.getSpec())
                .queryParam(FROM_PARAM, fromPath)
                .queryParam(PATH_PARAM, toPath)
                .queryParam(OVERWRITE_PARAM, overwrite)
                .when()
                .post(COPY_ENDPOINT);

        Allure.addAttachment(RESPONSE_PREFIX + response.statusCode(),
                RESPONSE_ATTACHMENT, response.asPrettyString());

        return response;
    }

    // ==================== PATCH ====================

    /**
     * Обновляет пользовательские свойства папки с валидным токеном.
     *
     * @param folderPath путь к папке
     * @param body       тело запроса с custom_properties
     * @return ответ с ожидаемым статусом 200
     */
    public static Response patchFolder(String folderPath, Map<String, Object> body) {
        return patchFolder(folderPath, body, RequestSpec.getSpec());
    }

    /**
     * Обновляет пользовательские свойства папки с указанной спецификацией.
     *
     * @param folderPath путь к папке
     * @param body       тело запроса с custom_properties
     * @param spec       спецификация запроса
     * @return ответ сервера
     */
    public static Response patchFolder(String folderPath,
                                       Map<String, Object> body,
                                       RequestSpecification spec) {
        Allure.step("Обновить свойства папки: " + folderPath);
        Response response = given()
                .spec(spec)
                .queryParam(PATH_PARAM, folderPath)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch(RESOURCES_ENDPOINT);

        Allure.addAttachment(RESPONSE_PREFIX + response.statusCode(),
                RESPONSE_ATTACHMENT, response.asPrettyString());

        return response;
    }

    // ==================== DELETE ====================

    /**
     * Удаляет папку (перемещает в корзину) с валидным токеном.
     *
     * @param folderPath путь к удаляемой папке
     * @return ответ сервера
     */
    public static Response requestDeleteFolder(String folderPath) {
        return requestDeleteFolder(folderPath, false, RequestSpec.getSpec());
    }

    /**
     * Удаляет папку с указанием безвозвратности.
     *
     * @param folderPath  путь к удаляемой папке
     * @param permanently true — удалить безвозвратно, минуя корзину
     */
    public static void requestDeleteFolder(String folderPath, boolean permanently) {
        requestDeleteFolder(folderPath, permanently, RequestSpec.getSpec());
    }

    /**
     * Удаляет папку с указанной спецификацией.
     *
     * @param folderPath путь к удаляемой папке
     * @param spec       спецификация запроса (для проверки 401 ошибок)
     * @return ответ сервера
     */
    public static Response requestDeleteFolder(String folderPath, RequestSpecification spec) {
        return requestDeleteFolder(folderPath, false, spec);
    }

    /**
     * Удаляет папку.
     *
     * @param folderPath  путь к удаляемой папке
     * @param permanently true — безвозвратное удаление, false — в корзину
     * @param spec        спецификация запроса
     * @return ответ сервера
     */
    private static Response requestDeleteFolder(String folderPath,
                                                boolean permanently,
                                                RequestSpecification spec) {
        Allure.step("Удалить папку" + (permanently ? " безвозвратно" : "") + ": " + folderPath);
        Response response = given()
                .spec(spec)
                .queryParam(PATH_PARAM, folderPath)
                .queryParam(PERMANENTLY_PARAM, permanently)
                .when()
                .delete(RESOURCES_ENDPOINT);

        Allure.addAttachment(DELETE_RESPONSE, RESPONSE_ATTACHMENT,
                response.body().asString().isEmpty() ? EMPTY_BODY : response.asPrettyString());

        return response;
    }

    /**
     * Удаляет папку и проверяет, что статус ответа 202 или 204.
     * Используется для очистки тестовых данных.
     *
     * @param folderPath путь к удаляемой папке
     */
    public static void deleteFolder(String folderPath) {
        Response response = requestDeleteFolder(folderPath);
        Allure.step("Проверить статус ответа (202 или 204)", () -> {
            assertThat(response.statusCode()).isIn(HttpStatus.ACCEPTED, HttpStatus.NO_CONTENT);
        });
    }

    // ==================== Disk Info ====================

    /**
     * Получает общую информацию о Диске с валидным токеном.
     *
     * @return ответ с ожидаемым статусом 200
     */
    public static Response getDiskInfo() {
        return getDiskInfo(RequestSpec.getSpec());
    }

    /**
     * Получает общую информацию о Диске с указанной спецификацией.
     *
     * @param spec спецификация запроса (для проверки 401 ошибок)
     * @return ответ сервера
     */
    public static Response getDiskInfo(RequestSpecification spec) {
        Allure.step("Получить информацию о Диске");
        Response response = given()
                .spec(spec)
                .when()
                .get();

        Allure.addAttachment(RESPONSE_PREFIX + response.statusCode(),
                RESPONSE_ATTACHMENT, response.asPrettyString());

        return response;
    }

    /**
     * Получает информацию о Диске с ограниченным набором полей.
     *
     * @param fields список запрашиваемых полей через запятую
     * @return ответ с запрошенными полями
     */
    public static Response getDiskInfoWithFields(String fields) {
        Allure.step("Получить информацию о Диске (fields: " + fields + ")");
        Response response = given()
                .spec(RequestSpec.getSpec())
                .queryParam(FIELDS_PARAM, fields)
                .when()
                .get();

        Allure.addAttachment(RESPONSE_PREFIX + response.statusCode(),
                RESPONSE_ATTACHMENT, response.asPrettyString());

        return response;
    }
}