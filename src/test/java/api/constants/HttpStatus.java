package api.constants;

import api.exceptions.UtilityClassException;

/**
 * Константы HTTP-статусов ответа.
 */
public class HttpStatus {

    /**
     * Успешный запрос.
     */
    public static final int OK = 200;

    /**
     * Ресурс успешно создан.
     */
    public static final int CREATED = 201;

    /**
     * Запрос принят, операция выполняется асинхронно.
     */
    public static final int ACCEPTED = 202;

    /**
     * Запрос выполнен успешно, тело ответа пустое.
     */
    public static final int NO_CONTENT = 204;

    /**
     * Некорректные данные в запросе.
     */
    public static final int BAD_REQUEST = 400;

    /**
     * Не авторизован (токен отсутствует или невалидный).
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * Запрошенный ресурс не найден.
     */
    public static final int NOT_FOUND = 404;

    /**
     * Конфликт — ресурс уже существует.
     */
    public static final int CONFLICT = 409;

    private HttpStatus() {
        throw new UtilityClassException(getClass());
    }
}