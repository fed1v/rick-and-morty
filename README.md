# Rick And Morty

**Было реализовано следующее:**
1. Splash Screen с логотипом Rick And Morty
2. BottomNavigationView для навигации по основным экранам
3. Toolbars:
   - На основных экранах содержат кнопки для поиска и фильтров
   - На экранах с деталями содержат кнопку Back

4. Основные экраны (списков персонажей, локаций и эпизодов)
5. Экраны с деталями персонажей, локаций и эпизодов
6. Фильтры
7. Progress бары во время загрузки данных
8. Если по запросу пользователя не было найдено данных, то пользователь уведомляется об этом
9. Получение данных для всех экранов из API https://rickandmortyapi.com/
10. Поиск персонажей, локаций, эпизодов по имени
11. Навигация:
   - Переход на детали элемента после нажатия на этот элемент
   - Обработка системной кнопки Back, а также кнопки Back, расположенной в Тулбарах фрагментов с деталями

Навигация реализована при помощи FragmentManager
