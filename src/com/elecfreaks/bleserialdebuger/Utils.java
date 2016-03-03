package com.elecfreaks.bleserialdebuger;

import java.io.ByteArrayOutputStream;

public class Utils {
	// 转化字符串为十六进制编码 
	public static String toHexString(String s) { 
		String str=""; 
		for (int i=0;i<s.length();i++){ 
			int ch = (int)s.charAt(i); 
			String s4 = Integer.toHexString(ch); 
			str = str + s4; 
		} 
		return str; 
	} 
	// 转化十六进制编码为字符串 
	public static String toStringHex(String s) { 
		byte[] baKeyword = new byte[s.length()/2]; 
		for(int i = 0; i < baKeyword.length; i++){ 
			try { 
				baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16)); 
			}catch(Exception e){ 
				e.printStackTrace(); 
			} 
		} 
		try { 
			s = new String(baKeyword, "utf-8");//UTF-16le:Not 
		} catch (Exception e1){ 
			e1.printStackTrace(); 
		} 
		return s; 
	} 

	/* 
	* 16进制数字字符集 
	*/ 
	private static String hexString="0123456789ABCDEF"; 
	/* 
	* 将字符串编码成16进制数字,适用于所有字符（包括中文） 
	*/ 
	public static String encodeHexString(String str) { 
		//根据默认编码获取字节数组 
		byte[] bytes=str.getBytes(); 
		StringBuilder sb=new StringBuilder(bytes.length*2); 
		//将字节数组中每个字节拆解成2位16进制整数 
		for(int i=0;i<bytes.length;i++){ 
			sb.append(hexString.charAt((bytes[i]&0xf0)>>4)); 
			sb.append(hexString.charAt((bytes[i]&0x0f)>>0)); 
		} 
		return sb.toString(); 
	} 
	/* 
	* 将16进制数字解码成字符串,适用于所有字符（包括中文） 
	*/ 
	public static String decodeHexString(String bytes) { 
		ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2); 
		//将每2位16进制整数组装成一个字节 
		for(int i=0;i<bytes.length();i+=2)
			baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1)))); 
		return new String(baos.toByteArray()); 
	}
	
	public static StringBuffer hexStringToAsciiString(String hexString){
		StringBuffer recString = new StringBuffer();
		byte bytes[] = hexStringToByteArray(hexString);
		if(bytes != null && bytes.length > 0)
			recString.append(new String(bytes));
		return recString;
	}
	
	public static byte[] hexStringToByteArray(String hexString){
		String strs[] = hexString.split(" ");
		byte bytes[] = new byte[strs.length];
		for(int i=0; i<strs.length; i++){
			if(strs[i] != null)
				bytes[i] = (byte) parseInt(strs[i], 16);
			else
				bytes[i] = 0;
		}
		return bytes;
	}
	
	public static int parseInt(String str, int radix){
		int val = 0;
		byte[] bytes = str.getBytes();
		
		for(int i=bytes.length-1; i>=0; i--){
			if(radix <= 10){
				if(bytes[i] >= '0' && bytes[i] < ('0'+radix))
					val += ((bytes[i]-'0') * Math.pow(radix, bytes.length-1-i));
			}else if(radix == 16){
				if(bytes[i] >= '0' && bytes[i] <= '9')
					val += ((bytes[i]-'0') * Math.pow(radix, bytes.length-1-i));
				else if(bytes[i] >= 'A' && bytes[i] <= 'F')
					val += ((bytes[i]-'A'+10) * Math.pow(radix, bytes.length-1-i));
				else if(bytes[i] >= 'a' && bytes[i] <= 'f')
					val += ((bytes[i]-'a'+10) * Math.pow(radix, bytes.length-1-i));
			}
		}
		
		return val;
	}
}
