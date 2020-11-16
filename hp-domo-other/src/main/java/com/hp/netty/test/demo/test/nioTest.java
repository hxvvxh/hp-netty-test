package com.hp.netty.test.demo.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

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
    public static void main(String[] args) throws Exception {
        testBuffer();
        testFileChannelOut();
        testFileChannelInput();
        testFileChannelAll();
        testCopy();
        testArray();
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
//        while (buffer.hasRemaining()){
//            //获取元素并输出
//            System.out.println(buffer.get());
//        }
        //只读buffer。readOnlyBuffer不可在存放数据
        IntBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
        while (readOnlyBuffer.hasRemaining()){
            //获取元素并输出
            System.out.println(readOnlyBuffer.get());
        }
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

    private static void testCopy() throws Exception{
        FileInputStream inputStream=new FileInputStream("E:\\workspace\\hp-netty-test\\a.jpg");
        FileOutputStream outputStream=new FileOutputStream("E:\\workspace\\hp-netty-test\\aCopy.jpg");
        FileChannel inputStreamChannel = inputStream.getChannel();
        FileChannel outputStreamChannel = outputStream.getChannel();

        //直接进行流数据复制
        outputStreamChannel.transferFrom(inputStreamChannel,0,inputStreamChannel.size());

        inputStreamChannel.close();
        outputStreamChannel.close();
        inputStream.close();
        outputStream.close();
    }

    private static void testArray() throws Exception{
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        InetSocketAddress address=new InetSocketAddress(7777);
        serverSocketChannel.socket().bind(address);

        ByteBuffer[] byteBuffers=new ByteBuffer[2];
        byteBuffers[0]=ByteBuffer.allocate(5);
        byteBuffers[1]=ByteBuffer.allocate(3);
        SocketChannel socketChannel = serverSocketChannel.accept();
        while (true){
            int max=8;
            int readLine=0;
            while (readLine<max){
                Long read=socketChannel.read(byteBuffers);
                readLine+=read;
                System.out.println("readLine"+readLine);
                Arrays.asList(byteBuffers).stream()
                        .map(buffer -> "p:" + buffer.position() + "limit" + buffer.limit())
                        .forEach(System.out::println);


                Arrays.asList(byteBuffers).stream().forEach(buffer->buffer.flip());
            }
            int writeLine=0;
            while (writeLine<max){
                Long write=socketChannel.write(byteBuffers);
                writeLine+=write;
            }
            Arrays.asList(byteBuffers).stream().forEach(buffer->buffer.clear());
            System.out.println("readLine:"+readLine+"writeLine:"+writeLine);
        }
    }
}
