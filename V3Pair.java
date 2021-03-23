package oniongen;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import org.apache.commons.codec.binary.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;


public class V3Pair {
    
    Ed25519KeyPairGenerator kpg;
    AsymmetricCipherKeyPair kp;
    Ed25519PublicKeyParameters publicKey;
    Ed25519PrivateKeyParameters privateKey;
    public static String onionCheckString = ".onion checksum";

    public V3Pair() throws IOException { 
        this.kpg = new Ed25519KeyPairGenerator();
        kpg.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
        this.kp = kpg.generateKeyPair();
        this.privateKey = (Ed25519PrivateKeyParameters) kp.getPrivate();
        this.publicKey = (Ed25519PublicKeyParameters) kp.getPublic();

    }
    
    static byte[] sha3_256(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA3-256");
        byte[] result = mDigest.digest(input);
        return result;
    } 
    
    public byte[] ed25519OnionChecksum() throws IOException, NoSuchAlgorithmException{
     
       byte[] onionCheckStringBytes = onionCheckString.getBytes();
       byte[] pubByte = publicKey.getEncoded();
       
       ByteArrayOutputStream sumOut = new ByteArrayOutputStream();
       sumOut.writeBytes(onionCheckStringBytes);
       sumOut.writeBytes(pubByte);
       sumOut.write(0x03);
       
       byte[] checksum = sha3_256(sumOut.toByteArray()
       return Arrays.copyOfRange(checksum,0,2);

    }
    
    public String calculateOnionv3() throws IOException, NoSuchAlgorithmException{
        byte[] pubByte = publicKey.getEncoded();
        ByteArrayOutputStream calcOut = new ByteArrayOutputStream();
        calcOut.writeBytes(pubByte);
        calcOut.writeBytes(ed25519OnionChecksum());
        calcOut.write(0x03);
        byte[] bArray = calcOut.toByteArray();
        
        Base32 b32 = new Base32();
        byte[] b32encoded = b32.encode(bArray);
        
        String onion = new String(b32encoded, StandardCharsets.UTF_8);
        return onion.toLowerCase() + ".onion";

    }
    
   
}
