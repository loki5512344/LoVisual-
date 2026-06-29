package dev.loki.lovisual.command;

import dev.loki.lovisual.cloud.CloudAPI;
import dev.loki.lovisual.core.event.EventBus;
import dev.loki.lovisual.core.event.impl.PacketEvent;
import dev.loki.lovisual.module.Module;
import dev.loki.lovisual.module.ModuleManager;
import dev.loki.lovisual.core.event.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static final String PREFIX = "/Lovisual";
    private final List<Command> commands = new ArrayList<>();
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public void init() {
        EventBus.register(this);
        registerDefaults();
    }

    private void registerDefaults() {
        register(new Command("toggle", "Toggle a module", "t") {
            @Override
            public void execute(String[] args) {
                if (args.length < 1) {
                    sendMessage("Usage: /Lovisual toggle <module>");
                    return;
                }
                Module module = ModuleManager.INSTANCE.get(args[0]);
                if (module == null) {
                    sendMessage("Module not found: " + args[0]);
                    return;
                }
                module.toggle();
                sendMessage(module.getName() + " -> " + (module.isEnabled() ? "§aEnabled" : "§cDisabled"));
            }
        });

        register(new Command("bind", "Bind a key to a module", "b") {
            @Override
            public void execute(String[] args) {
                if (args.length < 2) {
                    sendMessage("Usage: /Lovisual bind <module> <key>");
                    return;
                }
                Module module = ModuleManager.INSTANCE.get(args[0]);
                if (module == null) {
                    sendMessage("Module not found");
                    return;
                }
                sendMessage("Bound " + module.getName() + " to " + args[1]);
            }
        });

        register(new Command("help", "Show all commands", "h", "?") {
            @Override
            public void execute(String[] args) {
                sendMessage("§6--- LoVisual Commands ---");
                sendMessage("§e/Lovisual toggle <module> §7- Toggle module");
                sendMessage("§e/Lovisual bind <module> <key> §7- Bind key");
                sendMessage("§e/Lovisual help §7- This screen");
                sendMessage("§e/Lovisual config <save/load/profile> §7- Config management");
            }
        });

        register(new Command("config", "Manage configs") {
            @Override
            public void execute(String[] args) {
                if (args.length < 1) {
                    sendMessage("Usage: /Lovisual config <save/load/profile/list/cloud>");
                    return;
                }
                switch (args[0].toLowerCase()) {
                    case "save" -> {
                        ModuleManager.INSTANCE.configManager.save();
                        sendMessage("§aConfig saved");
                    }
                    case "load" -> {
                        ModuleManager.INSTANCE.configManager.load();
                        sendMessage("§aConfig loaded");
                    }
                    case "profile" -> {
                        if (args.length < 2) {
                            sendMessage("Current profile: " + ModuleManager.INSTANCE.configManager.getProfile());
                            return;
                        }
                        ModuleManager.INSTANCE.configManager.setProfile(args[1]);
                        ModuleManager.INSTANCE.configManager.load();
                        sendMessage("§aSwitched to profile: " + args[1]);
                    }
                    case "list" -> {
                        List<String> profiles = ModuleManager.INSTANCE.configManager.getProfiles();
                        sendMessage("§6Profiles: " + String.join(", ", profiles));
                    }
                    case "cloud" -> {
                        if (args.length < 2) {
                            sendMessage("§e/Lovisual config cloud upload <name> §7- Upload, get share key");
                            sendMessage("§e/Lovisual config cloud download <key> §7- Download by key");
                            return;
                        }
                        if (mc.player == null) return;
                        String uid = mc.player.getUuidAsString();
                        CloudAPI api = new CloudAPI(uid);

                        if ("upload".equals(args[1])) {
                            if (args.length < 3) { sendMessage("§cName it: /Lovisual config cloud upload <name>"); return; }
                            new Thread(() -> {
                                try {
                                    String data = ModuleManager.INSTANCE.configManager.getProfileData();
                                    String key = api.upload(args[2], data);
                                    sendMessage("§aShared! Key: §e" + key + " §7- give this to anyone");
                                } catch (Exception e) {
                                    sendMessage("§cUpload failed: " + e.getMessage());
                                }
                            }).start();
                        } else if ("download".equals(args[1])) {
                            if (args.length < 3) { sendMessage("§cUsage: /Lovisual config cloud download <key>"); return; }
                            new Thread(() -> {
                                try {
                                    String data = api.download(args[2]);
                                    ModuleManager.INSTANCE.configManager.loadFromString(data);
                                    sendMessage("§aConfig downloaded and applied!");
                                } catch (Exception e) {
                                    sendMessage("§cDownload failed: " + e.getMessage());
                                }
                            }).start();
                        }
                    }
                }
            }
        });

    }

    public void register(Command command) {
        commands.add(command);
    }

    @EventHandler
    private void onPacket(PacketEvent event) {
        if (event.getState() != PacketEvent.PacketState.SEND) return;
        if (!(event.getPacket() instanceof ChatMessageC2SPacket packet)) return;

        String message = packet.chatMessage();
        if (!message.startsWith(PREFIX)) return;

        event.cancel();
        String after = message.substring(PREFIX.length()).trim();
        String[] parts = after.split(" +");
        if (parts.length == 0) return;

        String cmdName = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        Command cmd = findCommand(cmdName);
        if (cmd == null) {
            sendMessage("Unknown command. Use /Lovisual help");
            return;
        }
        cmd.execute(args);
    }

    private Command findCommand(String name) {
        for (Command cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(name)) return cmd;
            for (String alias : cmd.getAliases()) {
                if (alias.equalsIgnoreCase(name)) return cmd;
            }
        }
        return null;
    }

    public List<Command> getCommands() { return commands; }

    private void sendMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message), false);
        }
    }
}
