package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Pepper {

    // Command injection
    public void runtime_exec(String cmd) {
        Process proc = null;
        BufferedReader br = null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
            br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            System.out.println(br.readLine());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Insecure hash algorithm
    public byte[] md5_digest(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(str.getBytes());
        return md.digest();
    }
}
