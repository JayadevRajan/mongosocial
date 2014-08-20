package org.mongosocial.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ImageUtil
{
	public static byte[] loadImage(String filePath) throws Exception
	{
		File file = new File(filePath);
		int size = (int) file.length();
		byte[] buffer = new byte[size];
		FileInputStream in = new FileInputStream(file);
		in.read(buffer);
		in.close();
		return buffer;
	}
	
	public static void storeImage(byte[] b) throws Exception
	{
		FileOutputStream out = new FileOutputStream("pic1.jpg");
		System.out.println(b.length);
		System.out.println(b[1234123]);
		out.write(b);
		out.close();
	}
}
