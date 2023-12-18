Задания:
1. Выводить персонализированную ошибку пользователю; учитывать входные данные запроса
  Реализовано с помощью HttpServletRequestWrapper и Filter;
  Используется в StudentController#studentEmptyDataException, проверяется тестом PreExApplicationTests#apiEmptyDataStudentErrorTest.
2. Тест кейсы контроллера, негативные тесты 4xx/5xx ошибок
  Все тесты, покрывающие контроллер приведены в PreExApplicationTests#controllerTest;
  4xx коды используем когда запрос выполнить нельзя, 5xx - когда запрос выполнить можно, но возникла непредвиденная ошибка.
3. Проверить дефолтное и переопределенное поведение проксирования с помощью CGLIB/JDK
   Можем использовать @EnableAspectJAutoProxy(proxyTargetClass = true), чтобы принудить spring использовать CGLIB, в обратную сторону это не работает, поэтому проверять проксирование будем на сервисе StudentService с использованием CGLIB;
   Проверять JDK проксирование будем на StudentRepository, имплементирующем JpaRepository;
   Тест представлен в PreExApplicationTests#contextLoads.
