# Лабораторная работа №4

Лабораторная по сути состоит из 4 частей. 

1 - Знакомство с ZooKeeper, где мы работаем из консоли - она будет преставленна в папке task1 в README.

2 - Написание приложения с животными. Оно находится по пути src/main/java/zoo

3 - Решение проблемы философов - src/main/java/Ph

4 - 2-х фазный коммит, его я в отличи от всех остальных пунктов писала на python, так как, во-первых, узнала что есть библиотека kazoo, которая позволяет работать с zookeeper в python, а, во-вторых, с Java всё было немного сложновато. Это файл Lab4.py, его описание будет здесь. 

#### Lab4, task4

Создаётся наблюдатель и несколько клиентов. 

Каждый клиент подаёт один из двух запросов. Наблюдать ждёт когда все проголосуют, считает голоса и после этого присваевает каждому клиенту тот результат, который был принят большинством
