package com.fongmi.android.tv.api;

import android.util.Base64;

import com.fongmi.android.tv.net.OKHttp;
import com.fongmi.android.tv.utils.FileUtil;
import com.fongmi.android.tv.utils.Json;
import com.google.common.io.BaseEncoding;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decoder {

    public static String getJson(String url) throws Exception {
        String key = url.contains(";") ? url.split(";")[2] : "";
        url = url.contains(";") ? url.split(";")[0] : url;
        String data = getData(url);
        if (Json.valid(data)) return data;
        if (data.isEmpty()) throw new Exception();
        if (data.contains("**")) data = base64(data);
        if (data.startsWith("2423")) data = cbc(data);
        if (key.length() > 0) data = ecb(data, key);
        return data;
    }

    public static String getExt(String ext) {
        try {
            return base64(getData(ext.substring(4)));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static File getSpider(String jar, String md5) {
        try {
            File file = FileUtil.getJar(jar);
            if (md5.length() > 0 && FileUtil.equals(jar, md5)) return file;
            String data = getData(jar.substring(4));
            data = data.substring(data.indexOf("**") + 2);
            return FileUtil.write(file, Base64.decode(data, Base64.DEFAULT));
        } catch (Exception ignored) {
            return FileUtil.getJar(jar);
        }
    }

    private static String getData(String url) throws Exception {
        if (url.startsWith("http")) return OKHttp.newCall(url).execute().body().string();
        else if (url.startsWith("file")) return FileUtil.read(url);
        throw new Exception();
    }

    private static String ecb(String data, String key) throws Exception {
        SecretKeySpec spec = new SecretKeySpec(padEnd(key), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, spec);
        return new String(cipher.doFinal(decodeHex(data)), StandardCharsets.UTF_8);
    }

    private static String cbc(String data) throws Exception {
        int indexKey = data.indexOf("2324") + 4;
        String key = new String(decodeHex(data.substring(0, indexKey)), StandardCharsets.UTF_8);
        key = key.replace("$#", "").replace("#$", "");
        int indexIv = data.length() - 26;
        String iv = data.substring(indexIv).trim();
        iv = new String(decodeHex(iv), StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(padEnd(key), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(padEnd(iv));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        data = data.substring(indexKey, indexIv).trim();
        byte[] encryptDataBytes = decodeHex(data);
        byte[] decryptData = cipher.doFinal(encryptDataBytes);
        return new String(decryptData, StandardCharsets.UTF_8);
    }

    private static String base64(String data) {
        return new String(Base64.decode(data.substring(data.indexOf("**") + 2), Base64.DEFAULT));
    }

    private static byte[] padEnd(String key) {
        return (key + "0000000000000000".substring(key.length())).getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] decodeHex(String s) {
        return BaseEncoding.base16().decode(s.toUpperCase());
    }
}
