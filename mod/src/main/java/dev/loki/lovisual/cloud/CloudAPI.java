package dev.loki.lovisual.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.loki.lovisual.config.BackendConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class CloudAPI {
    private static final Gson GSON = new Gson();
    private final String userId;

    public CloudAPI(String userId) {
        this.userId = userId;
    }

    public String upload(String name, String data) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("user_id", userId);
        body.addProperty("name", name);
        body.addProperty("data", data);

        String resp = post("/configs", body.toString());
        JsonObject obj = GSON.fromJson(resp, JsonObject.class);
        if (obj.has("error")) throw new Exception(obj.get("error").getAsString());
        return obj.get("share_key").getAsString();
    }

    public String download(String key) throws Exception {
        String resp = get("/configs/share/" + key);
        JsonObject obj = GSON.fromJson(resp, JsonObject.class);
        if (obj.has("error")) throw new Exception(obj.get("error").getAsString());
        return obj.get("data").getAsString();
    }

    private String get(String path) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(BackendConfig.getApiBase() + path).toURL().openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        return read(conn);
    }

    private String post(String path, String data) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(BackendConfig.getApiBase() + path).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
        return read(conn);
    }

    private String read(HttpURLConnection conn) throws Exception {
        InputStream is = conn.getResponseCode() < 400 ? conn.getInputStream() : conn.getErrorStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) sb.append(line);
        r.close();
        return sb.toString();
    }
}
