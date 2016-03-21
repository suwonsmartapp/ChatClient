package com.suwonsmartapp.chatclient;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {
	private final static String SERVER_HOST = "192.168.0.239";
	//	private final static String SERVER_HOST = "localhost";
	private final static int SERVER_PORT = 5000;

	private Socket mSocket;
	private MsgInfo msgInfo;
	private String mName;

	public static void main(String[] args) {
		new ChatClient().connect();
	}

	public void connect() {
		try {
			mSocket = new Socket(SERVER_HOST, SERVER_PORT);
			ClientWrite clientWrite = new ClientWrite("최동해");
			clientWrite.start();
			ClientRead clientRead = new ClientRead();
			clientRead.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	class ClientRead extends Thread {
		private DataInputStream mInputStream;

		@Override
		public void run() {
			try {
				mInputStream = new DataInputStream(mSocket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			super.run();
			try {
				// 계속 듣기만
				while (mInputStream != null) {
					// json 파싱
					String json = mInputStream.readUTF();
					try {
						MsgInfo msgInfo = new Gson().fromJson(json, MsgInfo.class);
						System.out.println(msgInfo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// 접속 종료시
				mSocket = null;
			}
		}
	}

	class ClientWrite extends Thread {

		private DataOutputStream mOutputStream;

		public ClientWrite(String nickName) {
			try {
				mName = nickName;
				mOutputStream = new DataOutputStream(mSocket.getOutputStream());
				mOutputStream.writeUTF(nickName);
				mOutputStream.flush();
				System.out.println("id : " + nickName + "접속 완료");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("writeUTF IOException");
			}
		}

		@Override
		public void run() {
			Scanner in = new Scanner(System.in);

			while (true) {
				System.out.print("메세지 입력 : ");
				// Json구성
				String msg = in.nextLine();
				long time = System.currentTimeMillis();
				msgInfo = new MsgInfo(mName, msg, time);

				Gson gson = new Gson();
//					String json = "{\"nickName\":\"" + nickName + "\",\"msg\":\"" + msg + "\",\"time\":\"" + time + "\"}";
				try {
					mOutputStream.writeUTF(gson.toJson(msgInfo));
//                    System.out.println(gson.toJson(msgInfo));
					mOutputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
}
