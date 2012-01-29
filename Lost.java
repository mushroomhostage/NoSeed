package me.exphc.Lost;

import java.util.logging.Logger;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.DataOutputStream;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.*;

import net.minecraft.server.Packet1Login;

public class Lost extends JavaPlugin {
    Logger log = Logger.getLogger("Minecraft");

    public void onEnable() {
        /*

        http://wiki.vg/Protocol

        Packet 0x01 - Login Request - Server to Client - Map Seed
        Packet 0x09 - Respawn - sent by client, then resent by server

src/minecraft_server/net/minecraft/src/NetLoginHandler.java
117:            netserverhandler.sendPacket(new Packet1Login("", entityplayermp.entityId, worldserver.getRandomSeed(), worldserver.getWorldInfo().func_46069_q(), entityplayermp.itemInWorldManager.getGameType(), (byte)worldserver.worldProvider.worldType, (byte)worldserver.difficultySetting, (byte)worldserver.worldHeight, (byte)mcServer.configManager.getMaxPlayers()));

src/minecraft_server/net/minecraft/src/ServerConfigurationManager.java
203:        entityplayermp1.playerNetServerHandler.sendPacket(new Packet9Respawn((byte)entityplayermp1.dimension, (byte)entityplayermp1.worldObj.difficultySetting, entityplayermp1.worldObj.getRandomSeed(), entityplayermp1.worldObj.getWorldInfo().func_46069_q(), entityplayermp1.worldObj.worldHeight, entityplayermp1.itemInWorldManager.getGameType()));
220:        entityplayermp.playerNetServerHandler.sendPacket(new Packet9Respawn((byte)entityplayermp.dimension, (byte)entityplayermp.worldObj.diff


    Packet1Login.java

       public Packet1Login(String s, int i, long j, WorldType worldtype, int k, byte b0, byte b1, byte b2, byte b3) {
            this.c = j; // map seed

        written out in a(DataOutputStream dataoutputstream)
*/

        Class argTypes[] = new Class[1];
        argTypes[0] = DataOutputStream.class;

        Method method = null;

        try {
            method = Packet1Login.class.getMethod("a", argTypes);
        } catch (NoSuchMethodException e) {
            log.severe("Failed to find Packet1Login a(DataOutputStream) method: " + e);
        }

        log.info("Found method: "+method);
        
        // TODO: change, replacing c


        log.info("Lost enabled");
    }

    public void onDisable() {
        log.info("Lost disabled");
    }
}
