//package cryptopro.jcp.example;
import ru.CryptoPro.JCP.JCP;
import java.io.UnsupportedEncodingException;
//import java.security.KeyPairGenerator;
import java.security.*;
import java.util.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import ru.CryptoPro.JCPRequest.GostCertificateRequest;
import java.io.*;


public class GetCertFromToken {
    public static String toHexString(byte[] array) {
        final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F'};
        StringBuffer ss = new StringBuffer(array.length * 3);
        for (int i = 0; i < array.length; i++) {
            ss.append(' ');
            ss.append(hex[(array[i] >>> 4) & 0xf]);
            ss.append(hex[array[i] & 0xf]);
        }
        return ss.toString();
    }
    public static void main(String[] argv) {
        try {

            KeyStore ks = KeyStore.getInstance("J6CFStore", "JCP");

            char[] STORE_PASS = new char[] {'1', '2', '3', '4'};
            String keystore_name = "gost_on_token";

            ks.load(null, null);
            final Certificate cert = ks.getCertificate(keystore_name);
            //System.out.println(cert.toString());

            PrivateKey privateKey = (PrivateKey)ks.getKey(keystore_name, STORE_PASS);
            //System.out.println(key);

            // подписываем
            final String SIGN_EL_ALG_2012_512 = JCP.GOST_SIGN_2012_512_NAME;
            final String SAMPLE_TEXT = "test signature";
            final Signature sign = Signature.getInstance(SIGN_EL_ALG_2012_512);
            sign.initSign(privateKey);
            sign.update(SAMPLE_TEXT.getBytes());
            final byte[] signEL = sign.sign();
            System.out.println(toHexString(signEL));

            // проверяем подпись
            Certificate certificate = ks.getCertificate(keystore_name);
            PublicKey publicKey = certificate.getPublicKey();
            final Signature sig = Signature.getInstance(SIGN_EL_ALG_2012_512);
            sig.initVerify(publicKey);
            sig.update(SAMPLE_TEXT.getBytes());
            final boolean signELver = sig.verify(signEL);
            System.out.println(signELver);
        }
        catch (Exception e) {
            System.out.println("Exception thrown : " + e);
        } 
    }
}

// изменить тип хранилища, путь и пароль