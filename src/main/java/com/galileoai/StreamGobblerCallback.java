package com.galileoai;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;


/**
 * 用于处理Runtime.getRuntime().exec产生的错误流及输出流
 * Created by baymin on 18-4-18.
 */
@Slf4j
public class StreamGobblerCallback extends Thread {
    private Work work;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Work {

        private boolean doing=true;
        private String res;
        public void setResult(String res) {
          log.info("结果：{}",res);
          this.setRes(res);
        }
    }

    InputStream is;
    String type;
    OutputStream os;

    StreamGobblerCallback(InputStream is, String type,Work w) {
        this(is, type, null,w);
    }

    StreamGobblerCallback(InputStream is, String type, OutputStream redirect,Work w) {
        this.is = is;
        this.type = type;
        this.os = redirect;
        this.work=w;
    }

    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            if (os != null)
                pw = new PrintWriter(os);

            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String res="";
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null)
                    pw.println(line);
                log.info("这里可以做手脚");
                log.info("{}>{}", type, line);
                res+=line;
            }


            if (pw != null){

                pw.flush();
            }
            work.setDoing(false);
            work.setResult(res);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
