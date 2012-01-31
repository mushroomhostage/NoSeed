package me.exphc.NoSeed;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
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
            throw new IllegalArgumentException("NoSeed is not properly installed! Are you running CraftBukkit under ModInstrument?" + e.getMessage());
        }
        fakeSeedField.setAccessible(true);
        lastRealSeedField.setAccessible(true);
        forceFlatField.setAccessible(true);

        getConfig().options().copyDefaults(true);
        saveConfig();

        try {
            // Load from config
            fakeSeedField.setLong(null, getConfig().getLong("fakeSeed", 42));
            forceFlatField.setBoolean(null, getConfig().getBoolean("forceFlat", true));

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to change fakeSeed: " + e.getMessage());
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

        return true;
    }
}

