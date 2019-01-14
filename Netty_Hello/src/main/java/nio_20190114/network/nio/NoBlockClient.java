package nio_20190114.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class NoBlockClient {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6666));
		
		socketChannel.configureBlocking(false);
		
		Selector selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_READ);
		
		FileChannel fileChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		while(fileChannel.read(byteBuffer) != -1) {
			byteBuffer.flip();
			socketChannel.write(byteBuffer);
			byteBuffer.clear();
		}
		
		while(selector.select()>0) {
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			
			while(iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				if(selectionKey.isReadable()) {
					SocketChannel client = (SocketChannel) selectionKey.channel();
					
					ByteBuffer resBuf = ByteBuffer.allocate(1024);
					int readByte;
					while((readByte = client.read(resBuf))>0) {
						System.out.println(new String(resBuf.array(),0,readByte));
					}
				}
				iterator.remove();
			}
		}
		
		//socketChannel.close();
		//fileChannel.close();
		
	}

}
