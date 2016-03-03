package com.elecfreaks.bleserialdebuger;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elecfreaks.ble.BluetoothHandler;
import com.elecfreaks.ble.BluetoothHandler.OnConnectedListener;
import com.elecfreaks.ble.BluetoothHandler.OnRecievedDataListener;

public class MainActivity extends Activity {
	private Button scanButton;
	
	private BluetoothHandler bluetoothHandler;
	private BluetoothDevice connectedDevice;
	private AlertDialog bleScanDialog;
	private boolean isConnected;
	
	private EditText editTextInput = null;
	private TextView textViewReceive = null;
	private TextView textViewCount = null;
	private byte[] WriteBytes = null;
	private StringBuffer recString;
	private StringBuffer sendString;
	private int mRecvCount = 0;
	private int mSendCount = 0;
	private String deviceName;
	
	private CheckBox receiveCheckBox;
	private CheckBox sendCheckBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		scanButton = (Button) findViewById(R.id.scanButton);
		receiveCheckBox = (CheckBox) findViewById(R.id.receiveCheckBox);
		sendCheckBox = (CheckBox) findViewById(R.id.sendCheckBox);
		
		bluetoothHandler = new BluetoothHandler(this);
		bluetoothHandler.setOnConnectedListener(new OnConnectedListener() {
			
			@Override
			public void onConnected(boolean isConnected) {
				// TODO Auto-generated method stub
				setConnectStatus(isConnected);
			}
		});
		bluetoothHandler.setOnRecievedDataListener(new OnRecievedDataListener() {
			
			@Override
			public void onRecievedData(byte[] bytes) {
				// TODO Auto-generated method stub
				mRecvCount += bytes.length;
				if(receiveCheckBox.isChecked()){
					for(byte b:bytes){
						String tempString = String.format("%02X ", b);
						recString.append(tempString);
					}
				}else{
					recString.append(new String(bytes));
				}
				
				if(recString.length() > 1000)
					recString = new StringBuffer();
				handler.sendEmptyMessage(0);
			}
		});
		
		editTextInput = (EditText) findViewById(R.id.editTextInput);
		textViewReceive = (TextView) findViewById(R.id.textViewReceive);
		textViewCount = (TextView) findViewById(R.id.textViewCount);
		textViewReceive.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		getActionBar().setTitle("disconnected");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        recString = new StringBuffer();
        
        textViewCount.setText("RX:"+mRecvCount+"    TX:"+mSendCount);
        
        receiveCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton view, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					byte[] bytes = textViewReceive.getText().toString().getBytes();
					recString = new StringBuffer();
					for(byte b:bytes){
						String tempString = String.format("%02X ", b);
						recString.append(tempString);
					}
					textViewReceive.setText(recString);
				}else{
					String tempString = textViewReceive.getText().toString();
					if(tempString != null && !tempString.equals(""))
						recString = Utils.hexStringToAsciiString(tempString);
					textViewReceive.setText(recString);
					recString = new StringBuffer(recString);
				}
			}
		});
        
        sendCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton view, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					byte[] bytes = editTextInput.getText().toString().getBytes();
					sendString = new StringBuffer();
					for(byte b:bytes){
						String tempString = String.format("%02X ", b);
						sendString.append(tempString);
					}
					editTextInput.setText(sendString);
				}else{
					String tempString = editTextInput.getText().toString();
					if(tempString != null && !tempString.equals(""))
						sendString = Utils.hexStringToAsciiString(tempString);
					editTextInput.setText(sendString);
					recString = new StringBuffer(sendString);
				}
			}
		});
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			textViewCount.setText("RX:"+mRecvCount+"    TX:"+mSendCount);
			textViewReceive.setText(recString);
		}
	};
	
	public void onButtonClear(View v){
		mRecvCount = 0;
		mSendCount = 0;
		recString = new StringBuffer();
		textViewCount.setText("RX:"+mRecvCount+"    TX:"+mSendCount);
		textViewReceive.setText("");
	}

	public void scanOnClick(final View v){
		if(!isConnected){
			bluetoothHandler.getDeviceListAdapter().clearDevice();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.device_list, null);
			
			ListView deviceListView = (ListView) view.findViewById(R.id.listViewDevice);
			deviceListView.setAdapter(bluetoothHandler.getDeviceListAdapter());
			
			deviceListView.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						connectedDevice = bluetoothHandler.getDeviceListAdapter().getItem(position).device;
						bleScanDialog.cancel();
						// connect
						bluetoothHandler.connect(connectedDevice.getAddress());
					}
			});
			
			builder.setView(view);
			bleScanDialog = builder.create();
			bleScanDialog.show();
			
			bluetoothHandler.scanLeDevice(true);	
		}else{
			setConnectStatus(false);
		}
	}
	
	public void onButtonSend(View v) {
		if(sendCheckBox.isChecked())
			WriteBytes = Utils.hexStringToByteArray(editTextInput.getText().toString());
		else
			WriteBytes = editTextInput.getText().toString().getBytes();
		// send
		bluetoothHandler.sendData(WriteBytes);
        mSendCount += WriteBytes.length;
        textViewCount.setText("RX:"+mRecvCount+"    TX:"+mSendCount);
	}
	
	public void setConnectStatus(boolean isConnected){
		this.isConnected = isConnected;
		if(isConnected){
			showMessage("Connection successful");
			getActionBar().setTitle(connectedDevice.getName());
			scanButton.setText("break");
		}else{
			bluetoothHandler.onPause();
    		bluetoothHandler.onDestroy();
    		scanButton.setText("scan");
		}
	}
	
	private void showMessage(String str){
		Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
	}
}
