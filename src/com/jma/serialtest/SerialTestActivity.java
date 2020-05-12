package com.jma.serialtest;

import com.jma.serialtest.SerialController.SerialCallBack;
import com.jma.serialtest.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SerialTestActivity extends Activity implements SerialCallBack
{
	private final String TAG = "SerialTestActivity";
	private SerialController serialController;
	private Button btnSend, btnStatus;
	private TextView tvShow;
	private static final int UPDATE_VIEW = 1;
	private static final int MSG_CHECK_STRING = 2;
	private static final int MSG_SEND_STRING = 3;
	private static final int DELAY_TIME = 500;
	private String data;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_main_serical_test);
		serialController = SerialController
				.getInstance(getApplicationContext());
		initView();
	}

	private void initView()
	{
		btnSend = (Button) findViewById(R.id.btn_send);
		tvShow = (TextView) findViewById(R.id.tv_receiver_show);
		btnStatus = (Button) findViewById(R.id.btn_ststus);
		serialController.setSerialCallBack(this);
		btnSend.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				tvShow.setText("");
				data = "";
				serialController.resetData();
				serialController.sendData(SerialController.sTest);
			}
		});
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "onDestroy");

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume");
		serialController.openPort();
		serialController.startReadData();
		handler.sendEmptyMessageDelayed(MSG_SEND_STRING, DELAY_TIME / 10);
		handler.sendEmptyMessageDelayed(MSG_CHECK_STRING, DELAY_TIME);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		Log.d(TAG, "onStop");
		handler.removeMessages(MSG_CHECK_STRING);
		serialController.stopReadThread();
		serialController.closePort();
	}

	@Override
	public void onSerialReceiver(String data)
	{
		Log.d(TAG, "onSerialReceiver->data->" + data);
		this.data = data;
		handler.sendMessage(handler.obtainMessage(UPDATE_VIEW, data));

	}

	private Handler handler = new Handler(new Handler.Callback()
	{

		@Override
		public boolean handleMessage(Message arg0)
		{
			switch (arg0.what)
			{
				case UPDATE_VIEW:
					tvShow.setText((String) arg0.obj);
					break;
				case MSG_CHECK_STRING:
					if (SerialController.sTest.equals(data))
					{
						btnStatus.setText(getResources().getString(
								R.string.success));
						btnStatus.setBackgroundColor(Color.GREEN);
					}
					else
					{
						btnStatus.setText(getResources().getString(
								R.string.fail));
						btnStatus.setBackgroundColor(Color.RED);
					}
					handler.sendEmptyMessageDelayed(MSG_CHECK_STRING,
							DELAY_TIME);
					break;
				case MSG_SEND_STRING:
					serialController.sendData(SerialController.sTest);
					break;
				default:
					break;
			}
			return false;
		}
	});

}
