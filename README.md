# Chatter

![Static Badge](https://img.shields.io/badge/shirotame-Chatter-Server)
![GitHub top language](https://img.shields.io/github/languages/top/shirotame/Chatter-Server)

Chatter - приложение, реализуещее простейший способ связи между удаленными устройствами. Состоит из 3 элементов: [Chatter-Server](https://github.com/shirotame/Chatter-Server), [Chatter-Desktop](https://github.com/shirotame/Chatter-Desktop) и [Chatter-Android](https://github.com/shirotame/Chatter-Android)

## Как реализовано?

Сделано на java.net.Socket, весь основной код находится в исходниках проекта. В целом, сервер ожидает подключения, ожидает условный заголовок подключение (в данном случае, символ) и затем, если всё успешно, добавляет соединение в локальный List.

## Возможности

Запуск сервера, передача данных между пользователями, просмотр информации, которая передаётся, возможность узнать страну, из которой идет подключение (с помощью бд GeoLite).

## Как запустить?

Чтобы запустить сервер Chatter необходимо иметь способ связи с устройствами, которые будут подключаться (статический IP, локальный IP (для локальной сети) и т.п.). Затем, нужно указать порт в App.java и запустить проект любым доступным способом.
