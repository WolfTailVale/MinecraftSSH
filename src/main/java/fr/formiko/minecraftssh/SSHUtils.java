package fr.formiko.minecraftssh;

import fr.formiko.utils.FLUFiles;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SSHUtils {
    private SSHUtils() {}
    public static void runAsynchronouslyAndDisplayResult(BooleanSupplier booleanSupplier, CommandSender commandSender, String cmd) {
        // Send immediate feedback that the process is starting
        commandSender.sendMessage(Component.text("Starting: " + cmd, NamedTextColor.YELLOW));
        new Thread(() -> runAndDisplayResult(booleanSupplier, commandSender, cmd)).start();
    }
    public static void runAndDisplayResult(BooleanSupplier booleanSupplier, CommandSender commandSender, String cmd) {
        long startTime = System.currentTimeMillis();
        System.out.println("[MinecraftSSH] Executing command: " + cmd);
        try {
            if (booleanSupplier.getAsBoolean()) {
                commandSender.sendMessage(Component.text(getMessage(true, startTime, cmd), NamedTextColor.GREEN));
                System.out.println("[MinecraftSSH] Command completed successfully: " + cmd + " (took " + (System.currentTimeMillis() - startTime) + "ms)");
            } else {
                commandSender.sendMessage(Component.text(getMessage(false, startTime, cmd), NamedTextColor.RED));
                System.out.println("[MinecraftSSH] Command failed: " + cmd + " (took " + (System.currentTimeMillis() - startTime) + "ms)");
            }
        } catch (Exception e) {
            commandSender.sendMessage(Component.text(getMessage(false, startTime, cmd), NamedTextColor.RED));
            System.out.println("[MinecraftSSH] Command threw exception: " + cmd + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getMessage(boolean success, long startTime, String cmd) {
        long duration = System.currentTimeMillis() - startTime;
        String status = success ? "✓ Success" : "✗ Failed";
        return status + " in " + duration + "ms for \"" + cmd + "\"";
    }

    public static void runAsynchronouslyAndDisplayResult(Supplier<String> supplier, CommandSender commandSender, String cmd) {
        // Send immediate feedback that the process is starting
        commandSender.sendMessage(Component.text("Starting: " + cmd, NamedTextColor.YELLOW));
        new Thread(() -> runAndDisplayResult(supplier, commandSender, cmd)).start();
    }
    public static void runAndDisplayResult(Supplier<String> supplier, CommandSender commandSender, String cmd) {
        System.out.println("[MinecraftSSH] Executing command: " + cmd);
        runAndDisplayResult(() -> {
            String message;
            try {
                message = supplier.get();
            } catch (Exception e) {
                commandSender.sendMessage(Component.text("Error executing command: " + e.getMessage(), NamedTextColor.RED));
                System.out.println("[MinecraftSSH] Command threw exception: " + cmd + " - " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            if (message == null) {
                System.out.println("[MinecraftSSH] Command returned null result: " + cmd);
                return false;
            } else {
                commandSender.sendMessage(Component.text(message));
                System.out.println("[MinecraftSSH] Command output length: " + message.length() + " characters");
                return true;
            }
        }, commandSender, cmd);
    }

    public static String byteToHumainReadableLenght(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), "KMGTPE".charAt(exp - 1));
    }

    public static List<String> getDirectoriesAndFiles(String path) {
        if (path == null || path.isEmpty() || !path.contains("/")) {
            path = ".";
        } else {
            path = path.substring(0, path.lastIndexOf("/"));
        }
        return FLUFiles.listFiles(path);
    }
}
