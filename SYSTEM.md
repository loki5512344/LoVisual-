# LoVisual — Система

## 1. Модульная система

### Базовый класс Module

Каждый модуль наследуется от `Module` и аннотируется `@ModuleInfo`:

```java
@ModuleInfo(name = "Sprint", desc = "Auto-sprint", category = ModuleCategory.MOVEMENT, key = KEY_CONTROL)
public class Sprint extends Module {
    private final BooleanSetting toggle = new BooleanSetting("Toggle", true);

    @Override
    protected void onEnable() { /* подписка на события */ }
    @Override
    protected void onDisable() { /* отписка */ }
}
```

### Жизненный цикл

```
toggle()
  ├── enable()
  │     ├── EventBus.register(this)     ← авто-подписка @EventHandler
  │     └── onEnable()                  ← переопределяемый хук
  └── disable()
        ├── EventBus.unregister(this)   ← авто-отписка
        └── onDisable()                 ← переопределяемый хук
```

- `toggle()` — переключает состояние, авто-сохраняет конфиг
- `enable()` / `disable()` — вкл/выкл с авто-регистрацией событий
- При старте все модули выключены, включаются через `/Lovisual toggle` или GUI

### Категории

`COMBAT`, `MOVEMENT`, `RENDER`, `PLAYER`, `MISC`

---

## 2. Система настроек (Settings)

### Авто-сбор через рефлексию

Поля-настройки объявляются как `final` поля в классе модуля. Базовый класс `Module` в конструкторе сканирует их рефлексией и собирает в `List<Setting<?>>`:

```java
public class Example extends Module {
    private final BooleanSetting bool = new BooleanSetting("Name", true);
    private final SliderSetting slider = new SliderSetting("Range", 3.0, 1.0, 6.0, 0.1);
    private final ModeSetting mode = new ModeSetting("Mode", "A", "A", "B", "C");
    private final ColorSetting color = new ColorSetting("Color", new Color(255, 80, 80));
    // BindSetting — клавиша модуля хранится в Module.key, не в отдельном Setting
}
```

### Типы

| Тип | Хранит | Сериализация |
|-----|--------|-------------|
| `BooleanSetting` | `Boolean` | `"true"` / `"false"` |
| `SliderSetting` | `Double` | `"3.5"` |
| `ModeSetting` | `String` | `"ModeName"` |
| `ColorSetting` | `Color` | `"#ff5050 solid"` / `"#ff5050 rainbow"` |

### Доступ из GUI

Настройки автоматически отображаются в ClickGUI при ПКМ по модулю — слайдеры, переключатели, цикл режимов.

---

## 3. EventBus

### Регистрация

При `enable()` модуля EventBus сканирует методы с аннотацией `@EventHandler` и подписывает их на соответствующие события:

```java
@EventHandler
private void onTick(TickEvent event) {
    /* вызывается каждый тик */
}

@EventHandler
private void onRender(Render2DEvent event) { }

@EventHandler
private void onRender3D(Render3DEvent event) { }
```

### События

| Событие | Когда |
|---------|-------|
| `TickEvent` | каждый игровой тик |
| `Render2DEvent` | рендер HUD (DrawContext) |
| `Render3DEvent` | рендер мира (MatrixStack, Camera) |
| `PacketEvent` | отправка/получение пакета |
| `KeyPressEvent` | нажатие клавиши |
| `MouseClickEvent` | клик мыши |
| `MoveEvent` | движение игрока |

---

## 4. Конфиги (.loconf)

### Формат

```
# LoVisual config

[theme]
background = #1a1a2e
accent = #dc1c1c
panelBg = #0d0d1a
panelBorder = #1e1e3a
panelHover = #2a2a4a
textPrimary = #cccccc
textSecondary = #888888
hudBg = #333355
gradientStart = #1a1a2e
gradientEnd = #0d0d1a
gradientDir = Vertical

Sprint {
  enabled = true
  key = 29
  Toggle = true
}

ClickGUI {
  enabled = false
  key = 344
}
```

### Команды

- `/Lovisual config save` — сохранить текущий профиль
- `/Lovisual config load` — загрузить
- `/Lovisual config profile <name>` — переключить профиль
- `/Lovisual config list` — список профилей
- `/Lovisual config cloud upload <name>` — загрузить на бэкенд
- `/Lovisual config cloud download <key>` — скачать по ключу

### Профили

Хранятся в `config/lovisual/*.loconf`. Профиль по умолчанию — `default.loconf`.

### Cloud-расшаривание

- Загрузка: UUID игрока → бэкенд → возвращает share_key (8 hex)
- Скачивание: ключ → любой игрок получает конфиг
- Без авторизации для скачивания
- Максимум 5 конфигов на пользователя

---

## 5. GUI

### MainMenu

Кастомный экран вместо TitleScreen (через миксин):
- Анимированный градиентный фон (волна)
- 50 плавающих частиц
- Заголовок "LoVisual" + версия
- Кнопки: Singleplayer, Multiplayer, Alt Manager, Options, Quit
- Справа внизу: "Logged in as: <username>"

### AltManager

Экран управления аккаунтами:
- Список из `config/lovisual/accounts.txt`
- Add — текстовое поле, Enter — добавить
- Login — записать в last_account.txt
- Remove — удалить из списка
- Данные читаются из кроссплатформенного конфиг-директории

### ClickGUI

- Чипсы категорий (все категории + Theme)
- Поисковая строка
- Сетка модулей (Grid/Masonry)
- ЛКМ — toggle модуля
- ПКМ — открыть настройки
- СКМ — привязать клавишу
- Скролл по списку
- Панель Theme (7 цветов × R/G/B слайдеры + направление градиента)

### HUD

10 элементов, перетаскиваемых мышью:
- Watermark, ModuleList, TargetHUD, ArmorHUD, PotionsHUD, Keystrokes, Scoreboard, Coords, ComboCounter, Notifications
- Каждый элемент имеет `Style` (Custom/Pulse/Rockstar/Skycore/Shade/4E)
- Цвета берутся из ThemeManager

---

## 6. Тема (Theme)

### Текущая архитектура

`Theme` — mutable POJO с полями `Color`:
- background, accent, panelBg, panelBorder, panelHover
- textPrimary, textSecondary, hudBg
- gradientStart, gradientEnd, gradientDir (Vertical/Horizontal/Diagonal)

`ThemeManager` — синглтон:
- `getCurrent()` — текущий Theme
- Статические шорткаты: `getAccentRGB()`, `getHudBgRGB()`, `getGradientStart()`, etc.
- Все HUD-элементы и ClickGUI читают цвета через ThemeManager

### Настройка

Через ClickGUI → чип "Theme" → слайдеры R/G/B для каждого цвета + выбор направления градиента. Сохраняется в `.loconf`.

---

## 7. Команды

Префикс: `/Lovisual`

| Команда | Описание |
|---------|----------|
| `toggle <module>` | Вкл/Выкл модуль |
| `bind <module> <key>` | Привязать клавишу |
| `help` | Список команд |
| `config <save/load/profile/list>` | Управление конфигами |
| `config cloud upload/download` | Облачные конфиги |
| `irc <message>` | Отправить IRC-сообщение |

---

## 8. IRC

Модуль `IRC` (MISC):
- Подключается к `ws://localhost:8080/ws` при включении
- Получает/отправляет JSON: `WSMessage{type, payload}`, `IRCMessage{from, content}`
- Сообщения показываются через Notification HUD
- Отправка: `/Lovisual irc <text>`

Бэкенд: хаб парсит WSMessage → IRCMessage → проставляет from → рассылает всем.

---

## 9. Auto-updater

- Бэкенд: `GET /version` → `{version, mod_url, changelog}`
- Мод: `Updater.check()` на старте → если версия отличается, пишет в чат
- Лаунчер: проверяет при загрузке → статус "Update available!"

---

## 10. Лаунчер (Rust + egui)

### Экраны

1. **Login/Register** — email + password, регистрация с username
2. **Dashboard** — профиль (nickname, playtime), кнопка Play, Settings, Logout
3. **Settings** — кастомная команда запуска (`{username}` подставляется)

### Запуск Minecraft

Кнопка Play:
1. Сохраняет сессию в `~/.config/lovisual/account.txt`
2. Пытается запустить официальный лаунчер
3. Если не найден — ищет Fabric-профиль в `.minecraft` и запускает напрямую
4. Или использует кастомную команду из настроек

### Сессия

- Сохраняется в кроссплатформенный config dir:
  - Linux: `~/.config/lovisual/account.txt`
  - Windows: `%APPDATA%/lovisual/account.txt`
  - macOS: `~/Library/Application Support/lovisual/account.txt`
- Мод читает тот же файл для отображения "Logged in as"

---

## 11. Бэкенд (Go + chi + SQLite)

### API

| Метод | Путь | Auth | Описание |
|-------|------|------|----------|
| POST | `/auth/register` | ✗ | Регистрация |
| POST | `/auth/login` | ✗ | Логин → token |
| POST | `/auth/logout` | ✓ | Выход |
| GET | `/profile` | ✓ | Профиль |
| PATCH | `/profile` | ✓ | Обновить nickname/avatar |
| POST | `/profile/playtime` | ✓ | Синхронизация времени |
| GET | `/profile/search?q=` | ✗ | Поиск юзеров |
| POST/DELETE | `/profile/friends/{id}` | ✓ | Добавить/удалить друга |
| GET | `/profile/friends` | ✓ | Список друзей |
| POST | `/configs` | ✗ | Загрузить конфиг |
| GET | `/configs/share/{key}` | ✗ | Скачать по ключу |
| DELETE | `/configs/{user}/{name}` | ✗ | Удалить конфиг |
| GET | `/version` | ✗ | Версия для авто-апдейта |
| WS | `/ws` | ✗ | IRC WebSocket |

### Middleware

- CORS — все origins
- RateLimit — 100 запросов/мин
- Auth — Bearer token для защищённых эндпоинтов
- SHA256 хеши паролей

---

## 12. Сборка

```bash
# Мод (Fabric 1.21.4)
cd mod && ./gradlew build
# → build/libs/lovisual-0.1.0.jar

# Бэкенд (Go)
cd backend && go run ./cmd/server
# → :8080

# Лаунчер (Rust)
cd launcher && cargo run
# → egui окно
```
