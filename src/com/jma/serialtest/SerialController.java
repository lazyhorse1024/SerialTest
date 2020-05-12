package com.jma.serialtest;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.content.Context;
import android.hardware.SerialManager;
import android.hardware.SerialPort;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class SerialController
{
	private final String TAG = "SerialController";
	private static final String DEV_PORT = "/dev/ttyS7";
	private static int PORT_RATE = 19200;
	private static SerialController serialController;
	private static String SERIAL_SERVICE = "serial";
	private SerialManager serialManager;
	private SerialPort serialPort;
	public static final String sTest = "skypine serialtest abcdefghijklmn";
	private SerialCallBack mSerialCallBack;
	private boolean isStopThread = false;
	private String sReceiver = "";

	private Handler mHandler;

	public static synchronized SerialController getInstance(Context context)
	{
		if (serialController == null)
		{
			serialController = new SerialController(context);
		}
		return serialController;
	}

	private SerialController(Context context)
	{
		serialManager = (SerialManager) context
				.getSystemService(SERIAL_SERVICE);
		HandlerThread thread = new HandlerThread("MyHandlerThread");
		thread.start();
		mHandler = new Handler(thread.getLooper());

	}

	public void setSerialCallBack(SerialCallBack serialCallBack)
	{
		mSerialCallBack = serialCallBack;
	}

	public void startReadData()
	{
		resetData();
		isStopThread = false;
		mHandler.post(mBackgroundRunnable);
	}

	Runnable mBackgroundRunnable = new Runnable()
	{
		final byte[] datas = new byte[1024];
		final ByteBuffer buffer = ByteBuffer.allocate(1024);

		@Override
		public void run()
		{
			while (!isStopThread)
			{
				if (serialPort != null)
				{
					int length = -1;
					buffer.clear();
					try
					{
						Log.d(TAG, "mReadThread->read start");
						length = serialPort.read(buffer);
						Log.d(TAG, "mReadThread->read end");
					}
					catch (IOException e)
					{
						length = -1;
						e.printStackTrace();
					}
					Log.d(TAG, "mReadThread->before length");
					if (length > 0)
					{
						Log.d(TAG, "mReadThread->data valid");
						buffer.get(datas, 0, length);
						String text = new String(datas, 0, length);
						sReceiver = sReceiver + text;
						mSerialCallBack.onSerialReceiver(sReceiver);
						Log.d(TAG, "chat: " + text);
						Log.d(TAG, "mReadThread->sleep start");
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						Log.d(TAG, "mReadThread->sleep end");
					}
				}
			}
		}
	};

	public void stopReadThread()
	{
		isStopThread = true;
		mHandler.removeCallbacks(mBackgroundRunnable);
		sendData("s");
	}

	public void resetData()
	{
		sReceiver = "";
	}

	public void sendData(String s)
	{
		if (serialPort != null)
		{
			Log.d(TAG, "sendData->" + s);
			byte[] sendData = s.getBytes();
			ByteBuffer outBuffer = ByteBuffer.wrap(sendData);
			try
			{
				serialPort.write(outBuffer, sendData.length);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void openPort()
	{
		if (serialManager != null)
		{
			Log.d(TAG, "openPort");
			try
			{
				serialPort = serialManager.openSerialPort(DEV_PORT, PORT_RATE);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void closePort()
	{
		if (serialPort != null)
		{
			Log.d(TAG, "closePort");
			try
			{
				serialPort.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public interface SerialCallBack
	{
		void onSerialReceiver(String data);
	}

}
