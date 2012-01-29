
package com.exphc.Lost;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

class PacketTransformer implements ClassFileTransformer {
    public PacketTransformer() {
        super();
    }

    public byte[] transform(ClassLoader loader, String className, Class redefininingClass, ProtectionDomain domain, byte[] bytes) {
        System.out.println("Transformer: " + className);
        return bytes;
    }
}

public class LostLoader {
    public static void premain(String args, Instrumentation inst) {
        // see http://www.javalobby.org/java/forums/t19309.html
        inst.addTransformer(new PacketTransformer());
    }
}

