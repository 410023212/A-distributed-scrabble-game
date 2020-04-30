import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer extends Thread {

	private int numPlayers = 0;
	private Socket[] playerSockets = new Socket[30];
	private Socket test;
	public DataInputStream[] input = new DataInputStream[30];
	public DataOutputStream[] output = new DataOutputStream[30];
	private String[] playerName = new String[30];
	private int port = 1234;
	public boolean gameRunning = false;
	private multithread[] playerThreads = new multithread[30];

	public MainServer(int port) {
		this.port = port;
	}

	public MainServer() {

	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("please input port number!");
			System.exit(0);
		} else {
			boolean isDigits = args[0].matches("[0-9]+");
			if (isDigits == false) {
				System.out.println("please input correct port number!");
				System.exit(0);
			} else {
				int portnumber = Integer.parseInt(args[0]);
				MainServer server = new MainServer(portnumber);
				server.begin();
			}
		}
	}

	public void begin() {

		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("Start Listening");
			while (true) {
				try {
					Socket tmpSock = server.accept();
					playerSockets[numPlayers] = tmpSock;
					// System.out.println(playerSockets[numPlayers]);
					// if(playerSockets[numPlayers] == null) continue;
					input[numPlayers] = new DataInputStream(playerSockets[numPlayers].getInputStream());
					output[numPlayers] = new DataOutputStream(playerSockets[numPlayers].getOutputStream());
					playerName[numPlayers] = input[numPlayers].readUTF();
					boolean tmp = false;
					for (int i = 0; i < numPlayers; i++) {
						if (playerName[i].equals(playerName[numPlayers])) {
							output[numPlayers].writeUTF("notUnique");
							tmp = true;
							break;
						}
					}
					if (tmp == true) {
						System.out.println("notUnique UserName");
						continue;
					}
					output[numPlayers].writeUTF("Unique");
					playerThreads[numPlayers] = new multithread(playerName[numPlayers], input[numPlayers],
							output[numPlayers], numPlayers, this);
					playerThreads[numPlayers].start();

					System.out.println("new Player: " + numPlayers);
					System.out.println(playerName[numPlayers]);
					numPlayers++;

					broadcastNewPlayer();
					// update server GUI
					// addPlayerGUI();
				} catch (IOException e) {
					// e.printStackTrace();
					System.out.println("server.Accept Fail!!!");
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(" Port has been used!");
		}

		// System.out.println("server.Accept Fail!!!");

	}

	private void broadcastNewPlayer() {
		System.out.println("broadcast User Pool");
		for (int i = 0; i < numPlayers; i++) {
			try {
				output[i].writeUTF("broadcastNewPlayer");
				output[i].writeInt(numPlayers);
				for (int j = 0; j < numPlayers; j++)
					output[i].writeUTF(playerName[j]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean startGame(int num, int[] selectedPlayers) {
		//select part players
		//check if a game exist
		if (gameRunning == true) {
			try {
				output[selectedPlayers[0]].writeUTF("gameExist");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		System.out.println("MainServer startGame!!!");
		String[] playerNametmp = new String[30];
		DataOutputStream[] outputtmp = new DataOutputStream[30];

		for (int i = 0; i < num; i++) {
			System.out.println(playerName[selectedPlayers[i]]);
			// output[selectedPlayers[i]].writeUTF("hello");
			outputtmp[i] = output[selectedPlayers[i]];
			playerNametmp[i] = playerName[selectedPlayers[i]];
		}
		//create a temporary Server to handle Game logic
		Server tmp = new Server(num, outputtmp, playerNametmp, this);
		for (int i = 0; i < num; i++) {
			playerThreads[selectedPlayers[i]].myServer = tmp;
		}
		// tmp.startGame();
		gameRunning = true;
		return true;
	}

	public void sentCurrentUsers(DataOutputStream output2) {
		try {
			output2.writeInt(numPlayers);
			for (int i = 0; i < numPlayers; i++)
				output2.writeUTF(playerName[i]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("sentCurrentUsers fail");
		}

	}

	public void disconnect(int num) {
		System.out.println("disconnect: num = " + num + " playerName = " + playerName[num]);
		numPlayers--;
		try {
			playerSockets[num].close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		;
		playerSockets[num] = playerSockets[numPlayers];
		input[num] = input[numPlayers];
		output[num] = output[numPlayers];
		playerName[num] = playerName[numPlayers];
		playerThreads[num] = playerThreads[numPlayers];
		playerThreads[num].setNo(num);
		// clean the player
		playerSockets[numPlayers] = null;
		input[numPlayers] = null;
		output[numPlayers] = null;
		playerName[numPlayers] = null;
		playerThreads[numPlayers] = null;
		broadcastNewPlayer();
		System.out.println("Now left " + numPlayers + " players");
	}
}
