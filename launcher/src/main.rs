mod app;
mod login;
mod dashboard;
mod api;
mod theme;
mod launch;

fn main() -> eframe::Result {
    let options = eframe::NativeOptions {
        viewport: egui::ViewportBuilder::default()
            .with_inner_size([440.0, 520.0])
            .with_min_inner_size([400.0, 400.0])
            .with_resizable(false),
        centered: true,
        ..Default::default()
    };

    eframe::run_native(
        "LoVisual",
        options,
        Box::new(|cc| {
            theme::apply(&cc.egui_ctx);
            Ok(Box::new(app::LauncherApp::default()))
        }),
    )
}
