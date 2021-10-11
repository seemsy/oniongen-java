//Usage: java -jar oniongen.jar (keyword) (position) (version)
package oniongen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class oniongen {
    
    static boolean found = false;
    static String keyword = "test";
    static String position = "any";
    static int version = 3;
            
    public static void main(String args[]) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
    //check args input
    if (args.length == 3) {
        keyword = args[0].toLowerCase();
        System.out.println(keyword);

    //check keyword is alphanumeric before continuing
        for (char c:keyword.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                System.out.println("Error: Keyword must be alphanumeric.");
                System.exit(0);
         }
     }
    
    //check position input is valid before continuing
        switch (args[1].toLowerCase()) {
          case "start" -> position = "start";
          case "end" -> position = "end";
          case "any" -> position = "any";

          default -> {
              System.out.println("Invalid position: " + args[1]);
              System.out.println("Valid: start, end, any");
              System.exit(0);
              break;
          }
      }
    //check version is valid then start loop to gen onion address
          switch (args[2]) {
              case "2" -> v2Main(keyword, position);
              case "3" -> v3Main(keyword, position);
              default -> {
                  System.out.println("Invalid version: " + args[2]);
                  System.out.println("Valid: 2, 3");
              }
          }

      }
      else {
          System.out.println("Usage: java -jar oniongen.jar (keyword) (position) (version)\n");
          System.out.println("Keyword: alphanumeric string");
          System.out.println("Position: start, end, any");
          System.out.println("Version: 2, 3");
      }
     
    }
 
    
    public static void v2Main(String keyword, String position) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        do {
            V2Pair v2 = new V2Pair();
            System.out.println(v2.calculateOnionv2());
            if(found = v2.calculateOnionv2().contains(keyword))
            {
                System.out.println(v2.calculateOnionv2());
                System.out.println(v2.privateKeyPEM());
            }
        }
        while(!found);
    }
    
    //v3 will output 
    public static void v3Main(String keyword, String position) throws NoSuchAlgorithmException, IOException, NoSuchProviderException  {
        do {
            V3Pair v3 = new V3Pair();
            System.out.println(v3.calculateOnionv3());
            if(v3.calculateOnionv3().contains(keyword))
            {
                found = true;
                System.out.println(v3.calculateOnionv3());
                v3.privateKeyExport();
                v3.publicKeyExport();
                try (FileOutputStream f = new FileOutputStream("hostname")) {
                    f.write(v3.calculateOnionv3().getBytes());
                }
            }
        }
        while(!found);
    }
    
}
