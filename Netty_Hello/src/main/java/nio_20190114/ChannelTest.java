package nio_20190114;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ChannelTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FileInputStream fis = null;
		try {
			fis = new FileInputStream("D:\\SQL server\\SQLServer2017-x64-CHS-Dev.iso");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileChannel fileChannel = fis.getChannel();
		
		try {
			FileChannel.open(Paths.get("D:\\SQL server\\SQLServer2017-x64-CHS-Dev.iso"), StandardOpenOption.WRITE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
