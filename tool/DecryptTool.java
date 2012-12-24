/*
 * Authored By Julian Chu <walkingice@0xlab.org>
 *
 * Copyright (c) 2012 0xlab.org - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class DecryptTool {

    private final static String sFilename = "MomomeData";
    private final static String ENCODE = "UTF-8";
    private final static String sAlgorithm = "PBEWithMD5AndDES";
    private final static int sIterationCount = 19;

    private final static byte[] sSalt = {
        (byte)0xAA, (byte)0x42, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x42, (byte)0xE3, (byte)0x11
    };

    protected static AlgorithmParameterSpec sSpec = new PBEParameterSpec(sSalt, sIterationCount);

    private static Cipher getCipher(char[] password, int mode) throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidKeySpecException,
            InvalidAlgorithmParameterException{

        KeySpec keySpec = new PBEKeySpec(password, sSalt, sIterationCount);
        SecretKey key = SecretKeyFactory.getInstance(sAlgorithm).generateSecret(keySpec);
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(mode, key, sSpec);
        return cipher;
    }

    public static CharSequence readStrFromStream(InputStream str) throws IOException {
        InputStreamReader isr = new InputStreamReader(str, ENCODE);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder builder = new StringBuilder();
        String line = null;
        while((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder;
    }

    public static CharSequence read(String key, FileInputStream stream) throws Exception {
        CharSequence data = null;
        Cipher cipher = getCipher(key.toCharArray(), Cipher.DECRYPT_MODE);
        CharSequence readIn = readStrFromStream(stream);
        byte[] encrypted = Base64Coder.decode(readIn.toString());
        byte[] utf8 = cipher.doFinal(encrypted);
        data = new String(utf8, ENCODE);

        return data;
    }

    private static void parseFile(CharSequence password) {
        File file = new File(sFilename);
        if (file.exists() && file.canRead()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                System.out.println(read(password.toString(), fis));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("");
                System.out.println("read file failed with password:" + password);
                System.out.println("");
            }
        } else {
            System.out.println("Cannot read file:" + sFilename);
        }
    }

    private static void printUsage() {
        System.out.println("");
        System.out.println("Usage:");
        System.out.println("\t$ java DecryptToo <YourPassword>");
        System.out.println("");
        System.out.println("It use <YourPassword> to decrypt the file:" + sFilename);
        System.out.println("Please place the file in the same directory");
        System.out.println("");
    }

    public static void main(String arg[]) {
        if (arg.length == 1 && !(arg[0].equals("-h"))) {
            parseFile(arg[0]);
        } else {
            printUsage();
        }
    }
}
