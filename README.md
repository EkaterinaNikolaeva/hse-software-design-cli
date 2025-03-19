# hse-software-design-cli

## Описание репозитория

Данный репозиторий содержит интерпретатор командной строки. Язык реализации - Java.

## Возможности

1. Простые операции

* cat [FILE]

* echo

* wc [FILE]

* pwd

* exit

2. Поддержка одинарный и двойных кавычек

3. Окружение

4. Подпрограммы

5. Оператор "|"

## Установка и запуск

1. Склонируйте репозиторий

```
git clone https://github.com/EkaterinaNikolaeva/hse-software-design-cli.git
```

2. Перейдите в директорию проекта

```
cd hse-software-design-cli
```
3. Запустите тесты

```
./gradlew test
```
4. Запустите интерпретатор командной строки

```
./gradlew run --console=plain
```
