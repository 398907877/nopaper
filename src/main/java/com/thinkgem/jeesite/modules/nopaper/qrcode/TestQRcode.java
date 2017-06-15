package com.thinkgem.jeesite.modules.nopaper.qrcode;


import java.io.File;

import java.io.FileNotFoundException;


public class TestQRcode {
	public static void main(String[] args) throws FileNotFoundException {
		
		QRCode  code=  QRCode.NEW("2017-05-09|88888888");
		
		code =  code.toFile(new  File("C:\\8888888\\TESTCODE.jpg"));


		
		System.out.println(222222);

		
//		
//	File  ff= new   File("D:\\8888888\\TESTCODE.jpg");
//		String code2 =QRCode.from(ff);
//		
//		System.out.println(code2);
		
	//	InputStream  in  = new  FileInputStream(ff);
	//OutputStream  out = new   FileOutputStream(ff);
		
		
		
	}

}
