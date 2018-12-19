package com.server;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.junit.Test;


/**
 * https://blog.csdn.net/acmman/article/details/80298062
 * @author xxq_1
 *
 */
public class Server {

	public static void main(String[] args) {
		//1、创建一个Netty服务类
		
		/**
		 * ServerBootstrap 是一个启动NIO服务的辅助启动类
		 * 你可以在这个服务中直接使用Channel
		 * */
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		
		//2、创建俩个线程池
		/**
		 *第一个经常被叫做'boss'，用来接受进来的连接
		 *第二个经常被叫做'worker'，用来处理已经被接受的连接
		 *一旦'boss'接收到连接，就会把连接信息注册到'worker'上。 
		 **/
		ExecutorService boss = Executors.newCachedThreadPool();
		ExecutorService worker = Executors.newCachedThreadPool();
		
		//3.为服务类设置一个NioSocket工厂
		bootstrap.setFactory(new NioServerSocketChannelFactory(boss,worker));
		
		//4.设置管道的工厂（匿名内部类实现）
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				// TODO Auto-generated method stub
				ChannelPipeline pipeline = org.jboss.netty.channel.Channels.pipeline();
				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("encoder", new StringEncoder());
				//设置一个处理服务端消息和各种消息事件的类（Handler）
				pipeline.addLast("hellohandler", new HelloHandler());
				return pipeline;
			}
		});
		
		//5.为服务端设置一个端口
		Channel channel = bootstrap.bind(new InetSocketAddress(10010));
		
		System.out.println("server start!");
		//获取控制台输入对象
				Scanner scanner = new Scanner(System.in);
					//向服务端写入控制台中输入的信息
					//获取控制台输入对象
					while(true) {
						//向服务端写入控制台中输入的信息
						System.out.println("请输入：");
						channel.write(scanner.next());
				}
	}
}
