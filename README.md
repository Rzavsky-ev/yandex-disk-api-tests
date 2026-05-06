# Яндекс.Диск API — Автотесты

Автоматизированные тесты для проверки REST API Яндекс.Диска с использованием Полигона.

## Стек технологий

- **Java 17**
- **JUnit 5**
- **RestAssured**
- **AssertJ**
- **Jackson**
- **Lombok**
- **Allure Reports**
- **Maven**

## Требования

- Java 17 или выше
- Maven 3.8+

## Быстрый старт

### 1. Клонировать репозиторий

```bash
git clone https://github.com/Rzavsky-ev/yandex-disk-api-tests.git
cd yandex-disk-api-tests
```

### 2. Получить OAuth-токен

Перейти на Полигон Яндекс.Диска, нажать «Получить OAuth-токен» и авторизоваться
под тестовым пользователем Полигона (не личным аккаунтом). Скопировать полученный токен.

### 3. Установить переменную окружения

**Linux / Mac:**

```bash
export YANDEX_DISK_TOKEN="ваш_токен"
```

**Windows (cmd):**

```cmd
set YANDEX_DISK_TOKEN=ваш_токен
```

**Windows (PowerShell):**

```powershell
$env:YANDEX_DISK_TOKEN="ваш_токен"
```

### 4. Запустить тесты

```bash
mvn clean test
```

### 5. Посмотреть отчёт Allure

```bash
mvn allure:serve
```

## Структура проекта

```text
src/
├── test/
│   ├── java/api/
│   │   ├── base/
│   │   │   └── BaseTest.java
│   │   ├── constants/
│   │   │   └── HttpStatus.java
│   │   ├── dto/
│   │   │   ├── ErrorResponse.java
│   │   │   ├── diskInfo/
│   │   │   │   ├── Disk.java
│   │   │   │   ├── SystemFolders.java
│   │   │   │   └── User.java
│   │   │   └── resources/
│   │   │       ├── Embedded.java
│   │   │       ├── Link.java
│   │   │       └── Resource.java
│   │   ├── exceptions/
│   │   │   └── UtilityClassException.java
│   │   ├── specs/
│   │   │   └── RequestSpec.java
│   │   ├── tests/
│   │   │   ├── disk/
│   │   │   │   └── DiskInfoTest.java
│   │   │   └── resources/
│   │   │       ├── CopyResourcesTest.java
│   │   │       ├── DeleteResourcesTest.java
│   │   │       ├── GetResourcesTest.java
│   │   │       ├── PatchResourcesTest.java
│   │   │       └── PutResourcesTest.java
│   │   └── utils/
│   │       ├── ApiClient.java
│   │       └── ResponseValidator.java
│   └── resources/
│       ├── logback-test.xml
│       └── schemas/
│           ├── disk-info.json
│           ├── error.json
│           ├── link.json
│           └── resource.json
```

## Тестовое покрытие

### GET /v1/disk — Информация о Диске

| Тест                                     | Статус | Описание                         |
|------------------------------------------|--------|----------------------------------|
| `testDiskInfoContract`                   | ✅ 200  | Контракт: JSON Schema            |
| `testGetDiskInfo`                        | ✅ 200  | Получение базовой информации     |
| `testGetDiskInfoWithFields`              | ✅ 200  | Работа параметра `fields`        |
| `testGetDiskInfoIgnoresUnknownField`     | ✅ 200  | Игнор неизвестного поля          |
| `testGetDiskInfoWithInvalidFieldsSyntax` | ✅ 200  | Устойчивость к битому синтаксису |
| `testGetDiskInfoUnauthorized`            | ❌ 401  | Запрос без токена                |

### PUT /v1/disk/resources — Создание папки

| Тест                           | Статус | Описание              |
|--------------------------------|--------|-----------------------|
| `testCreateFolderContract`     | ✅ 201  | Контракт: JSON Schema |
| `testCreateFolder`             | ✅ 201  | Создание папки        |
| `testCreateFolderEmptyPath`    | ❌ 400  | Пустой путь           |
| `testCreateFolderUnauthorized` | ❌ 401  | Невалидный токен      |
| `testCreateFolderConflict`     | ❌ 409  | Папка уже существует  |

### GET /v1/disk/resources — Получение метаинформации

| Тест                          | Статус | Описание                     |
|-------------------------------|--------|------------------------------|
| `testGetResourceContract`     | ✅ 200  | Контракт: JSON Schema        |
| `testGetFolderInfo`           | ✅ 200  | Информация о папке           |
| `testGetFolderInfoWithFields` | ✅ 200  | С параметром `fields`        |
| `testGetNonEmptyFolderInfo`   | ✅ 200  | Непустая папка с `_embedded` |
| `testGetFolderEmptyPath`      | ❌ 400  | Пустой путь                  |
| `testGetFolderUnauthorized`   | ❌ 401  | Невалидный токен             |
| `testGetNonExistentFolder`    | ❌ 404  | Папка не существует          |

### POST /v1/disk/resources/copy — Копирование

| Тест                         | Статус    | Описание                     |
|------------------------------|-----------|------------------------------|
| `testCopyContract`           | ✅ 201/202 | Контракт: JSON Schema        |
| `testCopyFolder`             | ✅ 201/202 | Копирование папки            |
| `testCopyNonExistentFolder`  | ❌ 404     | Исходная папка не существует |
| `testCopyFolderConflict`     | ❌ 409     | Целевая папка существует     |
| `testCopyFolderEmptyFrom`    | ❌ 400     | Пустой параметр `from`       |
| `testCopyFolderUnauthorized` | ❌ 401     | Невалидный токен             |

### PATCH /v1/disk/resources — Обновление свойств

| Тест                         | Статус | Описание                     |
|------------------------------|--------|------------------------------|
| `testPatchContract`          | ✅ 200  | Контракт: JSON Schema        |
| `testUpdateCustomProperties` | ✅ 200  | Добавление custom_properties |
| `testPatchEmptyPath`         | ❌ 400  | Пустой путь                  |
| `testPatchUnauthorized`      | ❌ 401  | Невалидный токен             |

### DELETE /v1/disk/resources — Удаление

| Тест                           | Статус    | Описание               |
|--------------------------------|-----------|------------------------|
| `testDeleteFolder`             | ✅ 202/204 | Удаление папки         |
| `testDeleteFolderPermanently`  | ✅ 202/204 | Безвозвратное удаление |
| `testDeleteNonExistentFolder`  | ❌ 404     | Папка не существует    |
| `testDeleteFolderEmptyPath`    | ❌ 400     | Пустой путь            |
| `testDeleteFolderUnauthorized` | ❌ 401     | Невалидный токен       |

## Запуск групп тестов

```bash
# Только smoke-тесты
mvn test -DincludeTags=smoke

# Только regression
mvn test -DincludeTags=regression

# Позитивные тесты
mvn test -DincludeTags=positive

# Негативные тесты
mvn test -DincludeTags=negative

# Контрактные тесты
mvn test -DincludeTags=contract

# Запуск с Allure-отчётом
mvn clean test allure:serve

# Только генерация отчёта (без запуска тестов)
mvn allure:serve
```

## CI/CD

Проект поддерживает запуск в Jenkins (Pipeline). Токен хранится в Jenkins Credentials.

## Автор

Эдуард Ржавский