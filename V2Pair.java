package oniongen;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.NoSuchProviderException;
//import java.security.Security;
import java.math.BigInteger;
import java.util.Arrays;
import java.io.IOException;
import java.io.StringWriter;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.apache.commons.codec.binary.Base32;


public class V2Pair {
   
   private RSAKeyPairGenerator kpg;
   private AsymmetricCipherKeyPair kp;
   private AsymmetricKeyParameter publicKey;
   private AsymmetricKeyParameter privateKey;
   private PrivateKeyInfo pkInfo;
   private SubjectPublicKeyInfo pubInfo;
   
    public V2Pair() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        this.kpg = new RSAKeyPairGenerator();
        kpg.init(new RSAKeyGenerationParameters(new BigInteger("10001", 16), new SecureRandom(), 1024, 80));
        this.kp = kpg.generateKeyPair();
        this.publicKey = kp.getPublic();
        this.privateKey = kp.getPrivate();
        this.pkInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(privateKey);
        this.pubInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKey);
    }

    private static byte[] sha1(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input);
        return result;
    }
   
    private byte[] privateKeyPKCS1() throws IOException {
        ASN1Encodable pkASN1Encodable = pkInfo.parsePrivateKey();
        ASN1Primitive pkASN1primitive = pkASN1Encodable.toASN1Primitive();
        byte[] privateKeyPKCS1 = pkASN1primitive.getEncoded();    
        return privateKeyPKCS1;
    }
   
    private byte[] publicKeyPKCS1() throws IOException {
        ASN1Encodable pubASN1Encodable = pubInfo.parsePublicKey();
        ASN1Primitive pubASN1primitive = pubASN1Encodable.toASN1Primitive();
        byte[] publicKeyPKCS1 = pubASN1primitive.getEncoded();    
        return publicKeyPKCS1;
    }
   
   public String privateKeyPEM() throws IOException {
        PemObject pemObject = new PemObject("RSA PRIVATE KEY", privateKeyPKCS1());
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        //TODO: export pem to file
        String pemString = stringWriter.toString();
        return pemString;
       
    }
    
    public String calculateOnionv2() throws NoSuchAlgorithmException, IOException {
        byte[] publicKeySHA1 = sha1(publicKeyPKCS1());
        byte[] publicKeySHA1Trim = Arrays.copyOfRange(publicKeySHA1, 0, 10);
        Base32 b32 = new Base32();
        String onion = b32.encodeAsString(publicKeySHA1Trim).toLowerCase();
        return onion + ".onion";
    }   
}
