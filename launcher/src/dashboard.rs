use eframe::egui;
use egui::{CornerRadius, Color32};
use crate::api::Profile;
use crate::theme::ACCENT;

#[derive(Default)]
pub struct Dashboard {
    pub profile: Option<Profile>,
    pub status: String,
}

impl Dashboard {
    pub fn show(&mut self, ctx: &egui::Context, launch: &mut bool, logout: &mut bool) {
        egui::CentralPanel::default().show(ctx, |ui| {
            ui.add_space(16.0);

            ui.horizontal(|ui| {
                ui.heading(egui::RichText::new("LoVisual").color(ACCENT).size(24.0).strong());
                ui.with_layout(egui::Layout::right_to_left(egui::Align::Center), |ui| {
                    if ui.button("Logout").clicked() {
                        *logout = true;
                    }
                });
            });

            ui.add_space(20.0);

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
                                .corner_radius(CornerRadius::same(30))
                                .show(ui, |ui| { ui.add_space(50.0); });
                            ui.add_space(16.0);

                            ui.vertical(|ui| {
                                ui.heading(egui::RichText::new(&p.nickname).size(20.0));
                                ui.colored_label(Color32::from_gray(140),
                                    &format!("Level {}  |  {} XP", p.level, p.xp));
                            });
                        });

                        ui.add_space(12.0);

                        ui.horizontal(|ui| {
                            stat(ui, "Playtime", &format!("{}h", p.playtime / 3600));
                        });
                    });
            }

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
        });
    }
}

fn stat(ui: &mut egui::Ui, label: &str, value: &str) {
    ui.vertical_centered(|ui| {
        ui.heading(egui::RichText::new(value).size(18.0).color(ACCENT));
        ui.label(egui::RichText::new(label).size(11.0).color(Color32::from_gray(130)));
    });
    ui.add_space(24.0);
}
