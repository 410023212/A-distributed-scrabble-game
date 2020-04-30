import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Server extends Thread {
	private char[][] grid = new char[24][24];
	// private Client[] clientList = new Client[10];
	// private Socket[] playerList;
	// private DataInputStream[] input;
	public DataOutputStream[] output = null;
	private int numPlayer;
	private int currentPlayer = 0;
	public String[] playerName;
	private int x;
	private int y;
	private char character;
	private int[] score = new int[8];
	private int numVote = 0;
	private int yes = 0;
	private String currentWord;
	private boolean getAword = false;
	private int pass;
	private boolean gameRuning = true;
	private MainServer main;

	public Server(int num, DataOutputStream[] outputtmp, String[] playerNametmp, MainServer mainServer) {
		numPlayer = num;
		this.playerName = playerNametmp;
		this.output = outputtmp;
		this.main = mainServer;
		for (int i = 0; i < 24; i++)
			for (int j = 0; j < 24; j++)
				grid[i][j] = ' ';
		for (int i = 0; i < 8; i++)
			score[i] = 0;
		startGame();
	}

	public void startGame() {
		System.out.println("Server startGame!!!");
		System.out.println("numPlayer: " + numPlayer);
		for (int i = 0; i < numPlayer; i++) {
			try {
				System.out.println(i);
				System.out.println(playerName[i]);
				output[i].writeUTF("startGame");
				output[i].writeInt(numPlayer);
				output[i].writeInt(i);
				output[i].writeInt(currentPlayer);
				for (int k = 0; k < numPlayer; k++) {
					output[i].writeUTF(playerName[k]);
				}

				output[i].writeUTF("next");
				output[i].writeInt(currentPlayer);

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("can't find one Player");
			}

		}

	}

	public void closeGame() {
		if (gameRuning == false)
			return;
		for (int i = 0; i < numPlayer; i++) {
			try {
				output[i].writeUTF("closeGame");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("closeGame fail");
			}

		}
		gameRuning = false;
		main.gameRunning = false;
	}

	public boolean setChar(int x2, int y2, char character2) {
		if (grid[x2][y2] == ' ') {
			grid[x2][y2] = character2;
			updateGrid(x2, y2, character2);
			return true;
		} else {
			System.out.println("character exsist");
			return false;
		}
	}

	private void updateGrid(int x, int y, char character) {
		for (int i = 0; i < numPlayer; i++) {
			try {
				output[i].writeUTF("updateGrid");
				output[i].writeInt(x);
				output[i].writeInt(y);
				output[i].writeChar(character);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("can't find one Player when synGrid");
			}
		}
	}

	public void placeWord(String word) {
		System.out.println("set a Word: " + word);
		this.currentWord = word;
		for (int i = 0; i < numPlayer; i++) {
			try {
				output[i].writeUTF("getVote");
				output[i].writeUTF(word);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("can't find one Player when getVote");
			}
		}

	}

	public void setVote(int no, boolean ans) {
		numVote++;
		if (ans) {
			yes++;
		}
		if (numVote >= numPlayer) {
			if (yes >= numPlayer) {
				// admit the word update score
				getAword = true;
				score[currentPlayer] += currentWord.length();
				synScore();
			} else {
				// word dosen't pass
				synScore();
			}
			numVote = 0;
			yes = 0;
		}

	}

	private void synScore() {
		for (int i = 0; i < numPlayer; i++) {
			try {
				output[i].writeUTF("synScore");
				output[i].writeInt(currentPlayer);
				output[i].writeInt(score[currentPlayer]);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("can't find one Player when synScore");
			}
		}
	}

	public void nextPlayer() {
		currentPlayer = (currentPlayer + 1) % numPlayer;
		System.out.println("Server: nextPlayer" + currentPlayer);
		if (getAword == false) {
			pass++;
		} else
			pass = 0;
		if (pass == numPlayer) {
			closeGame();
			return;
		}
		getAword = false;
		try {
			// broadcast who is the next player
			for (int i = 0; i < numPlayer; i++) {
				output[i].writeUTF("next");
				output[i].writeInt(currentPlayer);
			}
			output[currentPlayer].writeUTF("yourTurn");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
