use std::fs;
use std::path::PathBuf;
use eframe::egui;
use egui::{Color32, CornerRadius};
use crate::api::Client;
use crate::theme::ACCENT;

#[derive(Default)]
pub struct LoginScreen {
    pub email: String,
    pub password: String,
    pub username: String,
    pub mode: LoginMode,
    pub error: String,
}

#[derive(Default, PartialEq)]
pub enum LoginMode {
    #[default]
    Login,
    Register,
}

impl LoginScreen {
    fn save_session(username: &str) {
        let base = dirs::config_dir().unwrap_or_else(|| PathBuf::from("."));
        let dir = base.join("lovisual");
        fs::create_dir_all(&dir).ok();
        fs::write(dir.join("account.txt"), username).ok();
    }

    pub fn show(&mut self, ctx: &egui::Context, api: &mut Client) -> Option<String> {
        let mut token: Option<String> = None;

        egui::CentralPanel::default().show(ctx, |ui| {
            ui.add_space(60.0);
            ui.vertical_centered(|ui| {
                ui.heading(
                    egui::RichText::new("LoVisual")
                        .color(ACCENT)
                        .size(36.0)
                        .strong(),
                );
                ui.label("Minecraft Utility Client");
            });

            ui.add_space(40.0);

            egui::Frame::default()
                .inner_margin(egui::Margin::symmetric(40, 10))
                .show(ui, |ui| {
                    ui.vertical_centered(|ui| {
                        let w = 280.0;

                        if self.mode == LoginMode::Register {
                            ui.add_sized([w, 32.0], egui::TextEdit::singleline(&mut self.username)
                                .hint_text("Username"));
                            ui.add_space(6.0);
                        }

                        ui.add_sized([w, 32.0], egui::TextEdit::singleline(&mut self.email)
                            .hint_text("Email"));
                        ui.add_space(6.0);

                        ui.add_sized([w, 32.0], egui::TextEdit::singleline(&mut self.password)
                            .hint_text("Password")
                            .password(true));
                        ui.add_space(16.0);

                        let label = match self.mode {
                            LoginMode::Login => "LOGIN",
                            LoginMode::Register => "REGISTER",
                        };

                        if ui.add_sized([w, 36.0], egui::Button::new(
                            egui::RichText::new(label).size(14.0).strong()
                        ).fill(ACCENT).corner_radius(CornerRadius::same(6))).clicked() {
                            self.error.clear();
                            let result = match self.mode {
                                LoginMode::Login => {
                                    api.login(&self.email, &self.password)
                                        .map(|(t, u)| (t, u))
                                }
                                LoginMode::Register => {
                                    match api.register(&self.email, &self.username, &self.password) {
                                        Ok(username) => {
                                            match api.login(&self.email, &self.password) {
                                                Ok((t, _)) => Ok((t, username)),
                                                Err(e) => Err(e),
                                            }
                                        }
                                        Err(e) => Err(e),
                                    }
                                }
                            };
                            match result {
                                Ok((t, u)) => {
                                    Self::save_session(&u);
                                    token = Some(t);
                                }
                                Err(e) => self.error = e,
                            }
                        }

                        if !self.error.is_empty() {
                            ui.add_space(8.0);
                            ui.colored_label(Color32::RED, &self.error);
                        }

                        ui.add_space(8.0);
                        let toggle = match self.mode {
                            LoginMode::Login => "No account? Register",
                            LoginMode::Register => "Have an account? Login",
                        };
                        if ui.link(toggle).clicked() {
                            self.mode = match self.mode {
                                LoginMode::Login => LoginMode::Register,
                                LoginMode::Register => LoginMode::Login,
                            };
                            self.error.clear();
                        }
                    });
                });
        });

        token
    }
}
