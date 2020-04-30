import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

public class multithread extends Thread {
	private Thread t;
	private String threadName;
	private int numofPlayer;
	private DataInputStream input;
	private DataOutputStream output;
	private String myName;
	private MainServer main;
	public Server myServer;

	public multithread(String string, DataInputStream dataInputStream, DataOutputStream dataOutputStream,int num, MainServer mainServer) {
		this.input = dataInputStream;
		this.output = dataOutputStream;
		this.myName = string;
		this.numofPlayer =num;
		this.main = mainServer;
	}

	public void run() {
		
		while(true) {
			String action = null;
			try {
				action = input.readUTF();
				System.out.println("Action: "+action+"Name "+myName);
				if (action.equals("getCurrentUsers")) {
					main.sentCurrentUsers(output);

				} else if (action.equals("startGame")) {
					int num = input.readInt();
					int[] players = new int[8];
					for(int i=0 ; i<num; i++) {
						players[i] = input.readInt();
					}
					main.startGame(num,players);
					/*
					if(myServer == null) {
						output.writeUTF("nowork");
					}
					else {
						System.out.println("true");
						output.writeUTF("work");
					}
					*/
							
				} else if (action.equals("closeGame")) {
					myServer.closeGame();
					main.gameRunning = false;

				} else if (action.equals("placeChar")) {
					int x = input.readInt();
					int y = input.readInt();
					char character = input.readChar();
					myServer.setChar(x,y,character);

				} else if (action.equals("placeWord")) {
					String word = input.readUTF();
					myServer.placeWord(word);
					
				} else if (action.equals("vote")) {
					int no = input.readInt();
					boolean ans = input.readBoolean();
					myServer.setVote(no,ans);

				} else if (action.equals("pass")) {
					myServer.nextPlayer();
				} else if (action.equals("disconnect")) {
					main.disconnect(numofPlayer);
					break;
				}	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				
				main.disconnect(numofPlayer);
				return;
			}
			

			
		}
			
	}

	

	public void setNo(int num) {
		this.numofPlayer =num;
	}
}
