use std::path::PathBuf;
use std::process::Command;
use std::fs;

pub fn save_session(username: &str) {
    let base = dirs::config_dir().unwrap_or_else(|| PathBuf::from("."));
    let dir = base.join("lovisual");
    fs::create_dir_all(&dir).ok();
    fs::write(dir.join("account.txt"), username).ok();
}

pub fn launch(username: &str, launch_cmd: &str) -> Result<String, String> {
    save_session(username);

    if !launch_cmd.is_empty() {
        let parts: Vec<&str> = launch_cmd.split_whitespace().collect();
        let cmd = parts[0];
        let args: Vec<&str> = parts[1..].iter().map(|s| {
            if *s == "{username}" { username } else { s }
        }).collect();

        let mut child = Command::new(cmd)
            .args(&args)
            .spawn()
            .map_err(|e| format!("Failed to launch: {}", e))?;

        std::thread::spawn(move || {
            let _ = child.wait();
        });

        return Ok("Launched via custom command".into());
    }

    if let Ok(msg) = try_official_launcher() {
        return Ok(msg);
    }

    find_minecraft_dir()
        .and_then(|mc| try_fabric_launch(&mc, username))
        .or_else(|_| {
            Err("Minecraft not found. Install Minecraft Launcher or set a custom launch command.".into())
        })
}

fn try_official_launcher() -> Result<String, String> {
    let launcher = if cfg!(target_os = "linux") {
        "minecraft-launcher"
    } else if cfg!(target_os = "macos") {
        "/Applications/Minecraft.app/Contents/MacOS/launcher"
    } else if cfg!(target_os = "windows") {
        r"minecraft-launcher"
    } else {
        return Err("unsupported OS".into());
    };

    match Command::new(launcher).spawn() {
        Ok(mut child) => {
            std::thread::spawn(move || { let _ = child.wait(); });
            Ok("Launched official Minecraft launcher".into())
        }
        Err(_) => Err("official launcher not found".into()),
    }
}

fn find_minecraft_dir() -> Result<PathBuf, String> {
    if cfg!(target_os = "linux") {
        let home: Result<String, String> = std::env::var("HOME").map_err(|_| "HOME not set".to_string());
            Ok(PathBuf::from(home?).join(".minecraft"))
        } else if cfg!(target_os = "macos") {
            let home: Result<String, String> = std::env::var("HOME").map_err(|_| "HOME not set".to_string());
            Ok(PathBuf::from(home?).join("Library").join("Application Support").join("minecraft"))
        } else if cfg!(target_os = "windows") {
            let appdata: Result<String, String> = std::env::var("APPDATA").map_err(|_| "APPDATA not set".to_string());
            Ok(PathBuf::from(appdata?).join(".minecraft"))
    } else {
        Err("unsupported OS".into())
    }
}

fn try_fabric_launch(mc_dir: &PathBuf, _username: &str) -> Result<String, String> {
    let java = find_java(mc_dir)?;
    let libraries = mc_dir.join("libraries");
    let versions = mc_dir.join("versions");

    let fabric_versions: Vec<_> = fs::read_dir(&versions)
        .map_err(|_| "no versions dir".to_string())?
        .filter_map(|e| e.ok())
        .filter(|e| e.file_name().to_string_lossy().contains("fabric"))
        .collect();

    if fabric_versions.is_empty() {
        return Err("Fabric installation not found".into());
    }

    let ver_name = fabric_versions[0].file_name().to_string_lossy().to_string();
    let ver_dir = versions.join(&ver_name);
    let json_path = ver_dir.join(format!("{}.json", ver_name));

    let json_str = fs::read_to_string(&json_path)
        .map_err(|_| "cannot read version json".to_string())?;
    let json: serde_json::Value = serde_json::from_str(&json_str)
        .map_err(|_| "invalid version json".to_string())?;

    let main_class = json["mainClass"]
        .as_str()
        .unwrap_or("net.fabricmc.loader.impl.launch.knot.KnotClient");

    let mut cp = String::new();
    if let Some(libs) = json["libraries"].as_array() {
        for lib in libs {
            if let Some(dl) = lib["downloads"]["artifact"]["path"].as_str() {
                let path = libraries.join(dl);
                if path.exists() {
                    if !cp.is_empty() { cp.push(std::path::MAIN_SEPARATOR); }
                    cp.push_str(&path.to_string_lossy());
                }
            }
        }
    }

    let mc_jar = ver_dir.join(format!("{}.jar", ver_name));
    if mc_jar.exists() {
        if !cp.is_empty() { cp.push(std::path::MAIN_SEPARATOR); }
        cp.push_str(&mc_jar.to_string_lossy());
    }

    if cp.is_empty() {
        return Err("could not build classpath".into());
    }

    let game_dir_str = mc_dir.to_string_lossy().to_string();
    let assets_dir = mc_dir.join("assets").to_string_lossy().to_string();
    let assets_index = format!("1.21");

    let mut child = Command::new(&java)
        .arg("-cp")
        .arg(&cp)
        .arg(main_class)
        .arg("--gameDir").arg(&game_dir_str)
        .arg("--assetsDir").arg(&assets_dir)
        .arg("--assetIndex").arg(&assets_index)
        .arg("--version").arg(&ver_name)
        .spawn()
        .map_err(|e| format!("Failed to launch: {}", e))?;

    std::thread::spawn(move || {
        let _ = child.wait();
    });

    Ok(format!("Launched {}", ver_name))
}

fn find_java(mc_dir: &PathBuf) -> Result<String, String> {
    let candidates = if cfg!(target_os = "windows") {
        vec![
            mc_dir.join("runtime").join("java-runtime-gamma").join("bin").join("javaw.exe"),
            mc_dir.join("runtime").join("java-runtime-alpha").join("bin").join("javaw.exe"),
        ]
    } else if cfg!(target_os = "macos") {
        vec![
            mc_dir.join("runtime").join("java-runtime-gamma").join("bin").join("java"),
            mc_dir.join("runtime").join("java-runtime-alpha").join("bin").join("java"),
        ]
    } else {
        vec![
            mc_dir.join("runtime").join("java-runtime-gamma").join("bin").join("java"),
            mc_dir.join("runtime").join("java-runtime-alpha").join("bin").join("java"),
        ]
    };

    for c in &candidates {
        if c.exists() {
            return Ok(c.to_string_lossy().to_string());
        }
    }

    if let Ok(path) = std::env::var("JAVA_HOME") {
        let j = PathBuf::from(path).join("bin").join("java");
        if j.exists() {
            return Ok(j.to_string_lossy().to_string());
        }
    }

    if Command::new("java").arg("-version").output().is_ok() {
        return Ok("java".into());
    }

    Err("Java not found".into())
}
