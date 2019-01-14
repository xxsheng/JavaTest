/**
 * 
 */
package nio_20190114;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olympus_Pactera
 *
 */
public class SimpleFileTransferTest {

	private long transferFile(File source, File des)throws IOException{
		long startTime = System.currentTimeMillis();
		
		if(!des.exists()) {
			des.createNewFile();
		}
		
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
		
		//将数据源读到的内容写入目的地--使用数组
		byte[] bytes = new byte[1024*1024];
		int len;
		try {
		
			while((len = bis.read(bytes))!=-1) {
				bos.write(bytes,0,len);
			}
		} finally {
			bis.close();
			bos.close();
		}
		
		
		long endTime = System.currentTimeMillis();
		return endTime-startTime;
	}
	
	private long transferFileWithNIO(File source, File des)throws IOException {
		long startTime = System.currentTimeMillis();
		
		if(!des.exists())
			des.createNewFile();
		RandomAccessFile read = new RandomAccessFile(source, "rw");
		RandomAccessFile write = new RandomAccessFile(des, "rw");
		
		FileChannel readChannel = read.getChannel();
		FileChannel writeChannel =write.getChannel();
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);
		while(readChannel.read(byteBuffer)>0) {
			byteBuffer.flip();
			writeChannel.write(byteBuffer);
			byteBuffer.clear();
		}
		
		writeChannel.close();
		readChannel.close();
		
		long endTime = System.currentTimeMillis();
		
		return endTime-startTime;
	}
	
	public static void main(String[] args) throws IOException {
		SimpleFileTransferTest simpleFileTransferTest = new SimpleFileTransferTest();
		File source = new File("D:\\SQL server\\SQLServer2017-x64-CHS-Dev.iso");
		File des = new File("D:\\SQL server\\io.avi");
		File nio = new File("D:\\SQL server\\nio.avi");
		
		System.out.println(simpleFileTransferTest.transferFile(source, des)+": 普通字节流时间");
		System.out.println(simpleFileTransferTest.transferFileWithNIO(source, nio)+ ": nio字节流时间");
		
	}
}
