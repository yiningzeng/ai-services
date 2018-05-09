package com.galileoai;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baymin on 18-4-13.
 */
public class ShellKit {
    /**
     * 运行shell脚本
     * @param shell 需要运行的shell脚本
     */
    public static  void execShell(String shell) {
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec(shell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 运行shell
     *
     * @param shStr
     * 需要执行的shell
     * @return
     * @throws IOException
     * 注:如果sh中含有awk,一定要按new String[]{"/bin/sh","-c",shStr}写,才可以获得流.
     */
    public static  List<String> runShell(String shStr) throws Exception {
        List<String> strList = new ArrayList<String>();

        Process process;
        process = Runtime.getRuntime().exec(new String[] {"/bin/sh","-c",shStr},null,null);


        InputStreamReader ir = new InputStreamReader(process
                .getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;


        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
        // kick off stderr
        errorGobbler.start();

        StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(), "STDOUT");
        // kick off stdout
        outGobbler.start();

        /*InputStreamReader ir = new InputStreamReader(process
                .getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;
        process.waitFor();
        while ((line = input.readLine()) != null) {
            strList.add(line);
        }*/

        return strList;
    }
}
