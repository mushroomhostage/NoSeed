
package me.exphc.GetLost;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

// Replace classes
class GetLostClassReplacer implements ClassFileTransformer {
    Map<String, byte[]> replacements;

    byte[] newBytes;

    public GetLostClassReplacer(Map<String, byte[]> r) {
        replacements = r;

    }

    public byte[] transform(ClassLoader loader, String className, Class redefininingClass, ProtectionDomain domain, byte[] bytes) {
        System.out.println("Transformer: " + className);

        if (replacements.containsKey(className)) {
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

    public static void premain(String args, Instrumentation inst) {
        Map<String, byte[]> map = new HashMap<String, byte[]>();

        System.out.println("Reading class");
        map.put("net/minecraft/server/Packet1Login", slurp("GetLost-dev/Packet1Login.class"));
        System.out.println("Adding transformer");

        inst.addTransformer(new GetLostClassReplacer(map)); 
    }
}

