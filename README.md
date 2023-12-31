# java-explore-with-me
java-explore-with-me
Сервис-афиша для публикации событий и поиска участников.
Свободное время — ценный ресурс.

Приложение состоит из двух микросервисов:
ewm-service и stats-service 

ewn-service - основной сервис:
Имеет API с тремя уровнями доступа: PUBLIC/PRIVATE/ADMIN

PUBLIC:

Получение событий, подборок событий и категорий по id;
Получение списка всех категорий и всех подборок событий;
Поиск событий по нескольким параметрам(текст, дата, категория, различные флаги: оплата, наличие доступных мест);
Через публичный API пользователь видит не полную информацию о событии, скрывается описание, дата публикации, локация и лимит участников.

PRIVATE:

Создание и редактирование события, комментария к событию или запроса на участие;
Получение списка всех событий и запросов на участие, созданных пользователем;
Получение событий по id;
Отмена события;
Получение списка всех запросов на участие в событии, опубликованном пользователем, их подтверждение и отмена;
Отмена запроса на участие;

ADMIN:

Создание, обновление и удаление категорий;
Создание подборок, их редактирование(добавление/удаление событий из подборок), удаление, возможность закрепление подборки на главной странице;
Поиск событий по нескольким параметрам(список id инициаторов события, статусов, категорий, дате события), постраничная выдача;
Публикация или отмена события, внесение изменений в событие;

stats-service - сервис статистики:
Собирает информацию с публичного API ewm-service собирает информацию о количестве обращений пользователей к спискам событий и о количестве запросов к подробной информации о событии. Формирует статистику.

Реализована дополнительная функциональность: возможность пользователям оставлять комментарии к событиям:

PUBLIC:

Получение комментариев к событию по id.

PRIVATE:

Добавление/обновление/удаление пользователем комментария;
Получение комментариев пользователя по id.

ADMIN:

Удаление комментариев;
Поучение комментариев по времени.

Ссылка на PR:https://github.com/LipatovKir/java-explore-with-me/pull/7#issue-1893183627

В работе использовано:
Java
Spring Boot
Hibernate
Lombok
PostgreSQL
Docker