package com.client;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * https://blog.csdn.net/acmman/article/details/80298655
 * @author xxq_1
 *
 */
public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//1.创建客户端服务类
		//与服务端不同，用的是ClientBootstrap
		ClientBootstrap bootstrap = new ClientBootstrap();
		
		//2.创建俩个线程池
		/**
		 * 第一个经常被叫做boss，用来接收进来的连接
		 * 第二个经常被叫做worker，用来处理已经被接收的连接
		 * 一旦boss接收到连接，就会把连接信息注册到worker上
		 * */
		ExecutorService boss = Executors.newCachedThreadPool();
		ExecutorService worker = Executors.newCachedThreadPool();
		
		//3.为客户端设置一个NioSocket工厂
		//与服务器端不同，客户端用的是NioClientSocketChannelFactory
		bootstrap.setFactory(new NioClientSocketChannelFactory(boss,worker));
		
		//4.设置管道的工厂（内部类实现）
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				// TODO Auto-generated method stub
				ChannelPipeline piepeline = Channels.pipeline();
				//设置解码方式为String
				piepeline.addLast("decoder", new StringDecoder());
				piepeline.addLast("encoder", new StringEncoder());
				//设置一个处理服务器端消息和各种消息事件的类（Handler）
				piepeline.addLast("helloHandler", new ClientHelloHandler());
				return piepeline;
			}
		});
		//5.连接服务端
		ChannelFuture connect = bootstrap.connect(new InetSocketAddress("127.0.0.1", 10010));
		@SuppressWarnings("unused")
		Channel channel = connect.getChannel();
		
//		//获取控制台输入对象
//		Scanner scanner = new Scanner(System.in);
//		while(true) {
//			//向服务端写入控制台中输入的信息
//			System.out.println("请输入：");
//			channel.write(scanner.next());
//		}
	}

}
