package com.hp.netty.test.demo.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * NIO 三大核心内容
 * Channel,Buffer,Selector
 *  每个Channel 都会有一个对应的Buffer
 *  Selector 对应一个线程，一个线程对应多个Channel
 *  Selector 选择执行哪一个Channel 是由事件决定的(时间驱动)。
 *  Selector会根据Channel的事件去切换各个通道去执行程序
 *  Buffer是一个内存块(底层数组，使用指针去控制)
 *  NIO的读写数据是通过Buffer(双向的)可切换的-BIO是读写是分开的
 *
 * @author hp
 * @version 1.0
 * @date 2020/11/12 21:56
 */
public class nioTest {
    public static void main(String[] args) {
//        testBuffer();
//        testFileChannelOut();
//        testFileChannelInput();
        testFileChannelAll();
    }

    private static void testBuffer() {
        //创建指定长度的缓冲区
        IntBuffer buffer=IntBuffer.allocate(5);
        for (int i =0;i<buffer.capacity();i++){
            //存放数据
            buffer.put(i*2);
        }
        //将buffer 读写反转
        buffer.flip();

        //判断是否还存在元素
        while (buffer.hasRemaining()){
            //获取元素并输出
            System.out.println(buffer.get());
        }
        buffer.flip();
    }

    private static void testFileChannelOut() {
        String str="hello hp";
        try {
            FileOutputStream outputStream=new FileOutputStream("E:\\workspace\\hp-netty-test\\test.txt");
            FileChannel fileChannel = outputStream.getChannel();

            ByteBuffer byteBuffer= ByteBuffer.allocate(1024);
            byteBuffer.put(str.getBytes());
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void testFileChannelInput() {
        try {
            File file=new File("E:\\workspace\\hp-netty-test\\test.txt");
            FileInputStream inputStream=new FileInputStream(file);
            FileChannel fileChannel = inputStream.getChannel();

            ByteBuffer byteBuffer= ByteBuffer.allocate((int)file.length());
            fileChannel.read(byteBuffer);
            System.out.println(new String(byteBuffer.array()));
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void testFileChannelAll() {
        try {
            FileInputStream inputStream=new FileInputStream("E:\\workspace\\hp-netty-test\\test.txt");
            FileChannel inputStreamChannel = inputStream.getChannel();

            FileOutputStream outputStream=new FileOutputStream("E:\\workspace\\hp-netty-test\\test2.txt");
            FileChannel outputStreamChannel = outputStream.getChannel();

            ByteBuffer byteBuffer=ByteBuffer.allocate(256);

            while (true){
                //这里需要去复位 不然read会一直是0 死循环
                byteBuffer.clear();
                int read=inputStreamChannel.read(byteBuffer);
                if (read == -1 ){
                    break;
                }
                byteBuffer.flip();
                outputStreamChannel.write(byteBuffer);
            }
            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
