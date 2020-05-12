package com.skypine.serialtest;

import java.io.File;

import android.text.TextUtils;

public class CheckFileController
{
	private final static String FLAG_FILE = "/serialtest";

	private String mountPath;
	
	private static CheckFileController instance;

	public synchronized static CheckFileController getInstance()
	{
		if (instance == null)
		{
			instance = new CheckFileController();
		}
		return instance;
	}

	private CheckFileController()
	{
	}

	public void setMountPath(String mountPath)
	{
		this.mountPath = mountPath;
	}


	public boolean checkSerialTestFile()
	{
		boolean isFileExit = false;
		if (!TextUtils.isEmpty(mountPath)) 
		{
			File file = new File(mountPath + FLAG_FILE);
			isFileExit = file.exists();
		}
		return isFileExit;
	}
}
