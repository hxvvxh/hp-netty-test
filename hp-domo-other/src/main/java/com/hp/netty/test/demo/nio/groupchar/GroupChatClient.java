package com.hp.netty.test.demo.nio.groupchar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author hp
 * @version 1.0
 * @date 2020/12/28 21:57
 */
public class GroupChatClient {
    private static final String HOST="127.0.0.1";
    private static final int PORT=6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public GroupChatClient() {
        try {
            selector=Selector.open();
            socketChannel=SocketChannel.open(new InetSocketAddress(HOST,6667));
            //设置非阻塞
            socketChannel.configureBlocking(false);
            //注册
            socketChannel.register(selector, SelectionKey.OP_READ);
            //得到username
            username=socketChannel.getLocalAddress().toString();
            System.out.println(username+"is ok....");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器发送消息
     * @param info
     */
    public void sendInfo(String info) {
        info = username + " 说：" + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 服务消息
     */
    public void readInfo(){
        try {
            int readChannels=selector.select(2000);
            if (readChannels > 0) {//有可以用的通道
                //遍历selector
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                if (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    //获取可读的渠道
                    if (key.isReadable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //读取渠道中的数据
                        channel.read(buffer);
                        //转换消息
                        String msg=new String(buffer.array());
                        System.out.println("服务器传来消息"+msg.trim());
                    }
                }
                iterator.remove();
            }else {
//                System.out.println("没有可用的通道......");
            }
        }catch (Exception e){
            
        }
    }

    public static void main(String[] args) {
        //启动客户端
        GroupChatClient chatClient=new GroupChatClient();

        //启动一下线程
        new Thread(()->{
           while (true){
               chatClient.readInfo();
               try {
                   TimeUnit.SECONDS.sleep(3);
               }catch (Exception e){
                   e.printStackTrace();
               }

           }
        }).start();

        //发送给服务器信息
        Scanner scanner=new Scanner(System.in);

        while (scanner.hasNextInt()){
            String s=scanner.nextLine();
            chatClient.sendInfo(s);
        }

    }
}
