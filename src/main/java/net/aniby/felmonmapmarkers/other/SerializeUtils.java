package net.aniby.felmonmapmarkers.other;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SerializeUtils {
    private static final String fontChars =
            " !\"#$%&'()*+,-./0123456789:;<=>?" +
                    "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_" +
                    "'abcdefghijklmnopqrstuvwxyz{|}~\u007F" +
                    "\u00C7\u00FC\u00E9\u00E2\u00E4\u00E0\u00E5\u00E7" + // Çüéâäàåç
                    "\u00EA\u00EB\u00E8\u00EF\u00EE\u00EC\u00C4\u00C5" + // êëèïîìÄÅ
                    "\u00C9\u00E6\u00C6\u00F4\u00F6\u00F2\u00FB\u00F9" + // ÉæÆôöòûù
                    "\u00FF\u00D6\u00DC\u00F8\u00A3\u00D8\u00D7\u0191" + // ÿÖÜø£Ø×ƒ
                    "\u00E1\u00ED\u00F3\u00FA\u00F1\u00D1\u00AA\u00BA" + // áíóúñÑªº
                    "\u00BF\u00AE\u00AC\u00BD\u00BC\u00A1\u00AB\u00BB";  // ¿®¬½¼¡«»


    public static String mapTextSerialize(String string) {
        StringBuilder builder = new StringBuilder();
        for (char ch : string.toCharArray()) {
            String character = String.valueOf(ch);
            if (fontChars.contains(character))
                builder.append(character);
        }
        return builder.toString();
    }

    public static String componentSerialize(Component component) {
        return MiniMessage.miniMessage().serialize(
                component
        ).replaceAll("<[^>]*>", "");
    }
}
