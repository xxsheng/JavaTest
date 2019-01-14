package nio_20190114.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class NoBlockServer {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ServerSocketChannel serverChannle = ServerSocketChannel.open();
		serverChannle.configureBlocking(false);
		serverChannle.bind(new InetSocketAddress(6666));
		
		Selector selector = Selector.open();
		
		serverChannle.register(selector, SelectionKey.OP_ACCEPT);
		
		while(selector.select()>0) {
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while(iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				
				if(selectionKey.isAcceptable()) {
					SocketChannel client = serverChannle.accept();
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ);
					//client.close();
				}else if(selectionKey.isReadable()) {
					SocketChannel client = (SocketChannel) selectionKey.channel();
					ByteBuffer buf = ByteBuffer.allocate(1024);
					FileChannel fileChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
					while(client.read(buf)>0) {
						buf.flip();
						fileChannel.write(buf);
						buf.clear();
					}
					
					ByteBuffer writebuf = ByteBuffer.allocate(1024);
					writebuf.put("image is success get".getBytes());
					writebuf.flip();
					client.write(writebuf);
					
					//client.close();
					//fileChannel.close();
				}
				
				iterator.remove();
			}
		}
		

	}

}
