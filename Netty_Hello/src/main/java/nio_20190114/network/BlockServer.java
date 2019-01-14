package nio_20190114.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BlockServer {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		
		FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		
		serverChannel.bind(new InetSocketAddress(6666));
		
		//获取客户端连接
		SocketChannel client = serverChannel.accept();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		while(client.read(buf) != -1) {
			buf.flip();
			outChannel.write(buf);
			buf.clear();
		}
		
		serverChannel.close();
		outChannel.close();
		client.close();
	}

}
