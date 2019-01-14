package nio_20190114.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class TestPipe {

	public void test1() throws IOException{
		Pipe pipe = Pipe.open();
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		Pipe.SinkChannel sinkChannel = pipe.sink();
		byteBuffer.put("通过单向管道发送数据".getBytes());
		byteBuffer.flip();
		sinkChannel.write(byteBuffer);
		
		Pipe.SourceChannel sourceChannel = pipe.source();
		byteBuffer.flip();
		int len = sourceChannel.read(byteBuffer);
		System.out.println(new String(byteBuffer.array(),0,len));
	}
}
