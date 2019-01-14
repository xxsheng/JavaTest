package nio_20190114.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BlockClient {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//2.发送一张图片给服务端
		FileChannel channel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		
		//1.获取通道
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6666));
		

		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		while(channel.read(buf) != -1) {
			buf.flip();
			socketChannel.write(buf);
			buf.clear();
		}
		
		//告诉服务器已经写完了
		socketChannel.shutdownInput();
		
		int len = 0;
		while((len = socketChannel.read(buf)) != -1) {
			//切换读模式
			buf.flip();
			System.out.println(new String(buf.array(),0,len));
			//切换写模式
			buf.clear();
		}
		
		socketChannel.close();
		channel.close();
		System.out.println("123");
		
	}

}
