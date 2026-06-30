package dev.loki.lovisual.module.impl.misc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.loki.lovisual.gui.hud.impl.Notifications;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleCategory;
import dev.loki.lovisual.module.ModuleInfo;
import dev.loki.lovisual.setting.impl.BooleanSetting;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

@ModuleInfo(name = "IRC", desc = "In-game IRC chat", category = ModuleCategory.MISC, key = 0)
public class IRC extends Module {
    public static IRC INSTANCE;

    private final BooleanSetting autoConnect = new BooleanSetting("AutoConnect", true);
    private WebSocket ws;
    private HttpClient httpClient;
    private final Gson gson = new Gson();
    private String uri = "ws://localhost:8080/ws";

    @Override
    public void onEnable() {
        INSTANCE = this;
        connect();
    }

    @Override
    public void onDisable() {
        disconnect();
        INSTANCE = null;
    }

    public boolean isConnected() {
        return ws != null && !ws.isInputClosed();
    }

    public void connect() {
        if (isConnected()) return;
        httpClient = HttpClient.newHttpClient();
        httpClient.newWebSocketBuilder()
                .buildAsync(URI.create(uri), new Listener())
                .whenComplete((webSocket, throwable) -> {
                    if (throwable != null) {
                        log.error("IRC WS connect failed", throwable);
                        Notifications.error("IRC: Connection failed");
                    } else {
                        ws = webSocket;
                        Notifications.info("IRC: Connected");
                    }
                });
    }

    public void disconnect() {
        if (ws != null && !ws.isInputClosed()) {
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
        }
        ws = null;
        if (httpClient != null) {
            httpClient.close();
            httpClient = null;
        }
    }

    public void sendMessage(String content) {
        if (!isConnected()) {
            Notifications.warn("IRC: Not connected");
            return;
        }
        JsonObject payload = new JsonObject();
        payload.addProperty("content", content);

        JsonObject msg = new JsonObject();
        msg.addProperty("type", "irc_message");
        msg.add("payload", payload);

        ws.sendText(gson.toJson(msg), true);
    }

    private class Listener implements WebSocket.Listener {
        private final StringBuilder buffer = new StringBuilder();

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                String text = buffer.toString();
                buffer.setLength(0);
                try {
                    JsonObject msg = gson.fromJson(text, JsonObject.class);
                    String type = msg.get("type").getAsString();
                    if ("irc_message".equals(type)) {
                        JsonObject payload = msg.getAsJsonObject("payload");
                        String from = payload.get("from").getAsString();
                        String content = payload.get("content").getAsString();
                        Notifications.info("IRC [" + from + "]: " + content);
                    }
                } catch (Exception e) {
                    log.error("Failed to parse IRC message", e);
                }
            }
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            log.error("IRC WS error", error);
            Notifications.error("IRC: Connection error");
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            ws = null;
            Notifications.warn("IRC: Disconnected");
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }
    }
}
