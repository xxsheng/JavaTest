package nio_20190114;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestChannelDemo2 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//分散读取
		FileInputStream fis = new FileInputStream("1.jpg");
		
		FileChannel channel1 = fis.getChannel();
		
		ByteBuffer buf1 = ByteBuffer.allocate(100);
		ByteBuffer buf2 = ByteBuffer.allocate(1024);
		
		ByteBuffer [] bufs = {buf1, buf2};
		channel1.read(bufs);
		
		for(ByteBuffer byteBuffer : bufs) {
			byteBuffer.flip();
		}
		
		System.out.println(new String(bufs[0].array(),0,bufs[0].limit()));
		System.out.println(new String(bufs[1].array(),0,bufs[1].limit()));
		
		//聚集写入
		RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
		FileChannel channel2 = raf2.getChannel();
		channel2.write(bufs);
	}

}
