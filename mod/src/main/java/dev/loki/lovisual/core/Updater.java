package dev.loki.lovisual.core;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import dev.loki.lovisual.LoVisual;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Updater {
    private static final String VERSION_URL = "http://localhost:8080/version";
    private static final Gson GSON = new Gson();

    public static void check() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(VERSION_URL))
                    .GET()
                    .build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                JsonObject json = GSON.fromJson(resp.body(), JsonObject.class);
                String latest = json.get("version").getAsString();
                if (!latest.equals(LoVisual.VERSION)) {
                    String modUrl = json.get("mod_url").getAsString();
                    LoVisual.LOGGER.info("[LoVisual] Update available: {} -> {}", latest, modUrl);
                }
            } catch (Exception e) {
                // silently fail
            }
        }).start();
    }
}
