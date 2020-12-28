package com.hp.netty.test.demo.nio.groupchar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author hp
 * @version 1.0
 * @date 2020/12/28 21:32
 */
public class GroupChatServer {

    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT=6667;

    public GroupChatServer() {
        try {
            //得到选择器
            selector=Selector.open();
            //初始化serverSocketCHannel
            listenChannel=ServerSocketChannel.open();
            //设置非阻塞
            listenChannel.configureBlocking(false);
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //注册到selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void listen(){
        try {
            while (true){
                //阻塞式获取事件
                int count=selector.select(2000);
                if (count>0){//有事件发生
                    //遍历事件集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        //拿到事件的SelectionKey集合
                        SelectionKey selectionKey=iterator.next();
                        //如果事件状态为：正在连接
                        if (selectionKey.isAcceptable()){//连接状态
                            //阻塞式获取渠道
                            SocketChannel sc = listenChannel.accept();
                            //设置非阻塞
                            sc.configureBlocking(false);
                            //将改sc 注册到selector中，并设置为上线状态
                            sc.register(selector,SelectionKey.OP_READ);
                            System.out.println(sc.getRemoteAddress()+"上线");
                        }
                        if (selectionKey.isReadable()){//可读状态
                            //TODO 处理读
                            readData(selectionKey);
                        }
                        //删除处理后的key状态(防止重复处理)
                        iterator.remove();
                    }
                }else {
//                    System.out.println("等待中......");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 读取客户端消息
     */
    private void readData(SelectionKey key){
        //定义一个socketchannel
        SocketChannel channel=null;

        try {
            //取到关联的channel
            channel= (SocketChannel) key.channel();

            //创建缓冲器
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count=channel.read(buffer);
            if (count>0){
                //转成字符串
                String read=new String(buffer.array());
                System.out.println("from 客户端："+read);
                //向其他客户端转发消息
                sendInfoOtherClients(read,channel);
            }

        }catch (Exception e){
            try {
                System.out.println(channel.getRemoteAddress()+"离线");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 客户端转发消息
     * @param msg
     * @param self
     */
    private void sendInfoOtherClients(String msg,
                                      SocketChannel self
                                      ){
        System.out.println("服务器转发消息中...");
        //所有注册到selector 的scoketchannel
        selector.keys().forEach(key -> {
            Channel targetChanel = key.channel();
            if (targetChanel instanceof SocketChannel &&
                targetChanel != self){
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                try {
                    //数据写入通道
                    ((SocketChannel) targetChanel).write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    public static void main(String[] args) {
        //创建服务器
        GroupChatServer server=new GroupChatServer();
        server.listen();
    }
}
