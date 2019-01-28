package javautils.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageCodeUtil {
	
	static Random r = new Random();
	
	static int width = 250;
	static int height = 100;
	static int line = 80;
	static int length = 4;
	static int fontSize = 84;

	public static void generate(String key, HttpServletRequest request, HttpServletResponse response) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics g = image.getGraphics();
		g.fillRect(0, 0, width, height);
		g.setFont(font());
		g.setColor(color(110, 133));
		for (int i = 0; i <= line; i++) {
			drawLine(g);
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(drawString(g, i * 60 + 10, 80));
		}
		g.dispose();
		request.getSession().setAttribute(key, sb.toString());
		write(image, response);
	}
	
	static void write(BufferedImage image, HttpServletResponse response) {
		response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        try {
        	ServletOutputStream out = response.getOutputStream();
    		ImageIO.write(image, "PNG", response.getOutputStream());
    		out.flush();
    		out.close();
		} catch (Exception e) {}
	}
	
	static String drawString(Graphics g, int x, int y) {
		Random r = new Random();
		g.setFont(font());
		int red = r.nextInt(101);
		int green = r.nextInt(111);
		int blue = r.nextInt(121);
		g.setColor(new Color(red, green, blue));
		//g.translate(r.nextInt(3), r.nextInt(3));
		String s = string();
		g.drawString(s, x, y);
		return s;
	}

	static void drawLine(Graphics g) {
		int x = r.nextInt(width);
		int y = r.nextInt(height);
		int xl = r.nextInt(13);
		int yl = r.nextInt(15);
		g.drawLine(x, y, x + xl, y + yl);
	}
	
	static char ch[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

	static Font font() {
		return new Font("Arial", Font.CENTER_BASELINE, 84);
	}

	static Color color(int fc, int bc) {
		if (fc > 255) fc = 255;
		if (bc > 255) bc = 255;
		int red = fc + r.nextInt(bc - fc - 16);
		int green = fc + r.nextInt(bc - fc - 14);
		int blue = fc + r.nextInt(bc - fc - 18);
		return new Color(red, green, blue);
	}
	
	static String string() {
		int index = r.nextInt(ch.length);
		return String.valueOf(ch[index]);
	}
	
}