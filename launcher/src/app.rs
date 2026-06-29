use eframe::egui;
use crate::api::Client;
use crate::login::LoginScreen;
use crate::dashboard::Dashboard;

pub enum Screen {
    Login(LoginScreen),
    Dashboard(Dashboard),
}

pub struct LauncherApp {
    screen: Screen,
    api: Client,
}

impl Default for LauncherApp {
    fn default() -> Self {
        Self {
            screen: Screen::Login(LoginScreen::default()),
            api: Client::new(),
        }
    }
}

impl eframe::App for LauncherApp {
    fn update(&mut self, ctx: &egui::Context, _frame: &mut eframe::Frame) {
        match &mut self.screen {
            Screen::Login(login) => {
                if let Some(token) = login.show(ctx, &mut self.api) {
                    match self.api.get_profile() {
                        Ok(profile) => {
                            self.screen = Screen::Dashboard(Dashboard {
                                profile: Some(profile),
                                status: "Ready".into(),
                            });
                        }
                        Err(e) => {
                            login.error = e;
                        }
                    }
                }
            }
            Screen::Dashboard(dash) => {
                let mut launch = false;
                let mut logout = false;
                dash.show(ctx, &mut launch, &mut logout);

                if launch {
                    dash.status = "Launching Minecraft...".into();
                    // TODO: launch Minecraft with the mod
                }

                if logout {
                    self.api = Client::new();
                    self.screen = Screen::Login(LoginScreen::default());
                }
            }
        }
    }
}
