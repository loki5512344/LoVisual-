package dev.loki.lovisual.gui.mainmenu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private static final Path CONFIG_DIR = getConfigDir();
    private static final Path ACCOUNTS_FILE = CONFIG_DIR.resolve("accounts.txt");
    private static final Path LAST_ACCOUNT_FILE = CONFIG_DIR.resolve("last_account.txt");

    private static Path getConfigDir() {
        String os = System.getProperty("os.name").toLowerCase();
        String base;
        if (os.contains("win")) {
            base = System.getenv("APPDATA");
        } else if (os.contains("mac")) {
            base = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            base = System.getProperty("user.home") + "/.config";
        }
        return Path.of(base, "lovisual");
    }

    static {
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLastAccount() {
        try {
            if (Files.exists(LAST_ACCOUNT_FILE)) {
                return Files.readString(LAST_ACCOUNT_FILE).trim();
            }
        } catch (IOException ignored) {}
        return null;
    }

    public static List<String> getAccounts() {
        List<String> accounts = new ArrayList<>();
        try {
            if (Files.exists(ACCOUNTS_FILE)) {
                List<String> lines = Files.readAllLines(ACCOUNTS_FILE);
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        accounts.add(trimmed);
                    }
                }
            }
        } catch (IOException ignored) {}
        return accounts;
    }

    public static void addAccount(String name) {
        List<String> accounts = getAccounts();
        if (!accounts.contains(name)) {
            accounts.add(name);
            writeAccounts(accounts);
        }
    }

    public static void removeAccount(String name) {
        List<String> accounts = getAccounts();
        accounts.remove(name);
        writeAccounts(accounts);
    }

    public static void setLastAccount(String name) {
        try {
            Files.writeString(LAST_ACCOUNT_FILE, name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeAccounts(List<String> accounts) {
        try {
            Files.write(ACCOUNTS_FILE, accounts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
