package com.thinkgem.jeesite.modules.nopaper.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64ToPic {
	
	


 /**
  * 将文件转成base64 字符串
  * @param path文件路径
  * @return  * 
  * @throws Exception
  */

 public static String encodeBase64File(String path) throws Exception {
  File file = new File(path);
  FileInputStream inputFile = new FileInputStream(file);
  byte[] buffer = new byte[(int) file.length()];
  inputFile.read(buffer);
  inputFile.close();
  return new BASE64Encoder().encode(buffer);

 }

 /**
  * 将base64字符解码保存文件
  * @param base64Code
  * @param targetPath
  * @throws Exception
  */

 public static void decoderBase64File(String base64Code, String targetPath)
   throws Exception {
  byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
  FileOutputStream out = new FileOutputStream(targetPath);
  out.write(buffer);
  out.close();

 }

 /**
  * 将base64字符保存文本文件
  * @param base64Code
  * @param targetPath
  * @throws Exception
  */

 public static void toFile(String base64Code, String targetPath)
   throws Exception {

  byte[] buffer = base64Code.getBytes();
  FileOutputStream out = new FileOutputStream(targetPath);
  out.write(buffer);
  out.close();
 }

 
 public static String txt2String(File file){
     StringBuilder result = new StringBuilder();
     try{
         BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
         String s = null;
         while((s = br.readLine())!=null){//使用readLine方法，一次读一行
             result.append("\r\n"+s);
         }
         br.close();    
     }catch(Exception e){
         e.printStackTrace();
     }
     return result.toString();
 }
 
 public static void main(String[] args){
     File file = new File("C:\\999999999999999999999999\\encode64.txt");
     System.out.println(txt2String(file));
     
     GenerateImage(txt2String(file));
     
 }
 
 
 public static boolean GenerateImage(String imgStr)  
 {   //对字节数组字符串进行Base64解码并生成图片  
     if (imgStr == null) //图像数据为空  
         return false;  
     BASE64Decoder decoder = new BASE64Decoder();  
     try   
     {  
         //Base64解码  
         byte[] b = decoder.decodeBuffer(imgStr);  
         for(int i=0;i<b.length;++i)  
         {  
             if(b[i]<0)  
             {//调整异常数据  
                 b[i]+=256;  
             }  
         }  
         //生成jpeg图片  
         String imgFilePath = "C:\\999999999999999999999999\\encode641.jpg";//新生成的图片  
         OutputStream out = new FileOutputStream(imgFilePath);      
         out.write(b);  
         out.flush();  
         out.close();  
         return true;  
     }   
     catch (Exception e)   
     {  
         return false;  
     }  
 }  
 
 


}
