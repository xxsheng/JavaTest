package ioOld;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Old_IO_Server {

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(10010);
			System.out.println("服务端启动");
			
			while(true) {
				final Socket socket = server.accept(); //服务端在等待客户端链接，当连接上后会分配一个线程，多个客户端会产生多个实例 [阻塞]
				System.out.println("出现一个新客户端");
				
				//业务处理
				handler(socket);
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
