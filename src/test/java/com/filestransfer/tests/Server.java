package com.filestransfer.tests;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        // firstServerTest();
        ServerSocket server = null;
        DataInputStream dis = null;
        FileOutputStream fos = null;
        try {
            try {
                server = new ServerSocket(8888);
                System.out.println("***服务器即将启动，等待客户端的连接***");

                while (true) {
                    Socket client = server.accept();

                    dis = new DataInputStream(client.getInputStream());
                    // 文件名和长度
                    String fileName = dis.readUTF();
                    long fileLength = dis.readLong();
                    fos = new FileOutputStream(new File("d:/" + fileName));

                    byte[] sendBytes = new byte[1024];
                    int transLen = 0;
                    System.out.println("----开始接收文件<" + fileName + ">,文件大小为<"
                            + fileLength + ">----");
                    while (true) {
                        int read = 0;
                        read = dis.read(sendBytes);
                        if (read == -1)
                            break;
                        transLen += read;
                        System.out.println(
                            "接收文件进度" + 100 * transLen / fileLength + "%...");
                        fos.write(sendBytes, 0, read);
                        fos.flush();
                    }
                    System.out.println("----接收文件<" + fileName + ">成功-------");

                    if (dis != null) {
                        dis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                    client.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (dis != null) {
                    dis.close();
                }
                if (fos != null) {
                    fos.close();
                }

                server.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void firstServerTest() {
        try {
            // 创建一个服务器端的Socket，即ServerSocket,绑定需要监听的端口
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket socket = null;
            // 记录连接过服务器的客户端数量
            int count = 0;
            System.out.println("***服务器即将启动，等待客户端的连接***");
            while (true) {// 循环侦听新的客户端的连接
                // 调用accept（）方法侦听，等待客户端的连接以获取Socket实例
                socket = serverSocket.accept();
                // 创建新线程
                Thread thread = new Thread(new ServerThread(socket));
                thread.start();

                count++;
                System.out.println("服务器端被连接过的次数：" + count);
                InetAddress address = socket.getInetAddress();
                System.out.println("当前客户端的IP为：" + address.getHostAddress());
            }
            // serverSocket.close();一直循环监听，不用关闭连接
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
