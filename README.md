# Дипломный проект профессии «Тестировщик»
Дипломный проект представляет собой автоматизацию тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка.
## Задача
Автоматизировать сценарии (как позитивные, так и негативные) покупки тура.

Задача разложена на 4 этапа:
1. Планировании автоматизации тестирования - [План](https://github.com/12ok/aqa--diploma/blob/master/documents/Plan.md)
1. Непосредственно самой автоматизации
1. Подготовке отчётных документов по итогам автоматизированного тестирования
1. Подготовка отчётных документов по итогам автоматизации

## Инструкция по запуску
* склонировать репозиторий `git clone https://github.com/12ok/aqa--diploma`
* перейти в директорию `gate-simulator`
* собрать образ командой `docker image build -t node-app:1.0 .`
* вернуться в директорию проекта
* запустить docker container `docker-compose up -d`
* запустить приложение `java -jar aqa-shop.jar -port=8080`
