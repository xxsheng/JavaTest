package nio_20190114;

import java.nio.ByteBuffer;

public class BufferTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		System.out.println(byteBuffer.limit());
		System.out.println(byteBuffer.position());
		System.out.println(byteBuffer.capacity());
		System.out.println(byteBuffer.mark());
		
		String s = "firework";
		byteBuffer.put(s.getBytes());
		
		System.out.println(byteBuffer.limit());
		System.out.println(byteBuffer.position());
		System.out.println(byteBuffer.capacity());
		System.out.println(byteBuffer.mark());
		
		//切换读模式
		byteBuffer.flip();
		
		System.out.println(byteBuffer.limit());
		System.out.println(byteBuffer.position());
		System.out.println(byteBuffer.capacity());
		System.out.println(byteBuffer.mark());
		
		byte [] byte1 = new byte[byteBuffer.limit()];
		byteBuffer.get(byte1);
		System.out.println(new String(byte1,0,byte1.length));
	}

}
