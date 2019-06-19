package com.galileoai.utils;

import com.galileoai.ret.ResPcb;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    public static void main(String[] args) {
       System.out.println(getMD5("尼玛"));

        ExportExcelUtil<ResPcb> util = new ExportExcelUtil<ResPcb>();

        List<ResPcb> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new ResPcb("0",2,"80ms","[points]","name","http://192.168.1.1",""));
            list.add(new ResPcb("0",3,"90ms","[points]","name","http://192.168.1.1",""));
        }
        String[] columnNames = { "ID", "检测点数", "检测时间","检测点","文件名","在线结果图片","null" };
        try {
            util.exportExcel("用户导出", columnNames, list, new FileOutputStream("test.xls"), ExportExcelUtil.EXCEL_FILE_2003);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNowDate(){
        return new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date());
    }


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


    /**
     * base64字符串转换成图片
     * @param imgStr		base64字符串
     * @param imgFilePath	图片存放路径
     * @return
     *
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:42:17
     */
    public static boolean Base64ToImage(String imgStr,String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片

        if (imgStr==null||imgStr=="") // 图像数据为空
            return false;

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }

            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            return false;
        }

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

    /**
     * 对字符串md5加密(小写+字母)
     *
     * @param str 传入要加密的字符串
     * @return  MD5加密后的字符串
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //加密
    public static String getBase64(String str){
        byte[] b=null;
        String s=null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(b!=null){
            s=new BASE64Encoder().encode(b);
        }
        return s.replaceAll("[\\s*\t\n\r]", "");
    }


}
