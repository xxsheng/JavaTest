package ioOld;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Old_IO_Server_ALL {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//创建一个缓存线程池
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		
		try {
			//创建socket服务，监听10010端口
			ServerSocket server = new ServerSocket(10010);
			System.out.println("服务端启动");
			
			while(true) {
				//获取一个套接字（阻塞）
				final Socket socket = server.accept();
				System.out.println("出现一个新客户端");
				
				//在线程池为新客户端开启一个线程
				newCachedThreadPool.execute(new Runnable() {
					
					@Override
					public void run() {
						
						//业务处理
						handler(socket);
					}
				});
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 读取数据
	 * @param socket
	 */
	private static void handler(Socket socket) {
		// TODO Auto-generated method stub
		byte [] bytes = new byte [1024];
		try {
			InputStream input = socket.getInputStream();
			
			int read = 0;
			while(read !=-1) {
				read = input.read(bytes);
				System.out.println(new String(bytes,0,read));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			System.out.println("socket 关闭");
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
