// пакеты Саши
package ru.pozhuev.springcourse.models;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

// мои пакеты
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

public class Crypto() {
    public Crypto() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        // Generation
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//        kpg.initialize(2048);
//        KeyPair kp = kpg.generateKeyPair();
//        Key pub = kp.getPublic();
//        Key pvt = kp.getPrivate();
        // Save
//        Base64.Encoder encoder = Base64.getEncoder();
        // Output
//        String pvt64 = encoder.encodeToString(pvt.getEncoded());
//        System.out.println("-----BEGIN RSA PRIVATE KEY-----\n");
//        System.out.println(pvt64);
//        System.out.println("\n-----END RSA PRIVATE KEY-----\n");
//
//        String pub64 = encoder.encodeToString(pub.getEncoded());
//        System.out.println("-----BEGIN RSA PUBLIC KEY-----\n");
//        System.out.println(pub64);
//        System.out.println("\n-----END RSA PUBLIC KEY-----\n");

        // Check
//        Base64.Decoder decoder = Base64.getDecoder();
//        byte[] keyBytes = decoder.decode(pvt64.getBytes("utf-8"));
//        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        this.pvtKey = keyFactory.generatePrivate(ks);
//        System.out.println(Arrays.equals(pvtKey.getEncoded(), pvt.getEncoded()));
//
//
//        keyBytes = decoder.decode(pub64.getBytes("utf-8"));
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//        PublicKey pubKey = keyFactory.generatePublic(spec);
//        System.out.println(Arrays.equals(pubKey.getEncoded(), pub.getEncoded()));
    }

    // функции Саши
    public static String keyToString(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey stringToPublicKey(String str) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] keyBytes = decoder.decode(str.getBytes("utf-8"));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey stringToPrivateKey(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] keyBytes = decoder.decode(str.getBytes("utf-8"));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(spec);
    }

    // public static String sign(String text, PrivateKey certCenterPrivateKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
    //     Signature sign = Signature.getInstance("SHA256withRSA");
    //     sign.initSign(certCenterPrivateKey);
    //     sign.update(Base64.getEncoder().encode(text.getBytes("utf-8")));
    //     return Base64.getEncoder().encodeToString(sign.sign());
    // }

    // public static boolean verify(String signature, String text, PublicKey certCenterPublicKey) {
    //     try {
    //         Signature sign = Signature.getInstance("SHA256withRSA");
    //         sign.initVerify(certCenterPublicKey);
    //         sign.update(Base64.getEncoder().encode(text.getBytes("utf-8")));
    //         return sign.verify(Base64.getDecoder().decode(signature.getBytes("utf-8")));
    //     }
    //     catch (Exception exception){
    //         return false;
    //     }
    // }

    // мои функции
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

    public static String sign(String text, PrivateKey privateKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        final Signature sign = Signature.getInstance(JCP.GOST_SIGN_2012_512_NAME);
        sign.initSign(privateKey);
        sign.update(text.getBytes());
        final byte[] signEL = sign.sign();
        //System.out.println(toHexString(signEL));
        return signEL;
    }

    public static boolean verify(byte[] signEL, String text, PublicKey publicKey) {
        final Signature sig = Signature.getInstance(JCP.GOST_SIGN_2012_512_NAME);
        sig.initVerify(publicKey);
        sig.update(text.getBytes());
        final boolean signELver = sig.verify(signEL);
        //System.out.println(signELver);
        return signELver;
    }

    public static Certificate getCertificateFromKeystore(String keystoreName) {
        KeyStore ks = KeyStore.getInstance("J6CFStore", "JCP");

        //char[] STORE_PASS = new char[] {'1', '2', '3', '4'};
        String keystore_name = "gost_on_token";

        ks.load(null, null);
        return ks.getCertificate(keystore_name);
    }

    public static PublicKey getPublicFromCertificate(Certificate cert) {
        return cert.getPublicKey();
    }

    public static PrivateKey getPrivateFromKeystore(KeyStore ks, String keystoreName) {
        KeyStore ks = KeyStore.getInstance("J6CFStore", "JCP");

        char[] STORE_PASS = new char[] {'1', '2', '3', '4'};
        String keystore_name = "gost_on_token";

        ks.load(null, null);
        return (PrivateKey)ks.getKey(keystore_name, STORE_PASS);
    }

}