# LoVisual — TODO

## Сборка
- [x] build.gradle.kts (Fabric + Loom)
- [x] settings.gradle.kts
- [x] gradle.properties
- [x] fabric.mod.json
- [x] mixins config
- [x] access widener

## Архитектура
- [x] DESIGN.md — полный дизайн
- [ ] EventBus (Orbit)
- [ ] Module system (Module, ModuleManager, ModuleInfo)
- [ ] Setting system (Boolean, Slider, Mode, Color)
- [ ] Config system (JSON profiles)
- [ ] Command system
- [ ] Managers class

## GUI
- [ ] ClickGUI — Grid/Masonry стиль
  - [ ] Категории чипсами
  - [ ] Сетка модулей
  - [ ] Поиск
  - [ ] Popup настроек
  - [ ] Анимации
- [ ] HUD
  - [ ] HudElement base
  - [ ] ArrayList
  - [ ] Watermark
  - [ ] TargetHUD
  - [ ] ArmorHUD
  - [ ] PotionsHUD
  - [ ] Keystrokes
  - [ ] Scoreboard
  - [ ] ComboCounter
  - [ ] Notifications

## Combat
- [ ] AutoClicker

## Movement
- [ ] Sprint

## Render
- [ ] FullBright
- [ ] PVision (Box/Glow/Outline, только видимые игроки)
- [ ] Nametags
- [ ] HealthTags
- [ ] NoHurtCam
- [ ] NoWeather
- [ ] Animations
- [ ] HitParticles
- [ ] Music
- [ ] SkyBox
- [ ] CustomGlint

## Player
- [ ] AutoTool
- [ ] ExpThrower
- [ ] InvManager
- [ ] MiddleClickFriend
- [ ] InventorySort
- [ ] Refill
- [ ] NoInteract

## Social
- [ ] Friends (+ Teams)
- [ ] IRC
- [ ] Spammer
- [ ] AntiAFK
- [ ] AutoRespawn
- [ ] AutoReconnect
- [ ] NameProtect

## Config
- [ ] Configs (profiles)
- [ ] Theme (dark/light/custom)

## Миксины
- [ ] InGameHud (Render2D)
- [ ] GameRenderer (Render3D)
- [ ] ClientConnection (packets)
- [ ] ClientPlayerEntity (movement/tick)
- [ ] Keyboard (key input)

## Рендер
- [ ] Renderer2D (batch, roundRect, gradient, blur)
- [ ] MSDF fonts
- [ ] Animation system (easing, spring)
- [ ] Shader programs (blur, roundRect)

## ref/modules/
- [ ] Extract all module files from clients
- [ ] Organize by module name
- [ ] Rename: `module_client.java`
