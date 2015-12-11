package com.suwonsmartapp.chatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChatClient {
//	private final static String SERVER_HOST = "192.168.0.222";
	private final static String SERVER_HOST = "localhost";
	private final static int SERVER_PORT = 5000;
	
	private Socket mSocket;
	
	public static void main(String[] args) {
		new ChatClient().connect();
	}
	
	public void connect() {
		try {
			mSocket = new Socket(SERVER_HOST, SERVER_PORT);
			
			new ClientReciver(mSocket, "닉네임").start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ClientWriter extends Thread {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
		}
	}
	
	class ClientReciver extends Thread {
		
		private DataInputStream mInputStream;
		private DataOutputStream mOutputStream;
		
		public ClientReciver(Socket socket, String nickName) {
			try {
				mInputStream = new DataInputStream(socket.getInputStream());
				mOutputStream = new DataOutputStream(socket.getOutputStream());
				
				mOutputStream.writeUTF(nickName);
				mOutputStream.flush();

				System.out.println("id : " + nickName + "접속 완료");
				
				while (true) {
					System.out.print("메세지 입력 : ");
					Scanner in = new Scanner(System.in);
					
					// Json구성
					String msg = in.nextLine();
					long time = System.currentTimeMillis();
					
					MsgInfo msgInfo = new MsgInfo(nickName, msg, time);
					Gson gson = new Gson();
					
					
//					String json = "{\"nickName\":\"" + nickName + "\",\"msg\":\"" + msg + "\",\"time\":\"" + time + "\"}";
					
					mOutputStream.writeUTF(gson.toJson(msgInfo));
					mOutputStream.flush();
				}
				
//				try {
//					Thread.sleep(5000);
//					mOutputStream.writeUTF("exit");
//					mOutputStream.flush();
//					System.out.println("id : " + nickName + "접속 종료");
//					System.exit(0);
//				} catch (InterruptedException e) {
//				}
				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("writeUTF IOException");
			}
		}
		
		@Override
		public void run() {
			try {
				// 계속 듣기만
				while (mInputStream != null) {
					// json 파싱
					Gson gson = new Gson();
					String json = mInputStream.readUTF();
					MsgInfo msgInfo = gson.fromJson(json, MsgInfo.class); 
					System.out.println(msgInfo.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// 접속 종료시	
				mSocket = null;
				
			}	
		}
	}
}
