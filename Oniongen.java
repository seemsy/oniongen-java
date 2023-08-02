// Usage: Usage: java -jar oniongen.jar <regex> <number>

package Oniongen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Oniongen {

    static boolean found = false;
    static String keyword = "test";

    public static void main(String args[]) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        if (args.length >= 1) {
            keyword = args[0].toLowerCase();
            System.out.println(keyword);
            Pattern rePattern = Pattern.compile(keyword);
            v3Main(rePattern);
        } else {
            System.out.println("Usage: <regex>\n");
            System.out.println("Example: java -jar oniongen.jar ^test.*$");
            System.out.println("Will generate a v3 .onion address beginning with 'test'");
        }
    }

    public static void v3Main(Pattern rePattern) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        do {
            V3Pair v3 = new V3Pair();
            System.out.println(v3.calculateOnionv3());
            Matcher m = rePattern.matcher(v3.calculateOnionv3());
            if (found = m.matches()) {
                System.out.println(v3.calculateOnionv3().toLowerCase() + ".onion");
                v3.privateKeyExport();
                v3.publicKeyExport();
                try (FileOutputStream f = new FileOutputStream("hostname")) {
                    f.write(v3.calculateOnionv3().getBytes());
                }
            }
        } while (!found);
    }
}
