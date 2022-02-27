package com.filestransfer.tests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.MessageFormat;

public class Client {
    public static void main(String[] args) {
        // firstClientTest();
        String SERVER_IP = "192.168.1.102";
        int SERVER_PORT = 8888;
        Socket client = null;
        FileInputStream fis = null;
        DataOutputStream dos = null;
        try {
            try {
                client = new Socket(SERVER_IP, SERVER_PORT);
                // 向服务端传送文件
                File file = new File("c:/experiments.docx");
                fis = new FileInputStream(file);
                dos = new DataOutputStream(client.getOutputStream());

                // 文件名和长度
                String fileName = file.getName();
                dos.writeUTF(fileName);
                dos.flush();
                dos.writeLong(file.length());
                dos.flush();

                // 传输文件
                byte[] sendBytes = new byte[1024];
                int length = 0;
                while ((length = fis.read(sendBytes, 0,
                    sendBytes.length)) > 0) {
                    dos.write(sendBytes, 0, length);
                    dos.flush();
                }

                System.out.println(MessageFormat.format("文件{0}传输完毕", fileName));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    fis.close();
                if (dos != null)
                    dos.close();
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void firstClientTest() {
        try {
            // 创建客户端Socket，指定服务器地址和端口
            Socket socket = new Socket("192.168.1.102", 8888);
            // 建立连接后，获取输出流，向服务器端发送信息
            OutputStream os = socket.getOutputStream();
            // 输出流包装为打印流
            PrintWriter pw = new PrintWriter(os);
            // 向服务器端发送信息
            System.out.println("线程休眠10秒");
            Thread.sleep(3000);
            pw.write("用户名：test;密码：123");// 写入内存缓冲区
            pw.flush();// 刷新缓存，向服务器端输出信息
            socket.shutdownOutput();// 关闭输出流

            // 获取输入流，接收服务器端响应信息
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            String data = null;
            while ((data = br.readLine()) != null) {
                System.out.println("我是客户端，服务器端提交信息为：" + data);
            }

            // 关闭其他资源
            // br.close();
            // is.close();
            // pw.close();
            // os.close();
            socket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
