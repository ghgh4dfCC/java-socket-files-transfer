package com.filestransfer.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExecShellTest {
    public static void main(String[] args) {
        /*Process process;
        //一次性执行多个命令
        String cmdLinux ="/bin/sh -c date";
        try {
            Runtime runtime = Runtime.getRuntime();

            System.out.println("开始执行。。。");
            process = runtime.exec(cmdWin);

            //打印执行的输出结果
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8"); //windows下用gbk：解决输出乱码；linux下改用utf-8
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null){
                System.out.println(line);
            }

            is.close();
            isr.close();
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }*/

        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
            System.out.println("这个是windows系统");
        }else{
            System.out.println("linux系统");
        }
    }

}
