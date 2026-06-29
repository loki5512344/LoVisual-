# LoVisual Mod

Fabric 1.21.4 utility client.

## Build

```bash
./gradlew build
```

Output: `build/libs/lovisual-0.1.0.jar`

## Requirements

- Java 21+
- Fabric Loader 0.16.12
- Fabric API 0.119.3+1.21.4

## Features

- Custom MainMenu with animated gradient + AltManager
- ClickGUI (Grid/Masonry) with search, category chips, settings popup
- HUD elements (10) with style system (Custom/Pulse/Rockstar/Skycore/Shade/4E)
- Theme system with customizable colors + gradient via GUI
- Module system (18 modules): Sprint, FullBright, ClickGUI, NoHurtCam, NoWeather, AutoTool, AntiAFK, etc.
- Commands: `/Lovisual toggle`, `bind`, `config`, `help`
- Cloud config sharing via backend
- Config format: `.loconf` with profiles
