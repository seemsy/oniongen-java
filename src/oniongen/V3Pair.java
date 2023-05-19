package oniongen;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.apache.commons.codec.binary.*;

public class V3Pair {
    
   private Ed25519KeyPairGenerator kpg;
   private AsymmetricCipherKeyPair kp;
   private Ed25519PublicKeyParameters publicKey;
   private Ed25519PrivateKeyParameters privateKey;

    public V3Pair() throws IOException { 
        this.kpg = new Ed25519KeyPairGenerator();
        kpg.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
        this.kp = kpg.generateKeyPair();
        this.privateKey = (Ed25519PrivateKeyParameters) kp.getPrivate();
        this.publicKey = (Ed25519PublicKeyParameters) kp.getPublic();

    }

    private static byte[] sha512(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-512");
        byte[] result = mDigest.digest(input);
        return result;
    } 
    
    
    private static byte[] sha3_256(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA3-256");
        byte[] result = mDigest.digest(input);
        return result;
    } 
    
    public byte[] ed25519OnionChecksum() throws IOException, NoSuchAlgorithmException{
       String onionCheckString = ".onion checksum";
       byte[] onionCheckStringBytes = onionCheckString.getBytes();
       byte[] pubByte = this.publicKey.getEncoded();
       
       ByteArrayOutputStream sumOut = new ByteArrayOutputStream();
       sumOut.writeBytes(onionCheckStringBytes);
       sumOut.writeBytes(pubByte);
       sumOut.write(0x03);
       
       byte[] checksum = sha3_256(sumOut.toByteArray());
       return Arrays.copyOfRange(checksum,0,2);

    }
    
    // dont need to expand private key unless it matches
    public byte[] expandPrivateKey() throws NoSuchAlgorithmException {
        byte[] hash = sha512(Arrays.copyOfRange(privateKey.getEncoded(), 0, 32));
        hash[0] &= 248;
        hash[31] &= 127;
        hash[31] |= 64;
        return hash;
    }
    
    public void privateKeyExport() throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream privOut = new ByteArrayOutputStream();
        privOut.writeBytes("== ed25519v1-secret: type0 ==".getBytes());
        privOut.write(0x00);
        privOut.write(0x00);
        privOut.write(0x00);
        //check if 0000000 works the same
        privOut.writeBytes(expandPrivateKey());
        
        byte[] privateKeyContents = privOut.toByteArray();
        
        FileOutputStream f = new FileOutputStream("hs_ed25519_secret_key");
        f.write(privateKeyContents);
        f.close();
    }
    
     public void publicKeyExport() throws FileNotFoundException, IOException {
        ByteArrayOutputStream pubOut = new ByteArrayOutputStream();
        pubOut.writeBytes("== ed25519v1-public: type0 ==".getBytes());
        pubOut.write(0x00);
        pubOut.write(0x00);
        pubOut.write(0x00);
        pubOut.writeBytes(publicKey.getEncoded());
       
        byte[] publicKeyContents = pubOut.toByteArray();
        
        FileOutputStream f = new FileOutputStream("hs_ed25519_public_key");
        f.write(publicKeyContents);
        f.close();
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
        
        String onion = new String(b32encoded, StandardCharsets.UTF_8).toLowerCase();
        return onion;
    }
    
   
}
