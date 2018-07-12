package com.galileoai.utils;

import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;

public class Utils {

    public static String imgSave(MultipartFile file, String savePath) throws Exception {

        File dir = new File(savePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Get the file and save it somewhere
        byte[] bytes = file.getBytes();
        String fileAllPath = "";
        String name = System.currentTimeMillis() + ".jpg";
        fileAllPath = savePath + name;
        FileOutputStream out = new FileOutputStream(fileAllPath);
        out.write(bytes);
        out.flush();
        out.close();
        return fileAllPath;
    }

    public static byte[] getByteByImgbase64(String base64) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            return decoder.decodeBuffer(base64);
        } catch (Exception er) {
            return null;
        }
    }
}
