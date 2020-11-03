package com.hp.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class NettyServerHandler extends ChannelInboundHandlerAdapter{  
    
    
    private int counter;  
      
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
          
        String body = (String)msg;  
        System.out.println("接受的数据是: " + body + ";条数是: " + ++counter);  
    }  
      
      
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        cause.printStackTrace();  
        ctx.close();  
    }  
  
}  
