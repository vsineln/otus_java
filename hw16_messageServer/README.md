# MessageServer
  Cервер из предыдущего ДЗ про MessageSystem разделить на три приложения:
  - MessageServer
  - Frontend
  - DBServer
  
  Сделать MessageServer сокет-сервером, Frontend и DBServer клиентами.
  Пересылать сообщения с Frontend на DBService через MessageServer.
  Запустить приложение с двумя Frontend и двумя DBService (но на одной базе
  данных) на разных портах.
  Frontend и DBService запускать "руками".
  По желанию, можно сделать запуск Frontend и DBServer из MessageServer.
  Такой запуск должен быть "отчуждаемый", т.е. "сборка" должна запускаться на
  другом компьютере без особых хлопот.
  
  
**Конфигурация портов, которая используется в решении:**
  ```  
  websocket(8090) --> frontend1(9009)                          database1(8083)
                                     --> MessageServer(8081)-->    
  websocket(8091) --> frontend2(9010)                          database2(8084) 
``` 
**Запуск сервисов из корня проекта**
- java -jar hw16_frontend/target/feService.jar (**frontend1**)
- java -jar hw16_frontend/target/feService.jar --spring.profiles.active=fe2 (**frontend2**)

- java -jar hw16_dbServer/target/dbService.jar (**database1**)
- java -jar hw16_dbServer/target/dbService.jar --spring.profiles.active=db2 (**database2**)

- java -jar hw16_messageServer/target/msServer.jar (**message server**)

**Сохранение пользователей**
- http://localhost:8090/user/save
- http://localhost:8091/user/save