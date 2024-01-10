Задания:
1. Выводить персонализированную ошибку пользователю; учитывать входные данные запроса
  Реализовано несколькими способами:
    - Around advice - ControllerExceptionAspect#handleException;
    - Получение даннных из пути запроса - StudentController#handleConstraintViolationExceptionByPath
    - Кэширование запроса с помощью ContentCachingRequestWrapper - StudentController#handleConstraintViolationException
 Проверяется тестом PreExApplicationTests#apiUpdateStudentTestUniqueError.
3. Тест кейсы контроллера, негативные тесты 4xx/5xx ошибок
  Все тесты, покрывающие контроллер приведены в PreExApplicationTests#controllerTest;
  4xx коды используем когда запрос выполнить нельзя, 5xx - когда запрос выполнить можно, но возникла непредвиденная ошибка.
4. Проверить дефолтное и переопределенное поведение проксирования с помощью CGLIB/JDK
   Можем использоватьspring.aop.proxy-target-class, чтобы принудить spring использовать CGLIB или JDK;
   Тест представлен в PreExApplicationTests#contextLoads.
