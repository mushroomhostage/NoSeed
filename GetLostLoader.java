
package me.exphc.GetLost;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.lang.reflect.Field;
import java.io.*;

import net.minecraft.server.Packet1Login;

// Replace classes
class GetLostClassReplacer implements ClassFileTransformer {
    Map<String, byte[]> replacements;

    byte[] newBytes;

    public GetLostClassReplacer(Map<String, byte[]> r) {
        replacements = r;

    }

    public byte[] transform(ClassLoader loader, String className, Class redefininingClass, ProtectionDomain domain, byte[] bytes) {

        if (replacements.containsKey(className)) {
            GetLostLoader.log("Transforming " + className);
            return replacements.get(className);
        } else {
            return bytes;
        }
    }
}

public class GetLostLoader {
    private static byte[] slurp(String filename) {
        byte[] bytes;

        try {
            // wish this were simpler..
            File file = new File(filename);
            InputStream stream = new FileInputStream(file);
            int length = (int)file.length();
            bytes = new byte[length];

            int at = 0;
            int bytesRead = 0;
            while (at < length) {
                bytesRead = stream.read(bytes, at, length - at);
                System.out.println("r"+bytesRead);
                at += bytesRead;
            }

            stream.close();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read "+filename+": "+e.getMessage());
        }

        return bytes;
    }

    public static void log(String message) {
        System.out.println("[GetLostLoader] " + message);
    }

    public static void premain(String args, Instrumentation inst) {
        Map<String, byte[]> map = new HashMap<String, byte[]>();

        log("Loading class replacements");
        map.put("net/minecraft/server/Packet1Login", slurp("GetLost-dev/Packet1Login.class"));
        log("Adding transformer");

        inst.addTransformer(new GetLostClassReplacer(map)); 
        // From now on, all classes loaded will pass through the transformer
    

        // Use reflection to change the static 'fakeSeed' variable
        Field fakeSeed;
        try {
            fakeSeed = net.minecraft.server.Packet1Login.class.getDeclaredField("fakeSeed");
        } catch (Exception e) {
            throw new IllegalArgumentException("Class transformation failed! Could not find fakeSeed: " + e.getMessage());
        }
        fakeSeed.setAccessible(true);
        try {
            fakeSeed.setLong(null, 999);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to change fakeSeed: " + e.getMessage());
        }
    }
}

