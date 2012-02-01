package me.exphc.NoSeed;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Random;
import java.lang.reflect.Field;
import java.io.*;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.command.*;
import org.bukkit.*;


public class NoSeed extends JavaPlugin {
    Logger log = Logger.getLogger("Minecraft");

    Field fakeSeedField;
    Field lastRealSeedField;
    Field forceFlatField;

    public void onEnable() {
        // Use reflection to change the static 'fakeSeed' variable
        try {
            fakeSeedField = net.minecraft.server.Packet1Login.class.getDeclaredField("fakeSeed");
            lastRealSeedField = net.minecraft.server.Packet1Login.class.getDeclaredField("lastRealSeed");
            forceFlatField = net.minecraft.server.Packet1Login.class.getDeclaredField("forceFlat");
        } catch (Exception e) {
            log.severe("Exception: " + e);
            log.severe("NoSeed is not properly installed! Are you using the latest version and running CraftBukkit with -javaagent:plugins/NoSeed.jar?");

            // Make it apparent
            //System.exit(-1);

            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        fakeSeedField.setAccessible(true);
        lastRealSeedField.setAccessible(true);
        forceFlatField.setAccessible(true);

        getConfig().options().copyDefaults(true);
        saveConfig();

        // Pick a random default seed, but make it somewhat recognizable for ease of identification
        Random random = new Random();
        long defaultSeed = random.nextLong();
        defaultSeed /= 10000;
        defaultSeed *= 10000;
        defaultSeed = Math.abs(defaultSeed);
        defaultSeed +=  1111;

        try {

            // Load from config
            fakeSeedField.setLong(null, getConfig().getLong("fakeSeed", defaultSeed));
            forceFlatField.setBoolean(null, getConfig().getBoolean("forceFlat", true));

        } catch (Exception e) {
            log.severe("Exception: " + e);
            log.severe("NoSeed failed to change fakeSeed! Are you running the latest version?");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }


        log.info("NoSeed enabled");
    }

    public void onDisable() {
        log.info("NoSeed disabled");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("seed")) {
            return false;
        }

        Player player;
        if (sender instanceof Player) {
            player = (Player)sender;
        } else {
            player = null;
        }

        boolean changed = false;
       
        // Set
        if (args.length > 0) {
            if (player != null && !player.hasPermission("noseed.set")) {
                sender.sendMessage("You do not have permission to set the fake seed");
                return true;
            }

            if (args[0].equalsIgnoreCase("flat")) {
                try {
                    boolean forceFlat = forceFlatField.getBoolean(null);
                    forceFlat = !forceFlat;
                    forceFlatField.setBoolean(null, forceFlat);
                    getConfig().set("forceFlat", forceFlat);
                    sender.sendMessage("Toggled forceFlat to "+forceFlat);
                } catch (Exception e) {
                    sender.sendMessage("Failed to toggle: " + e.getMessage());
                }
            } else {
                long newSeed = Long.parseLong(args[0]);

                try {
                    fakeSeedField.setLong(null, newSeed);
                } catch (Exception e) {
                    sender.sendMessage("Failed to set: " + e.getMessage());
                }
            }

            changed = true;
        }

        if (player != null && !player.hasPermission("noseed.get")) {
            sender.sendMessage("You do not have permission to get the real seed");
            return true;
        }

        // Get
        try {
            long fakeSeed = fakeSeedField.getLong(null);
            sender.sendMessage("Fake seed: " + fakeSeed);
            getConfig().set("fakeSeed", fakeSeed);

            long lastRealSeed = lastRealSeedField.getLong(null);
            sender.sendMessage("Real seed (last used): " + lastRealSeed);
            getConfig().set("lastRealSeed (read only)", lastRealSeed);

        } catch (Exception e) {
            sender.sendMessage("Failed to get: " + e.getMessage());
        }

        saveConfig();

        if (changed) {
            sender.sendMessage("Relog to view the changes");
        }

        return true;
    }


    // Instrumentation loader

    // Map of classes, by /-separated name, to their bytecode
    static Map<String, byte[]> bytecodeMap;

    public static void log(String message) {
        System.out.println("[NoSeed] " + message);
    }


    public static void premain(String args, Instrumentation inst) {
        bytecodeMap = new HashMap<String, byte[]>();

        log("Initializing");

        bytecodeMap.put("net/minecraft/server/Packet1Login", readResource("Packet1Login.class.noseed", 2705));
        bytecodeMap.put("net/minecraft/server/ServerConfigurationManager", readResource("ServerConfigurationManager.class.noseed", 23512));

        log("Adding transformer");

        inst.addTransformer(new NoSeedTransformer()); 
        // And so it begins!
        // From now on, all classes loaded will pass through the transformer
    }

    private static byte[] readResource(String name, int length) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        byte[] data = new byte[length];
        try {
            int at = 0;
            int n;
            do {
                n = stream.read(data, at, length - at);

                if (n > 0) {
                    at += n;
                }
            } while(n > 0);
            if (at != length) {
                log("Failed to read class file ("+name+")! Read "+at+" bytes, expected "+length);
                System.exit(-1);
            }
        } catch (IOException e) {
            log("Failed to read class file! Exception: " + e.getMessage());
        }

        return data;
    }
}

// Replace classes
class NoSeedTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className, Class redefininingClass, ProtectionDomain domain, byte[] bytes) {

        if (NoSeed.bytecodeMap.containsKey(className)) {
            NoSeed.log("Transforming " + className);
            return NoSeed.bytecodeMap.get(className);
        } else {
            //NoSeed.log("Pass-thru " + className);
            return bytes;
        }
    }
}

