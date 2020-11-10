package com.hp.netty.test.demo.bio;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hp
 * BIO 同步阻塞式的网络通信。
 * （1）启动服务
 * （2）cmd telnet 127.0.0.1 666
 * (3) send hello
 * (3.5) 令起cmd 循环2，3
 * (4)控制台显示线程id及名称
 *
 * @version 1.0
 * @date 2020/11/10 21:21
 */
public class BioServer {
    final static ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(666);
        while (true){
            System.out.println("主线程id=" + Thread.currentThread().getId() + "线程名称=" + Thread.currentThread().getName());
            //BIO这个地方会阻塞一直到获取到连接请求
            System.out.println("等待连接");
            final Socket socket=serverSocket.accept();
            System.out.println("连接到一个线程");
            newCachedThreadPool.execute(()->{
                handler(socket);
            });
        }
    }

    private static void handler(Socket socket){
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            while (true){
                //这里read的时候 会阻塞一直到获取信息
                System.out.println("等待读取数据");
                int read = inputStream.read(bytes);
                if (read != -1){
                    System.out.println("发现一个新的连接");
                    System.out.println("当前线程id=" + Thread.currentThread().getId() + "线程名称=" + Thread.currentThread().getName());
                    System.out.println("接受到的数据为" + new String(bytes, 0, read));
                }else {
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
