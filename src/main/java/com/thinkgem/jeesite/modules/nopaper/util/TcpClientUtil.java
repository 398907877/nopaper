package com.thinkgem.jeesite.modules.nopaper.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.security.MessageDigest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TcpClientUtil {

    private String ip;

    private int port;

    private static Socket socket = null;

    private static int timeout = 50 * 1000;

 

    public TcpClientUtil(String ip, int port) {

       this.ip = ip;

       this.port = port;

    }

 
    public String receiveAndSend(SendMessage  send) throws IOException {
    	
    	String  back=null;

       InputStream input = null;

       OutputStream output = null;

 

       try {

           if (socket == null ||socket.isClosed() || !socket.isConnected()) {

              socket = new Socket();

              InetSocketAddress addr = new InetSocketAddress(ip, port);

              socket.connect(addr, timeout);

              socket.setSoTimeout(timeout);

              System.out.println("TcpKeepAliveClientnew ");

           }

 

           input = socket.getInputStream();

           output = socket.getOutputStream();

 

           // read body

           byte[] receiveBytes = {};// 收到的包字节数组
           
           
           ObjectMapper  objectMapper = new ObjectMapper();  
           String sendpack= objectMapper.writeValueAsString(send);
           
           
        
           
      
           //  base64
           
           
           sendpack= baseEncode(sendpack.getBytes());
          
           
           
           System.out.println("发送出去的数据"+sendpack);
           
           output.write(sendpack.getBytes(), 0, sendpack.getBytes().length);
           
           
           
           
           int ko=1;
           while (ko<=1) {

              if (input.available() > 0) {

                  receiveBytes = new byte[input.available()];

                  input.read(receiveBytes);
                  // send
                  
                  

                  System.out.println(" 接收到的数据 :" +new String(receiveBytes,"GBK"));
                  
                  back=new String(new String(receiveBytes,"GBK"));

                  output.flush();
          
                  ko++;
                  socket.close();
    
              }       
             

           }

 

       } catch (Exception e) {

           e.printStackTrace();

           System.out.println("TcpClientnew socket error");

       }
 return back;
    }

    
    /**
     * 
     * @param str转换 二进制
     * @return
     */
    public static String str2HexStr(String str)    
    {      
      
        char[] chars = "0123456789ABCDEF".toCharArray();      
        StringBuilder sb = new StringBuilder("");    
        byte[] bs = str.getBytes();      
        int bit;      
            
        for (int i = 0; i < bs.length; i++)    
        {      
            bit = (bs[i] & 0x0f0) >> 4;      
            sb.append(chars[bit]);      
            bit = bs[i] & 0x0f;      
            sb.append(chars[bit]);    
            sb.append(' ');    
        }      
        return sb.toString().trim();      
    }    
    
    /**
     * MD5加码。32位
     * @param inStr
     * @return
     */
    public static String MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
 
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
 
        byte[] md5Bytes = md5.digest(byteArray);
 
        StringBuffer hexValue = new StringBuffer();
 
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
 
        return hexValue.toString();
    }
 

    public static void main(String[] args) throws Exception {
    	
    	
    	SendMessage  send= new SendMessage("010101", "张三", "10", "350322198210021012", "ZHANGSAN", "1", "350738", "1", "CN", "05918888568", "13314541433", "gutianlu", "350001", "1", "666	", "110");
    	
    	
    	

       TcpClientUtil client = new TcpClientUtil("192.168.1.12", 9190);

       System.out.println("返回的数据：：："+client.receiveAndSend(send));
       
       ObjectMapper mapper= new ObjectMapper();
       
       GetMessage  get =mapper.readValue(client.receiveAndSend(send), GetMessage.class);
       System.out.println("backmessage="+get.getXYM());
       
       
 
//
//    	String  test="吴佳俊uwiu";
//    
//    	 String back= baseEncode(	test.getBytes());
//    	 
//    	 System.out.println(back);
//    	 
//    	 
//    	 
//    	byte[]  bytessss=  decode("5ZC05L2z5L+KdXdpdQ==");
//    	
//    	
//    	String hah = new String(bytessss);
//    	
//    	System.out.println(hah);
//    	
    }
    
    
    /**  
     * 编码  
     * @param bstr  
     * @return String  
     */    
    public static String baseEncode(byte[] bstr){    
    return new sun.misc.BASE64Encoder().encode(bstr);    
    }    
    
    /**  
     * 解码  
     * @param str  
     * @return string  
     */    
    public static byte[] decode(String str){    
    byte[] bt = null;    
    try {    
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();    
        bt = decoder.decodeBuffer( str );    
    } catch (IOException e) {    
        e.printStackTrace();    
    }    
    
        return bt;    
    }    

 

}
