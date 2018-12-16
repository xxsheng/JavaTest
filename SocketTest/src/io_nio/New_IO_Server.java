package io_nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class New_IO_Server {

	//通信通道
	private Selector selector;
	
	/**
	 * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
	 * @param port
	 * @throws IOException
	 */
	public void initServer(int port) throws IOException {
		//获得一个ServerSocket通道
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		
		//设置通道为非阻塞
		serverChannel.configureBlocking(false);
		
		//将通道对应的ServerSocket绑定到port端口
		serverChannel.socket().bind(new InetSocketAddress(port));
		
		//获得一个通道管理器
		this.selector = Selector.open();
		
		//将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，注册该事件后，
		//当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public void listen() throws IOException {
		System.out.println("服务端启动");
		//轮询方式访问selector
		while(true) {
			//当注册的事件到达时，方法返回，否则，该方法会一直阻塞。
			selector.select();
			//获得selector中选中的项的迭代器，选中的项为注册的事件
			Iterator<?> ite = this.selector.selectedKeys().iterator();
			while(ite.hasNext()) {
				SelectionKey key = (SelectionKey) ite.next();
				//删除已选的key，以防重复处理
				ite.remove();
				handler(key);
			}
		}
	}

	/**
	 * 处理请求
	 * @param key
	 * @throws IOException 
	 */
	public void handler(SelectionKey key) throws IOException {
		// TODO Auto-generated method stub
		if(key.isAcceptable()){
			handlerAccept(key);//客户端链接请求
		}else if(key.isReadable()) {
			handlerRead(key);//获得了可读事件
		}
	}

	/**
	 * 处理链接请求
	 * @param key
	 * @throws IOException
	 */
	public void handlerAccept(SelectionKey key) throws IOException {
		// TODO Auto-generated method stub
		//获得ServerSocket
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		//获得和客户端连接的通道
		SocketChannel channel = server.accept();
		//设置成非阻塞
		channel.configureBlocking(false);
		
		System.out.println("新的客户端链接");
		//服务端发给客户端的确认信息
		channel.write(ByteBuffer.wrap("服务端成功创建链接".getBytes()));
		//在和客户端链接成功后，为了可以接收到客户端的信息，需要给通道设置读的权限
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	/**
	 * 处理可读的事件
	 * @param key
	 * @throws IOException
	 */
	public void handlerRead(SelectionKey key) throws IOException {
		// TODO Auto-generated method stub
		//得到事件发生的socket通道
		SocketChannel channel = (SocketChannel) key.channel();
		//创建读取的缓冲区（每次读1000个字节）
		ByteBuffer buffer = ByteBuffer.allocate(1000);
		channel.read(buffer);
		byte [] data = buffer.array();
		String msg = new String(data).trim();
		System.out.println("服务端收到消息"+msg);
		
		ByteBuffer outBuffer = ByteBuffer.wrap(("服务端收到消息："+msg).getBytes());
		channel.write(outBuffer);//将消息送回客户端
	}
	
	/**
	 * 启动服务测试
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		New_IO_Server nio = new New_IO_Server();
		nio.initServer(10010);
		nio.listen();
	}
}
