use egui::{Color32, Style, Vec2, Visuals};

pub fn apply(ctx: &egui::Context) {
    let mut style = Style::default();

    style.visuals = Visuals {
        dark_mode: true,
        override_text_color: Some(Color32::from_gray(200)),
        faint_bg_color: Color32::from_rgb(10, 2, 2),
        extreme_bg_color: Color32::from_rgb(8, 2, 2),
        code_bg_color: Color32::from_rgb(20, 5, 5),
        warn_fg_color: Color32::from_rgb(255, 60, 60),
        error_fg_color: Color32::from_rgb(255, 0, 0),
        hyperlink_color: Color32::from_rgb(200, 50, 50),
        selection: egui::style::Selection {
            bg_fill: Color32::from_rgb(120, 20, 20),
            stroke: egui::Stroke::new(1.0, Color32::from_rgb(200, 30, 30)),
        },
        widgets: egui::style::Widgets {
            noninteractive: egui::style::WidgetVisuals {
                bg_fill: Color32::from_rgb(18, 4, 4),
                weak_bg_fill: Color32::from_rgb(12, 3, 3),
                bg_stroke: egui::Stroke::new(1.0, Color32::from_rgb(40, 8, 8)),
                corner_radius: egui::CornerRadius::same(6),
                fg_stroke: egui::Stroke::new(1.0, Color32::from_gray(140)),
                expansion: 0.0,
            },
            inactive: egui::style::WidgetVisuals {
                bg_fill: Color32::from_rgb(20, 5, 5),
                weak_bg_fill: Color32::from_rgb(15, 3, 3),
                bg_stroke: egui::Stroke::new(1.0, Color32::from_rgb(60, 10, 10)),
                corner_radius: egui::CornerRadius::same(6),
                fg_stroke: egui::Stroke::new(1.0, Color32::from_gray(180)),
                expansion: 0.0,
            },
            hovered: egui::style::WidgetVisuals {
                bg_fill: Color32::from_rgb(40, 8, 8),
                weak_bg_fill: Color32::from_rgb(30, 5, 5),
                bg_stroke: egui::Stroke::new(1.0, Color32::from_rgb(200, 30, 30)),
                corner_radius: egui::CornerRadius::same(6),
                fg_stroke: egui::Stroke::new(2.0, Color32::from_gray(220)),
                expansion: 1.0,
            },
            active: egui::style::WidgetVisuals {
                bg_fill: Color32::from_rgb(60, 15, 15),
                weak_bg_fill: Color32::from_rgb(45, 10, 10),
                bg_stroke: egui::Stroke::new(1.0, Color32::from_rgb(255, 40, 40)),
                corner_radius: egui::CornerRadius::same(6),
                fg_stroke: egui::Stroke::new(2.0, Color32::from_gray(240)),
                expansion: 1.0,
            },
            open: egui::style::WidgetVisuals {
                bg_fill: Color32::from_rgb(30, 6, 6),
                weak_bg_fill: Color32::from_rgb(20, 4, 4),
                bg_stroke: egui::Stroke::new(1.0, Color32::from_rgb(80, 15, 15)),
                corner_radius: egui::CornerRadius::same(6),
                fg_stroke: egui::Stroke::new(1.5, Color32::from_gray(180)),
                expansion: 0.0,
            },
        },
        ..Default::default()
    };

    style.spacing.item_spacing = Vec2::new(12.0, 8.0);
    style.spacing.button_padding = Vec2::new(16.0, 8.0);

    ctx.set_style(style);
}

pub const ACCENT: Color32 = Color32::from_rgb(220, 30, 30);
