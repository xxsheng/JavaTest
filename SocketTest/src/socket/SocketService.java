/**
 * 
 */
package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Olympus_Pactera
 *
 */
public class SocketService {

	public static void main(String[] args) {
		SocketService socketService  = new SocketService();
		socketService.onServer();
	}
	
	public void onServer() {
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(5200);
			System.out.println("Server start is OK");
			//创建一个ServerSocket在端口5200监听客户请求
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Socket socket = null;
		try {
			socket = server.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String line;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("Client:"+ in.readLine());
			
			line = br.readLine();
			
			while(!line.equals("end")) {
				write.println(line);;
				write.flush();
				System.out.println("Server:" + line);
				System.out.println("Client:" + in.readLine());
				line = br.readLine();
			}
			
			write.close();
			in.close();
			socket.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
