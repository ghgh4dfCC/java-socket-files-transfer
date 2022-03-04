package com.filestransfer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileOperateTest {
    public static void main(String[] args) {
        String targetPath = "D:\\test\\www.aa.com\\mytest01";
        /*
         * // String targetPath = "";
         * System.out.println(File.separatorChar); if
         * (targetPath.contains("\\")) { targetPath =
         * targetPath.replaceAll("\\\\", "/"); System.out.println(targetPath); }
         * else { System.out.println("no"); }
         * 
         * if (targetPath.contains("/")) { targetPath =
         * targetPath.replaceAll("/", "\\\\"); System.out.println(targetPath); }
         * else { System.out.println("no2"); }
         */
        File directory = new File(targetPath);
        System.out.println(directory.getAbsolutePath());
        String fileName = directory.getName();
        System.out.println("文件名是：" + directory.getName());
        String postfix = fileName.substring(fileName.lastIndexOf(".") + 1);
        System.out.println("后缀名：" + postfix);

        List<String> resultFileName = new ArrayList<String>();
        String filter = "";
        ergodic(directory, resultFileName, filter);
        System.out.println("循环输出所有路径：");
        for (String a : resultFileName) {
            System.out.println(a);
        }
    }

    private static List<String> ergodic(File file, List<String> resultFileName,
            String filters) {
        File[] files = file.listFiles();
        if (files == null)
            return resultFileName;// 判断目录下是不是空的
        for (File f : files) {
            if (f.isDirectory()) {// 判断是否文件夹
                resultFileName.add(f.getPath());
                ergodic(f, resultFileName, filters);// 调用自身,查找子目录
            } else {
                String fileName = f.getName();
                String postfix = fileName
                        .substring(fileName.lastIndexOf(".") + 1);
                if (!"".equals(filters)) {
                    if (!filters.contains(postfix)) {
                        resultFileName.add(f.getPath());
                    }
                } else {
                    resultFileName.add(f.getPath());
                }
            }

        }
        return resultFileName;
    }
}
