use eframe::egui;
use egui::{CornerRadius, Color32};
use crate::api::Profile;
use crate::theme::ACCENT;
use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::{Arc, Mutex};

#[derive(Default)]
pub struct Dashboard {
    pub profile: Option<Profile>,
    pub status: String,
    pub config_cmd: String,
    pub show_settings: bool,
    pub update_checked: Arc<AtomicBool>,
    pub update_available: Arc<Mutex<Option<String>>>,
}

impl Dashboard {
    pub fn show(&mut self, ctx: &egui::Context, launch: &mut bool, logout: &mut bool, _saved_cmd: &mut String) {
        if !self.update_checked.load(Ordering::Relaxed) {
            self.update_checked.store(true, Ordering::Relaxed);
            let update_available = self.update_available.clone();
            std::thread::spawn(move || {
                match reqwest::blocking::get("http://localhost:8080/version") {
                    Ok(resp) => {
                        if let Ok(body) = resp.text() {
                            if let Ok(val) = serde_json::from_str::<serde_json::Value>(&body) {
                                let latest = val["version"].as_str().unwrap_or("");
                                if latest != "1.0.0" {
                                    if let Ok(mut avail) = update_available.lock() {
                                        *avail = Some("Update available!".into());
                                    }
                                }
                            }
                        }
                    }
                    Err(_) => {}
                }
            });
        }
        if let Ok(mut avail) = self.update_available.lock() {
            if let Some(msg) = avail.take() {
                self.status = msg;
            }
        }

        egui::CentralPanel::default().show(ctx, |ui| {
            ui.add_space(16.0);

            ui.horizontal(|ui| {
                ui.heading(egui::RichText::new("LoVisual").color(ACCENT).size(24.0).strong());
                ui.with_layout(egui::Layout::right_to_left(egui::Align::Center), |ui| {
                    if ui.button("Settings").clicked() {
                        self.show_settings = !self.show_settings;
                    }
                    ui.add_space(8.0);
                    if ui.button("Logout").clicked() {
                        *logout = true;
                    }
                });
            });

            ui.add_space(20.0);

            if self.show_settings {
                self.show_settings_panel(ui);
            } else {
                self.show_profile(ui);
                self.show_play_button(ui, launch);
            }
        });
    }

    fn show_settings_panel(&mut self, ui: &mut egui::Ui) {
        egui::Frame::default()
            .fill(Color32::from_rgb(18, 4, 4))
            .corner_radius(CornerRadius::same(10))
            .stroke(egui::Stroke::new(1.0, Color32::from_rgb(40, 8, 8)))
            .inner_margin(egui::Margin::symmetric(20, 16))
            .show(ui, |ui| {
                ui.heading(egui::RichText::new("Launch Settings").size(16.0).color(ACCENT));
                ui.add_space(8.0);

                ui.label("Custom launch command:");
                ui.label(egui::RichText::new(
                    "Use {username} as placeholder. Leave empty for auto-detect."
                ).size(10.0).color(Color32::from_gray(120)));

                let resp = ui.add_sized(
                    [ui.available_width(), 28.0],
                    egui::TextEdit::singleline(&mut self.config_cmd)
                        .font(egui::TextStyle::Monospace)
                        .desired_width(f32::INFINITY),
                );

                if resp.lost_focus() {
                    let base = dirs::config_dir().unwrap_or_else(|| std::path::PathBuf::from("."));
                    let dir = base.join("lovisual");
                    let _ = std::fs::create_dir_all(&dir);
                    let _ = std::fs::write(dir.join("launch_cmd.txt"), &self.config_cmd);
                }

                ui.add_space(8.0);
                ui.label("Examples:");
                ui.label(egui::RichText::new(
                    "minecraft-launcher\njava -jar fabric-loader.jar\n/prismlauncher/PrismLauncher"
                ).size(10.0).color(Color32::from_gray(120)));
            });
    }

    fn show_profile(&mut self, ui: &mut egui::Ui) {
        if let Some(p) = &self.profile {
            egui::Frame::default()
                .fill(Color32::from_rgb(18, 4, 4))
                .corner_radius(CornerRadius::same(10))
                .stroke(egui::Stroke::new(1.0, Color32::from_rgb(40, 8, 8)))
                .inner_margin(egui::Margin::symmetric(20, 16))
                .show(ui, |ui| {
                    ui.horizontal(|ui| {
                        egui::Frame::default()
                            .fill(ACCENT)
                            .corner_radius(CornerRadius::same(25))
                            .show(ui, |ui| { ui.add_space(50.0); });
                        ui.add_space(16.0);

                        ui.vertical(|ui| {
                            ui.heading(egui::RichText::new(&p.nickname).size(20.0));
                            ui.colored_label(Color32::from_gray(140),
                                &format!("{}h • {}", p.playtime / 3600, &p.user_id[..8]));
                            if !p.avatar_url.is_empty() {
                                ui.colored_label(Color32::from_gray(80), &p.avatar_url);
                            }
                        });
                    });

                    ui.add_space(12.0);
                });
        }
    }

    fn show_play_button(&mut self, ui: &mut egui::Ui, launch: &mut bool) {
        ui.add_space(24.0);

        ui.vertical_centered(|ui| {
            if ui.add_sized(
                [260.0, 48.0],
                egui::Button::new(
                    egui::RichText::new("PLAY")
                        .size(18.0)
                        .strong()
                        .color(Color32::WHITE),
                )
                .fill(ACCENT)
                .corner_radius(CornerRadius::same(8)),
            ).clicked() {
                *launch = true;
            }

            ui.add_space(8.0);
            ui.colored_label(Color32::from_gray(100), &self.status);
        });
    }
}


