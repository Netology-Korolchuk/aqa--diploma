# Дипломный проект профессии «Тестировщик»
Дипломный проект представляет собой автоматизацию тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка.
## Задача
Автоматизировать сценарии (как позитивные, так и негативные) покупки тура.

Задача разложена на 4 этапа:
1. Планировании автоматизации тестирования - [План](https://github.com/12ok/aqa--diploma/blob/master/documents/Plan.md)
1. Непосредственно самой автоматизации
1. Подготовке отчётных документов по итогам автоматизированного тестирования
    * [Отчет по итогам автоматизации](https://github.com/12ok/aqa--diploma/blob/master/documents/Report.md)
    * [Найденные баги](https://github.com/12ok/aqa--diploma/issues)
1. Подготовка отчётных документов по итогам автоматизации - [Отчет по итогам работы](https://github.com/12ok/aqa--diploma/blob/master/documents/Summary.md)



## Инструкция по запуску
**Для запуска необходим Docker или Docker Toolbox**.
В `application.properties` и `build.gradle` указаны настройки для **Docker Toolbox**.
Если используется **Docker** необходимо изменить `192.168.99.100` на `localhost`

* склонировать репозиторий `git clone https://github.com/12ok/aqa--diploma`
* запустить docker container `docker-compose up -d` Дождаться пока контейнеры запустятся
### Для запуска приложения с MySQL
* запустить приложение `java -Dspring.datasource.url=jdbc:mysql://192.168.99.100:3306/app -jar ./artifacts/aqa-shop.jar`. 
* выполнить команду для запуска тестов `./gradlew clean test` (для Linux), `./gradlew.bat clean test` (для Windows) 
* результаты выполнения тестов находятся в директории `build/allure-results`
* выполнить команду для формирования отчета `gradlew allureReport`
* отчет о выполнении тестов находится в директории `build/reports/allure-report/`
* выполнить команду для открытия отчета в браузере `gradlew allureServe`

### Для запуска приложения с PostgreSQL
* запустить приложение `java -Dspring.datasource.url=jdbc:postgresql://192.168.99.100:5432/app -jar artifacts/aqa-shop.jar`
* выполнить команду для запуска тестов `./gradlew clean test -Ddb.url=jdbc:postgresql://192.168.99.100:5432/app` (для Linux), `./gradlew.bat clean test -Ddb.url=jdbc:postgresql://192.168.99.100:5432/app` (для Windows) 
* результаты выполнения тестов находятся в директории `build/allure-results`
* выполнить команду для формирования отчета `gradlew allureReport`
* отчет о выполнении тестов находится в директории `build/reports/allure-report/`
* выполнить команду для открытия отчета в браузере `gradlew allureServe`

* после окончания тестов остановить контейнеры `docker-compose down`





