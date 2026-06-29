# Lovisual Client — Архитектура

**Стек:** Fabric 26.2 (Yarn mappings), Fabric Loom, Mixin, Java 21+
**Цель:** модульный чит-клиент с чистой архитектурой, переиспользуемыми компонентами и производительным рендером.

---

## 1. Общая структура проекта

```
Lovisual/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── src/main/
│   ├── java/ru/lovisual/
│   │   ├── Lovisual.java                  # ModInitializer, точка входа
│   │   ├── LovisualClient.java             # клиентская инициализация
│   │   │
│   │   ├── core/                           # ядро
│   │   │   ├── event/
│   │   │   │   ├── EventBus.java           # шина (Orbit)
│   │   │   │   ├── Event.java              # базовое событие
│   │   │   │   ├── CancellableEvent.java   # отменяемое событие
│   │   │   │   └── impl/                   # все события (packet, render, tick, key...)
│   │   │   ├── manager/
│   │   │   │   ├── ModuleManager.java
│   │   │   │   ├── CommandManager.java
│   │   │   │   ├── ConfigManager.java
│   │   │   │   ├── FriendManager.java
│   │   │   │   └── NotificationManager.java
│   │   │   └── managers/
│   │   │       └── Managers.java           # реестр всех менеджеров (синглтон)
│   │   │
│   │   ├── module/                         # модульная система
│   │   │   ├── Module.java                 # абстрактный базовый класс
│   │   │   ├── ModuleInfo.java             # аннотация (name, desc, category, key)
│   │   │   ├── ModuleCategory.java         # enum (COMBAT, MOVEMENT, RENDER, PLAYER, MISC)
│   │   │   ├── ModuleManager.java          # регистрация, поиск, lifecycle
│   │   │   └── impl/                       # реализации модулей
│   │   │       ├── combat/
│   │   │       │   ├── KillAura.java
│   │   │       │   ├── AutoClicker.java
│   │   │       │   └── Velocity.java
│   │   │       ├── movement/
│   │   │       │   ├── Sprint.java
│   │   │       │   ├── Speed.java
│   │   │       │   ├── Flight.java
│   │   │       │   └── NoFall.java
│   │   │       ├── render/
│   │   │       │   ├── FullBright.java
│   │   │       │   ├── ESP.java
│   │   │       │   ├── Chams.java
│   │   │       │   └── ClickGUI.java
│   │   │       ├── player/
│   │   │       │   ├── Scaffold.java
│   │   │       │   ├── NoSlow.java
│   │   │       │   └── AntiVoid.java
│   │   │       └── misc/
│   │   │           ├── StaffDetector.java
│   │   │           └── Disconnecter.java
│   │   │
│   │   ├── setting/                        # система настроек
│   │   │   ├── Setting.java                # generic базовый класс
│   │   │   ├── SettingGroup.java           # группа настроек (для GUI)
│   │   │   └── impl/
│   │   │       ├── BooleanSetting.java
│   │   │       ├── SliderSetting.java
│   │   │       ├── ModeSetting.java
│   │   │       ├── ColorSetting.java
│   │   │       ├── BindSetting.java
│   │   │       └── TextSetting.java
│   │   │
│   │   ├── gui/                            # графический интерфейс
│   │   │   ├── clickgui/
│   │   │   │   ├── ClickGuiScreen.java     # Screen с табами/панелями
│   │   │   │   ├── CategoryPanel.java      # панель категории
│   │   │   │   ├── ModuleButton.java       # кнопка модуля с анимацией
│   │   │   │   └── SettingsPanel.java      # панель настроек
│   │   │   ├── hud/
│   │   │   │   ├── HudElement.java         # абстрактный HUD-элемент
│   │   │   │   ├── DraggableHudElement.java # с драгом и снаппингом
│   │   │   │   ├── HudSnapGrid.java        # привязка к сетке
│   │   │   │   └── impl/
│   │   │   │       ├── Watermark.java
│   │   │   │       ├── ArrayList.java
│   │   │   │       ├── TargetHud.java
│   │   │   │       ├── Potions.java
│   │   │   │       ├── Keystrokes.java
│   │   │   │       ├── Coords.java
│   │   │   │       └── Notifications.java
│   │   │   └── components/
│   │   │       ├── ColorPicker.java
│   │   │       ├── Popup.java
│   │   │       ├── SearchBar.java
│   │   │       └── Tooltip.java
│   │   │
│   │   ├── render/                         # рендеринг
│   │   │   ├── Renderer2D.java             # 2D контекст (с батчингом)
│   │   │   ├── Renderer3D.java             # 3D контекст
│   │   │   ├── MSDFFont.java               # MSDF-шрифты
│   │   │   ├── FontRegistry.java           # реестр шрифтов
│   │   │   ├── shader/
│   │   │   │   ├── GlProgram.java          # загрузка шейдеров
│   │   │   │   ├── BlurProgram.java
│   │   │   │   └── RoundedRectProgram.java
│   │   │   └── animation/
│   │   │       ├── Animation.java          # easing-анимации
│   │   │       ├── Easing.java
│   │   │       └── SpringAnimator.java
│   │   │
│   │   ├── command/                        # командная система
│   │   │   ├── CommandManager.java
│   │   │   ├── Command.java                # абстрактная команда
│   │   │   └── impl/
│   │   │       ├── BindCommand.java
│   │   │       ├── ToggleCommand.java
│   │   │       ├── ConfigCommand.java
│   │   │       ├── FriendCommand.java
│   │   │       └── HelpCommand.java
│   │   │
│   │   ├── mixin/                          # миксины (перехватчики)
│   │   │   ├── client/
│   │   │   │   ├── ClientConnectionMixin.java     # пакеты
│   │   │   │   ├── MinecraftClientMixin.java      # главный цикл
│   │   │   │   └── KeyboardMixin.java             # ввод
│   │   │   ├── render/
│   │   │   │   ├── InGameHudMixin.java            # HUD рендер
│   │   │   │   ├── GameRendererMixin.java         # 3D рендер
│   │   │   │   ├── WorldRendererMixin.java        # блок/entity рендер
│   │   │   │   └── EntityRenderDispatcherMixin.java
│   │   │   ├── entity/
│   │   │   │   ├── ClientPlayerEntityMixin.java   # движение
│   │   │   │   └── LivingEntityMixin.java
│   │   │   └── network/
│   │   │       └── ClientPlayNetworkHandlerMixin.java
│   │   │
│   │   ├── config/                         # конфиги
│   │   │   ├── ConfigManager.java
│   │   │   ├── ConfigProfile.java
│   │   │   └── ConfigEntry.java
│   │   │
│   │   ├── theme/                          # система тем
│   │   │   ├── ThemeManager.java
│   │   │   ├── Theme.java
│   │   │   ├── ColorPalette.java
│   │   │   └── presets/
│   │   │       ├── DarkTheme.java
│   │   │       └── LightTheme.java
│   │   │
│   │   └── util/                           # утилиты
│   │       ├── render/
│   │       │   ├── ColorUtil.java
│   │       │   ├── GlUtil.java
│   │       │   └── MatrixUtil.java
│   │       ├── math/
│   │       │   ├── MathUtil.java
│   │       │   ├── Vec2f.java
│   │       │   └── Vec3d.java
│   │       ├── network/
│   │       │   ├── PacketUtil.java
│   │       │   └── RotationUtil.java
│   │       ├── player/
│   │       │   ├── InventoryUtil.java
│   │       │   └── MovementUtil.java
│   │       └── world/
│   │           ├── BlockUtil.java
│   │           └── WorldUtil.java
│   │
│   └── resources/
│       ├── fabric.mod.json
│       ├── assets/lovisual/
│       │   └── shaders/
│       │       ├── blur.json
│       │       ├── blur.fsh
│       │       └── rounded_rect.fsh
│       └── mixins.lovisual.json
```

---

## 2. Модульная система

### 2.1 Базовый класс Module

```java
@ModuleInfo(name = "KillAura", desc = "Automatic attack", category = Category.COMBAT, key = KEY_R)
public class KillAura extends Module {

    private final BooleanSetting players = BooleanSetting.of("Players", true);
    private final SliderSetting range = SliderSetting.of("Range", 3.0, 1.0, 6.0);
    private final ModeSetting mode = ModeSetting.of("Mode", "Single", "Single", "Switch", "Multi");

    @Override
    public void onEnable() {
        // подписка на события
    }

    @Override
    public void onDisable() {
        // отписка
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPacket(PacketEvent event) {
        // обработка
    }
}
```

- **Настройки** — объявляются как поля, подхватываются рефлексией автоматически (как в Pulse)
- **@ModuleInfo** — декларативные метаданные (как в Rockstar/shade)
- **Lifecycle**: `onEnable()` → подписка на события, `onDisable()` → отписка
- **ToggleMode**: `TOGGLE` / `HOLD`

### 2.2 ModuleManager

```java
public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<>();

    public void register(Module module) { ... }
    public void init() { /* рефлексивно сканирует пакет impl/ */ }
    public <T extends Module> T get(Class<T> clazz) { ... }
    public Module get(String name) { ... }
    public List<Module> getByCategory(Category cat) { ... }
    public void handleKey(int key) { /* триггерит бинды */ }
}
```

Регистрация модулей — **package scan** через ClassGraph или аннотации, вместо ручного списка. Опционально — ручной список для контроля порядка.

---

## 3. Event System

**Orbit** (MeteorDevelopment) — лёгкая, с приоритетами, отменой цепочки:

```java
// подписка
eventBus.subscribe(this);

// событие
public class PacketEvent extends CancellableEvent {
    private final Packet<?> packet;
    private final PacketState state; // SEND / RECEIVE
}

// слушатель с приоритетом
@EventHandler(priority = EventPriority.HIGHEST)
public void onPacket(PacketEvent event) {
    if (event.getPacket() instanceof PlayerMoveC2SPacket) {
        event.cancel();
    }
}
```

Или **field-based listeners** как в Rockstar (без аннотаций, через лямбды):

```java
private final EventListener<PacketEvent> packetListener =
    EventListener.of(PacketEvent.class, EventPriority.HIGH, event -> {
        // обработка
    });
```

Первый вариант проще, второй — типобезопаснее. Выбор за тобой, я бы взял Orbit.

---

## 4. GUI

### 4.1 ClickGUI
- `ClickGuiScreen extends Screen` — полноценный экран с табами (как Pulse)
- Категории — вертикальные панели слева
- Модули — плиточная сетка (Masonry как в shade)
- Настройки — правая панель (или popup как в 4E)
- Анимации: spring-физика для открытия/закрытия (как 4E)

### 4.2 HUD
- `HudElement` — абстрактный класс с позицией, размером, драгом (как Pulse/Rockstar)
- `DraggableHudElement` — наследует HudElement, добавляет перетаскивание и **snap-to-grid** (как Rockstar)
- Элементы рендерятся в `EventRender2D` через miхин `InGameHud`
- Позиции сохраняются в конфиг (`ConfigManager`)

---

## 5. Рендер

- **Renderer2D** — обёртка над `DrawContext` с методами: `fill`, `gradient`, `roundRect`, `blur`, `text` (как `CustomDrawContext` в Rockstar)
- **MSDF-шрифты** — `MSDFFont` + `FontRegistry` (как в shade/Rockstar)
- **Батчинг** — группировка draw-вызовов в один шейдер (как `RectBatching`/`FontBatching` в Rockstar)
- **Анимации** — `Animation` с easing-функциями (EaseInOutCubic, Spring), встроенные в Module и HudElement

---

## 6. Event Hierarchy (все события)

```
core/event/impl/
├── game/
│   ├── TickEvent.java
│   ├── WorldChangeEvent.java
│   ├── GameJoinEvent.java
│   └── GameLeaveEvent.java
├── player/
│   ├── MotionEvent.java
│   ├── MoveEvent.java
│   ├── JumpEvent.java
│   ├── StepEvent.java
│   └── SlowdownEvent.java
├── render/
│   ├── Render2DEvent.java
│   ├── Render3DEvent.java
│   ├── FogEvent.java
│   ├── HurtCamEvent.java
│   └── CrosshairEvent.java
├── network/
│   ├── PacketSendEvent.java
│   ├── PacketReceiveEvent.java
│   └── ServerConnectionEvent.java
├── input/
│   ├── KeyPressEvent.java
│   ├── MouseClickEvent.java
│   └── MouseScrollEvent.java
├── entity/
│   ├── AttackEntityEvent.java
│   ├── EntitySpawnEvent.java
│   └── RenderEntityEvent.java
└── world/
    ├── BlockCollisionEvent.java
    └── LiquidCollisionEvent.java
```

---

## 7. Конфиги

- **Формат**: JSON (Gson)
- **Профили**: несколько конфиг-профилей (как в Pulse/shade)
- **Автосохранение**: при выключении модуля, или по таймеру раз в 30 сек
- **Структура**: `configs/` в директории клиента

```json
{
  "modules": {
    "KillAura": {
      "enabled": true,
      "key": -1,
      "settings": {
        "Range": 3.5,
        "Mode": "Switch",
        "Players": true
      }
    }
  },
  "hud": {
    "ArrayList": { "x": 2, "y": 10, "enabled": true },
    "TargetHud": { "x": 100, "y": 50, "enabled": false }
  },
  "theme": "dark"
}
```

---

## 8. Миксины — точка входа событий в рендер/игру

Все миксины **только для хуков** — не меняют логику, только вставляют Event.call():

```java
// InGameHudMixin — вставка Render2DEvent
@Inject(at = @At("HEAD"), method = "render")
public void onRender(DrawContext ctx, RenderTickCounter ticker, CallbackInfo ci) {
    EventBus.post(new Render2DEvent(ctx, ticker));
}

// ClientConnectionMixin — вставка PacketEvent
@Inject(at = @At("HEAD"), method = "send", cancellable = true)
public void onSend(Packet<?> packet, CallbackInfo ci) {
    if (EventBus.post(new PacketSendEvent(packet)).isCancelled()) ci.cancel();
}
```

Никакого прямого изменения поведения в миксинах — только вызов событий.

---

## 9. Theme System

- `Theme` — хранит `ColorPalette` (акцент, фон, текст, модуль)
- `ThemeManager` — переключение между тёмной/светлой/кастомной
- Цвета с raibow/gradient опциями (для ArrayList)

---

## 10. Зависимости (build.gradle)

```gradle
dependencies {
    // fabric
    modImplementation "net.fabricmc:fabric-loader:0.16.0"
    modImplementation "net.fabricmc.fabric-api:fabric-api:0.100.0+1.21.4"

    // event bus
    implementation "net.meteordev:orbit:0.3.0"

    // config (json)
    implementation "com.google.code.gson:gson:2.11.0"
}
```

---

## Итог: откуда взяты паттерны

| Компонент | Влияние |
|-----------|---------|
| @ModuleInfo + авто-сбор настроек | Rockstar, Pulse |
| Orbit EventBus с приоритетами | Pulse |
| CustomDrawContext (wrapper) | Rockstar |
| Masonry-сетка GUI | shade.xyz |
| MSDF-шрифты | shade, Rockstar |
| Snap-to-grid HUD | Rockstar |
| Spring-анимации | 4E Client |
| CopyOnWriteArrayList для модулей | Skycore, code-main |
| Field-based listeners (альтернатива) | Rockstar |
| Разделение enabled/subscribed | Pulse |
| Батчинг рендера | Rockstar |
| JSON конфиги с профилями | Pulse, shade |
