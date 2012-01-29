
package me.exphc.Lost;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

import java.io.*;

class Transformer implements ClassFileTransformer {
    byte[] newBytes;

    public Transformer(byte[] nb) {
        super();

        newBytes = nb;
    }

    public byte[] transform(ClassLoader loader, String className, Class redefininingClass, ProtectionDomain domain, byte[] bytes) {
        System.out.println("Transformer: " + className);

        if (!className.equals("net/minecraft/server/Packet1Login")) {
            return bytes;
        }

        return newBytes;
    }
}

public class LostLoader {
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
        System.out.println("Reading class");
        byte[] data = slurp("Lost-dev/Packet1Login.class"); // TODO: relative paths
        System.out.println("Adding transformer");

        // see http://www.javalobby.org/java/forums/t19309.html
        inst.addTransformer(new Transformer(data)); 
    }
}

