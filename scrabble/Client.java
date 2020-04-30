import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//import com.sun.glass.events.WindowEvent;

public class Client extends Thread {

	private DataInputStream input;
	private DataOutputStream output;
	private int numPlayer; // the total number of a running Game
	private int myNo;	//my number in a running Game
	private String IP= "localhost";
	private int port = 1234;
	private Socket client;
	private String myName;	//my username
	private String[] playerName = new String[4]; // a list of username in a running Game
	private int currentPlayer; //who's turn is now 
	private int[] score = new int[100]; 
	public int numUser; // the number of all online players
	public String[] userPool = new String[12]; //all online players
	private Gui gui;
	
	
	public Client(Gui myGui) {
		this.gui=myGui;
		Arrays.fill(score, 0);
	}
	
	public Client(Gui myGui,String ip, int port) {
		this.IP = ip;
		this.port =  port;
		this.gui=myGui;
		Arrays.fill(score, 0);
		//login();
	}

	public int login() {
		System.out.println(IP);
		System.out.println(port);
		try {
			client = new Socket(IP, port);
			// client = new Socket(IP, port);
			input = new DataInputStream(client.getInputStream());
			output = new DataOutputStream(client.getOutputStream());
			output.writeUTF(myName);
			
			String action = input.readUTF();
			if(action.equals("notUnique")) {
				client.close();
				System.out.println("repeat name");
				return 1;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("connecting fail");
			return 2;
		}
		//getCurrentUsers();
		System.out.println("connecting established");
		return 3;
	}
	//update userpool
	public void getCurrentUsers() {
		System.out.println("getCurrentUsers");
		try {
			output.writeUTF("getCurrentUsers");
			
			numUser = input.readInt();
			System.out.println(numUser);
			for (int i = 0; i < numUser; i++) {
				userPool[i] = input.readUTF();
				System.out.println(userPool[i]);
			}
			for (int i = numUser; i < 12; i++) {
				userPool[i] = null;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("getCurrentUsers fail");
		}
		System.out.println("getCurrentUsers Over");
	}
	//send startGame require to mainServer
	public boolean startGame(int num,String[] selectedPlayers ) {
		
		System.out.println("startGame: "+"num == "+num);
		try {
			output.writeUTF("startGame");
			output.writeInt(num);
			//System.out.println("numPlayer: "+ num);
				
			int [] selected = new int[8];
			for(int j = 0 ; j < num ; j++) {
				//System.out.println("selectedPlayers: "+ selectedPlayers[j]);
			}
			for(int j = 0 ; j < numPlayer ; j++) {
				//System.out.println("userPool: "+ userPool[j]);
			}
			for(int i = 0 ; i < num ; i++) {
				//System.out.println(selectedPlayers[i]);
				for(int j = 0 ; j < numUser ; j++) {
					//System.out.println(selectedPlayers[i].equals(userPool[j]));
					if(selectedPlayers[i].equals(userPool[j])) {
						selected[i] = j;
						//System.out.println("selected[i]: " + j);
					}		
				}
			}
			
			for(int i = 0 ; i < num ; i++)
				output.writeInt(selected[i]);
			//String tmp = input.readUTF();
			//System.out.println(tmp);
			Arrays.fill(score, 0);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("startGame fail");
		}
		return false;
	}
	public void run() {
		listening();
	}
	public void listening() {
		String action = null;
		System.out.println("start listening");
		try {
			while (true) {

				action = input.readUTF();
				System.out.println("Action: "+action);
			
				if (action.equals("startGame")) {
					numPlayer = input.readInt();
					myNo = input.readInt();
					currentPlayer = input.readInt();
					for (int k = 0; k < numPlayer; k++) {
						playerName[k] = input.readUTF();
					}
					boolean yourturn = false;
					if (myNo == currentPlayer)
						yourturn = true;
					// clean GUI grid initial player score
					gui.gameGUI(numPlayer,playerName, true);
					

				} else if (action.equals("closeGame")) {
					//Calculate the winner
					int maxScore = -1,maxNo = 0;
					for(int i = 0; i< numPlayer ; i++)
					{
						if(score[i]>maxScore) {
							maxScore = score[i];
							maxNo = i;
						}
					}
					String winnerName = playerName[maxNo];
					int myScore = score[myNo];
					// show the result ot the game
					gui.closeGame(maxScore,winnerName,myScore);

				} else if (action.equals("updateGrid")) {
					int x = input.readInt();
					int y = input.readInt();
					char character = input.readChar();
					// update Grid
					gui.updateGrid(x,y,character);

				} else if (action.equals("getVote")) {
					String word = input.readUTF();
					// GUI vote for a recived word
					gui.voteWord(word);

				} else if (action.equals("synScore")) {
					currentPlayer = input.readInt();
					score[currentPlayer] = input.readInt();
					// update Score
					gui.updateScore(playerName[currentPlayer],score[currentPlayer]); // currentplayer -> string
				} else if (action.equals("yourTurn")) {
					currentPlayer = myNo;
					//now i can play
					
				} else if (action.equals("next")) {
					
					currentPlayer = input.readInt();
					
					gui.updatePlayer(playerName[currentPlayer]);
				}  else if (action.equals("notUnique")) {
					System.out.println("This is not a unique name");
					client.close();
					return;
					//gui.getAnotherName();
				} else if (action.equals("broadcastNewPlayer")) {
					//update userPool[]
					numUser = input.readInt();
					//System.out.println(numUser);
					for (int i = 0; i < numUser; i++) {
						userPool[i] = input.readUTF();
						//System.out.println(userPool[i]);
					}
					for (int i = numUser; i < 12; i++) {
						userPool[i] = null;
					}
				} else if (action.equals("gameExist")) {
					//update userPool[]
					System.out.println("There is a Game running");
					gui.startfalse();
				}
				
			}

		} catch (IOException e) {

			//e.printStackTrace();
			System.out.println("listening Stop");
			System.out.println("Connection Fail");
		}
		return;
	}
	public boolean isMyTurn() {
		//check if i'm the current player
		if(myNo == currentPlayer) {
			//System.out.println("myTurn");
			return true;
		}
		//System.out.println("not myTurn");
		return false;
	}
	public void closeGame() {
		try {
			output.writeUTF("closeGame");
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("closeGame Fail");
		}
	}
	
	public String placeChar(int x, int y, char character) {
		System.out.println("placeChar");
		try {
			output.writeUTF("placeChar");
			output.writeInt(x);
			output.writeInt(y);
			output.writeChar(character);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("placeChar Fail");
			return "connection fail";
		}
		return "succeed";
	}

	public String placeWord(String y) {
		System.out.println("placeWord: "+ y);
		try {
			output.writeUTF("placeWord");
			output.writeUTF(y);
			return "placeWord succeed";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("placeWord Fail");
			return "placeWord fail";
		}

	}
	public void vote(boolean ans) {
		System.out.println("vote");
		try {
			output.writeUTF("vote");
			output.writeInt(myNo);
			output.writeBoolean(ans);
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("vote fail");
		}
		
	}
	public void pass() {
		System.out.println("pass");
		try {
			output.writeUTF("pass");
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentPlayer++;
	}
	
	public void setMyname(String username) {
		this.myName = username;
		System.out.println("myName: "+ username);
	}
	
	public void disconnect() {
		try {
			output.writeUTF("disconnect");
			client.close();
		} catch (IOException e) {
			System.out.println("disconnect fail");
			//e.printStackTrace();
		}
	}
	
	//////////////////////////////////
	/*
	public String getPoints() {

		try {
			return input.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "connection fail";
		}
	}

	

	
	*/
}
