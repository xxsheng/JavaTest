/**
 * 
 */
package com.xxq.springmvc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * 序列化反序列化工具类
 * @author Olympus_Pactera
 *
 */
public class SerializeUtil {
	
	/**
	 * 对象序列化
	 * @param obj
	 * @return
	 */
	public static byte[] seriaJlize(Object obj) {
		ObjectOutputStream oos =null;
		ByteArrayOutputStream baos = null;
		
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			byte [] bytes = baos.toByteArray();
			
			baos.close();
			oos.close();
			
			return bytes;
		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
		}
		
		return null;
	}
	
	
	/**
	 * 对象反序列化
	 * @param bytes
	 * @return
	 */
	public static Object deserialize(byte[] bytes) {
		ObjectInputStream ois = null;
		ByteArrayInputStream bais = null;
		
		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			
			bais.close();
			ois.close();
			
			return ois.readObject();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}
}
