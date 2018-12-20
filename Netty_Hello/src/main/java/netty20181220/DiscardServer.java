/**
 * 
 */
package netty20181220;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * http://www.cnblogs.com/applerosa/p/7141684.html
 * @author Olympus_Pactera
 *
 */
public class DiscardServer {

	private int port;

	public DiscardServer(int port) {
		super();
		this.port = port;
	}

	public void run() throws Exception {
		/**
		 * NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器
		 * Netty提供了许多不同的EventLoopGroup的实现用来处理不同传输协议，在这个例子中我们实现了一个服务端的应用，
		 * 因此会有俩个NioEventLoopGroup会被使用，第一个经常被叫做boss，用来接收进来的连接。
		 * 第二个被经常叫做worker，用来处理已经被接受的连接，一旦boss接收到连接，就会把连接信息注册到worker上。
		 * 如何知道多少个线程已经被使用，如何映射到已经创建的Channels上都需要依赖EventLoopGroup的实现， 并且可以通过构造函数来配置他们的关系。
		 */
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		System.out.println("准备运行端口：" + port);
		try {
			// ServerBootStrap 是一个启动NIO服务的辅助启动类，你可以在这个服务中直接使用Channel
			ServerBootstrap b = new ServerBootstrap();
			// 这一步是必须的，如果没有设置group将会报java.lang.IllegalStateException:group not set 异常
			b = b.group(bossGroup, workerGroup);
			// ServerSocketChannel以NIO的selector为基础进行实现的，用来接受新的连接
			// 这里告诉Channel如何获取新的连接
			b = b.channel(NioServerSocketChannel.class);
			
			/**
			 * 
			 * */
			b = b.childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					// TODO Auto-generated method stub
					ch.pipeline().addLast(new DiscardServerHandler());
				}
			});
			
			b = b.option(ChannelOption.SO_BACKLOG, 128);
			
			b = b.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			ChannelFuture f = b.bind(new InetSocketAddress(port)).sync();
			
			f.channel().closeFuture().sync();
		} finally {
			// TODO: handle finally clause
			//关闭
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
