package com.thinkgem.jeesite.modules.nopaper.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.mail.util.BASE64DecoderStream;

public class Test {
	
	public static void main(String[] args) throws JsonProcessingException {
		
		A  a = new A();
		a.setNum("1");
		a.setPicSource("2323232");
		a.setType("0");
		
		
        ObjectMapper  objectMapper = new ObjectMapper();  
        String sendpack= objectMapper.writeValueAsString(a);
        
        System.out.println(sendpack);
		
	}

}
