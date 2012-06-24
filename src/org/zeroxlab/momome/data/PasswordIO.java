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

package org.zeroxlab.momome.data;

import org.zeroxlab.momome.FileIO;
import org.zeroxlab.momome.FileIO.RWException;
import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.util.Util;

import org.keyczar.util.Base64Coder;

import android.util.Log;
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


public class PasswordIO implements FileIO, Momo {

    private final static String sAlgorithm = "PBEWithMD5AndDES";
    private final static byte[] sSalt = {
        (byte)0xAA, (byte)0x42, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x42, (byte)0xE3, (byte)0x11
    };

    protected int mIterationCount = 19;
    protected AlgorithmParameterSpec mSpec;

    public PasswordIO() {
        mSpec = new PBEParameterSpec(sSalt, mIterationCount);
    }

    @Override
    public boolean save(String key,
            FileOutputStream stream,
            CharSequence content) throws RWException {
        try {
            Cipher cipher = getCipher(key.toCharArray(), Cipher.ENCRYPT_MODE);
            byte[] utf8 = content.toString().getBytes(ENCODING);
            byte[] encoded = cipher.doFinal(utf8);
            CharSequence base64 = Base64Coder.encode(encoded);

            Util.writeStrToStream(stream, base64);
        } catch (Exception e) {
            throw new RWException(e);
        }
        return true;
    }

    @Override
    public boolean save(String key,
            String outputPath,
            CharSequence content) throws RWException {

        try {
            Cipher cipher = getCipher(key.toCharArray(), Cipher.ENCRYPT_MODE);
            byte[] utf8 = content.toString().getBytes(ENCODING);
            byte[] encoded = cipher.doFinal(utf8);
            CharSequence base64 = Base64Coder.encode(encoded);

            Util.writeStrToFile(outputPath, base64);
        } catch (Exception e) {
            throw new RWException(e);
        }
        return true;
    }

    @Override
    public CharSequence read(String key,
            FileInputStream stream) throws RWException {
        CharSequence data = null;

        try {
            Cipher cipher = getCipher(key.toCharArray(), Cipher.DECRYPT_MODE);
            CharSequence readIn = Util.readStrFromStream(stream);
            byte[] encrypted = Base64Coder.decode(readIn.toString());
            byte[] utf8 = cipher.doFinal(encrypted);
            data = new String(utf8, ENCODING);
        } catch (Exception e) {
            throw new RWException(e);
        }

        return data;
    }

    @Override
    public CharSequence read(String key,
            String inputPath) throws RWException {
        CharSequence data = null;

        try {
            Cipher cipher = getCipher(key.toCharArray(), Cipher.DECRYPT_MODE);
            CharSequence readIn = Util.readStrFromFile(inputPath);
            byte[] encrypted = Base64Coder.decode(readIn.toString());
            byte[] utf8 = cipher.doFinal(encrypted);
            data = new String(utf8, ENCODING);

        } catch (Exception e) {
            throw new RWException(e);
        }

        return data;
    }

    private Cipher getCipher(char[] password, int mode) throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidKeySpecException,
            InvalidAlgorithmParameterException{

        KeySpec keySpec = new PBEKeySpec(password, sSalt, mIterationCount);
        SecretKey key = SecretKeyFactory.getInstance(sAlgorithm).generateSecret(keySpec);
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(mode, key, mSpec);
        return cipher;
    }
}
