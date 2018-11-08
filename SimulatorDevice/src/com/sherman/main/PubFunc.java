package com.sherman.main;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PubFunc {

	public static String genNewCookie() {
		/* 创建新的COOKIE */
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String strCookie = df.format(new Date());
		return strCookie;
	}

	public static String genCurTime() {
		/* 创建新的COOKIE */
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String strTime = df.format(new Date());
		return strTime;
	}
	
	// 随机数从0开始，最大imax，但不包括imax；即： [0, imax)
	public static int genRandom(int imax) {
		Random rand = new Random();
		int value = rand.nextInt(imax);
		return value;
	}
	
	public static void DebugLog(String strInfo)
	{
		Date date =  new Date();
		String strLogFileName = PubDefine.LOG_FILENAME;
		
		try {
			boolean bAppend = true;
			FileWriter fw = new FileWriter(strLogFileName, bAppend); //  throw IOException
			PrintWriter pw = new PrintWriter(fw);
			String strTime = String.format("%tF %tT", date, date);	
			String strLine = String.format("%s %s\r\n", strTime, strInfo);
			pw.write(strLine);
			pw.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
