use eframe::egui;
use crate::api::Client;
use crate::login::LoginScreen;
use crate::dashboard::Dashboard;
use crate::launch;

pub enum Screen {
    Login(LoginScreen),
    Dashboard(Dashboard),
}

pub struct LauncherApp {
    screen: Screen,
    api: Client,
    username: String,
    launch_cmd: String,
}

impl Default for LauncherApp {
    fn default() -> Self {
        let saved_cmd = std::fs::read_to_string(
            dirs::config_dir().unwrap_or_else(|| std::path::PathBuf::from("."))
                .join("lovisual").join("launch_cmd.txt")
        ).unwrap_or_default();

        Self {
            screen: Screen::Login(LoginScreen::default()),
            api: Client::new(),
            username: String::new(),
            launch_cmd: saved_cmd,
        }
    }
}

impl eframe::App for LauncherApp {
    fn update(&mut self, ctx: &egui::Context, _frame: &mut eframe::Frame) {
        let mut switch_to_login = false;
        let mut launch_click = false;

        match &mut self.screen {
            Screen::Login(login) => {
                if let Some(user) = login.show(ctx, &mut self.api) {
                    match self.api.get_profile() {
                        Ok(profile) => {
                            self.username = user;
                            self.screen = Screen::Dashboard(Dashboard {
                                profile: Some(profile),
                                config_cmd: self.launch_cmd.clone(),
                                ..Default::default()
                            });
                        }
                        Err(e) => {
                            login.error = e;
                        }
                    }
                }
            }
            Screen::Dashboard(dash) => {
                let mut logout = false;
                dash.show(ctx, &mut launch_click, &mut logout, &mut self.launch_cmd);

                if launch_click {
                    dash.status = "Launching Minecraft...".into();
                    match launch::launch(&self.username, &self.launch_cmd) {
                        Ok(msg) => dash.status = msg,
                        Err(e) => dash.status = e,
                    }
                }

                if logout {
                    switch_to_login = true;
                    launch::save_session(&self.username);
                }
            }
        }

        if switch_to_login {
            self.api = Client::new();
            self.screen = Screen::Login(LoginScreen::default());
        }
    }
}
