package me.exphc.NoSeed;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.lang.reflect.Field;
import java.io.*;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.*;
import org.bukkit.*;

public class NoSeed extends JavaPlugin {
    Logger log = Logger.getLogger("Minecraft");

    Field fakeSeedField;

    public void onEnable() {
        // Use reflection to change the static 'fakeSeed' variable
        try {
            fakeSeedField = net.minecraft.server.Packet1Login.class.getDeclaredField("fakeSeed");
        } catch (Exception e) {
            throw new IllegalArgumentException("Class transformation failed! Could not find fakeSeed: " + e.getMessage());
        }
        fakeSeedField.setAccessible(true);
        try {
            long n = Long.parseLong("456");
            fakeSeedField.setLong(null, n);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to change fakeSeed: " + e.getMessage());
        }


        log.info("NoSeed enabled");
    }

    public void onDisable() {
        log.info("NoSeed disabled");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // TODO: permissions
        if (!cmd.getName().equalsIgnoreCase("seed")) {
            return false;
        }
        
        if (args.length > 0) {
            long newSeed = Long.parseLong(args[0]);

            try {
                fakeSeedField.setLong(null, newSeed);
            } catch (Exception e) {
                sender.sendMessage("Failed to set: " + e.getMessage());
            }
        }

        try {
            long fakeSeed = fakeSeedField.getLong(null);
            sender.sendMessage("Fake seed: " + fakeSeed);
        } catch (Exception e) {
            sender.sendMessage("Failed to get: " + e.getMessage());
        }

        return true;
    }
}

