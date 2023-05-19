//Usage: Usage: java -jar oniongen.jar <regex> <number>
//TODO: threading(?), option to generate more than 1 matched address
package oniongen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.regex.*;

public class oniongen {
    
    static boolean found = false;
    static String keyword = "test";
            
    public static void main(String args[]) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {

    
    if (args.length == 2) {
        keyword = args[0].toLowerCase();
        System.out.println(keyword);
        
        // match start of address
        // ^test.*$
        
        // match end of address
        // .v3 addresses must end in [a,y,q,i]d
       // ".*notbad$"
        
        Pattern rePattern = Pattern.compile(keyword);  
        v3Main(rePattern);
   
      
    }
        
    else {
          System.out.println("Usage: <regex>\n");
          System.out.println("Example: java -jar oniongen.jar ^test.*$");
          System.out.println("Will generate a v3 .onion address beginning with 'test'");
          
      }
     
    }
       

    public static void v2Main(String keyword, String position) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        do {
            V2Pair v2 = new V2Pair();
            System.out.println(v2.calculateOnionv2());
            if(found = v2.calculateOnionv2().contains(keyword))
            {
                //Add export file here to local new folder
                System.out.println(v2.calculateOnionv2());
                System.out.println(v2.privateKeyPEM());
            }
        }
        while(!found);
    }
 
    public static void v3Main(Pattern rePattern) throws NoSuchAlgorithmException, IOException, NoSuchProviderException  {
        do {
            V3Pair v3 = new V3Pair();
            System.out.println(v3.calculateOnionv3());
            Matcher m = rePattern.matcher(v3.calculateOnionv3());
            if(found = m.matches())
            {
                //export file to local new dir/folder
                System.out.println(v3.calculateOnionv3().toLowerCase() + ".onion");
                
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
