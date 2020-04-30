import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

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

public class Gui extends JFrame {

	private String[] userPool = new String[30];
	private JPanel logPane;
	private JPanel choosePane;
	private JPanel gamePane;
	private JButton loginButton;
	private JButton gameButton;
	private JButton readyButton;
	private JTextArea hint;
	private JTextField usernameField;
	private JTextArea hintBegin;
	private JTextArea scoreBoard;
	private JList<String> userlist;
	private JList<String> selectedUser;
	private DefaultListModel<String> modelList = new DefaultListModel<>();
	private JScrollPane scrollMemberPool;
	private JScrollPane scrollselectedUser;
	private JScrollPane scrollScore;
	private ArrayList<Integer> scorelist = new ArrayList<Integer>();

	private JButton[][] grids;
	private JButton close;
	private JButton pass;
	private JButton vote_r;
	private JButton vote_c;
	private JTextArea charInput;
	private JTextArea charText;
	private JTextArea username1;
	private JTextArea score1;
	private JTextArea username_t1;
	private JTextArea score_t1;
	private JTextArea username2;
	private JTextArea score2;
	private JTextArea username_t2;
	private JTextArea score_t2;
	private JTextArea username3;
	private JTextArea score3;
	private JTextArea username_t3;
	private JTextArea score_t3;
	private JTextArea username4;
	private JTextArea score4;
	private JTextArea username_t4;
	private JTextArea score_t4;
	private JTextArea currentUser;
	private JTextArea currName;

	private Boolean flag = true;
	private Boolean voteFlag1 = true;
	private Boolean voteFlag2 = true;
	private int playerNum;
	private static String username = "";
	private ArrayList<String> players = new ArrayList<String>();
	private static Client myClient;
	private String word1;
	private String word2;
	private Boolean start = false;
	private int x = -1;
	private int y = -1;

	private int countFlag = 0;
	private int commonx = -1;
	private int commony = -1;
	private static boolean connect = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("please input ip address and port number!");
			System.exit(0);
		} else {
			boolean isDigits = args[1].matches("[0-9]+");
			if (isDigits == false) {
				System.out.println("please input correct port number!");
				System.exit(0);
			} else {
				String ip = args[0];
				int port = Integer.parseInt(args[1]);			
				Gui ui = new Gui();
				myClient = new Client(ui, ip, port);
				ui.setVisible(true);
				ui.logGUI();
			}
		}

	}

	/**
	 * Create the frame.
	 */
	public Gui() {

		this.logPane = new JPanel();
		this.logPane.setLayout(null);

		this.choosePane = new JPanel();
		this.choosePane.setLayout(null);

		this.gamePane = new JPanel();
		this.gamePane.setLayout(null);

	}

	public void logGUI() {

		this.setTitle("Scrabble");
		this.setBounds(0, 0, 400, 300);

		this.logPane.setVisible(true);
		this.choosePane.setVisible(false);
		this.gamePane.setVisible(false);

		this.loginButton = new JButton("log in");
		this.loginButton.setBounds(150, 200, 100, 50);

		this.hint = new JTextArea("please input username:");
		this.hint.setBackground(UIManager.getColor("Button.background"));
		this.hint.setEditable(false);
		this.hint.setBounds(120, 50, 200, 30);
		this.hint.setBorder(BorderFactory.createEmptyBorder());

		this.usernameField = new JTextField();
		this.usernameField.setBounds(100, 80, 200, 30);

		this.logPane.add(this.loginButton);
		this.logPane.add(this.hint);
		this.logPane.add(this.usernameField);

		getContentPane().add(logPane);
		this.setVisible(true);

		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				username = usernameField.getText();
				if (username.equals("")) {
					String error = "please input username!";
					JOptionPane.showMessageDialog(null, error, error, JOptionPane.ERROR_MESSAGE);
				} else {
					myClient.setMyname(username);
					int loginResult = myClient.login();
					if (loginResult == 3) {
						connect = true;
						myClient.start();
						chooseGUI();
					} else if(loginResult == 2){
						String info = "connection is refused!";
						JOptionPane.showMessageDialog(null, info, "connection fail", JOptionPane.INFORMATION_MESSAGE);
					}else {
						String info = "This name has already existed, please input another one!";
						JOptionPane.showMessageDialog(null, info, "change name", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (connect){
				   myClient.disconnect();
				}
				System.exit(0);
			}
		});

	}

	public void chooseGUI() {
		this.setTitle("Scrabble");
		this.add(this.choosePane);

		this.setBounds(0, 0, 650, 550);
		this.logPane.setVisible(false);
		this.choosePane.setVisible(true);
		this.gamePane.setVisible(false);

		this.gameButton = new JButton("Start game");
		this.gameButton.setBounds(350, 420, 150, 50);

		this.readyButton = new JButton("Log out");
		this.readyButton.setBounds(180, 420, 150, 50);

		this.hintBegin = new JTextArea("please choose player from the left list");
		this.hintBegin.setEditable(false);
		this.hintBegin.setBackground(UIManager.getColor("Button.background"));
		this.hintBegin.setBounds(200, 30, 250, 15);
		this.hintBegin.setBorder(BorderFactory.createEmptyBorder());

		this.scoreBoard = new JTextArea("scoreBoard");
		this.scoreBoard.setEditable(false);
		scrollScore = new JScrollPane(scoreBoard);
		scrollScore.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollScore.setBounds(200, 300, 250, 100);
		this.scrollScore.setVisible(true);
		this.userlist = new JList<>(myClient.userPool);
		this.userlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.userlist.setFixedCellHeight(20);

		this.scrollMemberPool = new JScrollPane(userlist);
		this.scrollMemberPool.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollMemberPool.setBounds(130, 70, 150, 200);

		this.selectedUser = new JList<>(modelList);
		this.selectedUser.setFixedCellHeight(20);
		this.scrollselectedUser = new JScrollPane(selectedUser);
		this.scrollselectedUser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollselectedUser.setBounds(380, 70, 150, 200);

		this.choosePane.add(this.gameButton);
		this.choosePane.add(this.readyButton);
		this.choosePane.add(this.hintBegin);
		this.choosePane.add(this.scrollScore);
		this.choosePane.add(scrollMemberPool);
		this.choosePane.add(scrollselectedUser);

		this.setVisible(true);

		this.userlist.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					if (username.equals(userlist.getSelectedValue())) {
						String info = "This is yourself";
						JOptionPane.showMessageDialog(null, info, info, JOptionPane.INFORMATION_MESSAGE);
					} else if (modelList.contains(userlist.getSelectedValue())) {
						String info = "Already select this user";
						JOptionPane.showMessageDialog(null, info, info, JOptionPane.INFORMATION_MESSAGE);

					} else
						modelList.addElement(userlist.getSelectedValue());
				}

			}
		});

		selectedUser.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					modelList.removeElement(selectedUser.getSelectedValue());
				}

			}
		});

		gameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				choosePlayer();

			}

		});

		readyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				myClient.disconnect();
				System.exit(0);

			}

		});
		

	}

	public void choosePlayer() {

		int length = this.modelList.getSize();
		System.out.println("length:== " + length);
		if (length > 4) {
			JOptionPane.showMessageDialog(null, "Please choose at most three players!", "number error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			String[] playermember = new String[4];
			playermember[0] = this.username;
			if (length > 0) {
				for (int i = 1; i <= length; i++) {

					playermember[i] = this.modelList.get(i - 1);

				}
			}

			for (int i = 0; i <= length; i++)
				System.out.println(playermember[i]);
			myClient.startGame(length + 1, playermember);
		}

	}

	public void gameGUI(int number, String[] playerName, boolean turn) {

		this.playerNum = number;
		for (int i = 0; i < number; i++)
			System.out.println(playerName[i]);
		for (int k = 0; k < this.playerNum; k++) {
			if (!username.equals((playerName)[k]))
				this.players.add(playerName[k]);
		}

		this.gamePane.setVisible(true);

		this.logPane.setVisible(false);
		this.choosePane.setVisible(false);
		setTitle("game page");
		setBounds(0, 0, 650, 550);

		grids = new JButton[20][20];
		close = new JButton("Close");
		pass = new JButton("Pass");
		vote_r = new JButton("row vote");
		vote_c = new JButton("column vote");
		charInput = new JTextArea("input char:");
		charText = new JTextArea();

		username1 = new JTextArea("your username:");
		score1 = new JTextArea("your score:");
		username_t1 = new JTextArea("null");
		score_t1 = new JTextArea("0");

		username2 = new JTextArea("2nd username:");
		score2 = new JTextArea("2nd score:");
		username_t2 = new JTextArea("null");
		score_t2 = new JTextArea("0");

		username3 = new JTextArea("3rd username:");
		score3 = new JTextArea("3rd score:");
		username_t3 = new JTextArea("null");
		score_t3 = new JTextArea("0");

		username4 = new JTextArea("4th username:");
		score4 = new JTextArea("4th score:");
		username_t4 = new JTextArea("null");
		score_t4 = new JTextArea("0");

		this.currentUser = new JTextArea("whose turn:");
		this.currName = new JTextArea();

		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				grids[i][j] = new JButton();
			}
		}

		gamePane.add(close);
		gamePane.add(pass);
		gamePane.add(vote_r);
		gamePane.add(vote_c);
		gamePane.add(charInput);
		gamePane.add(charText);

		gamePane.add(username1);
		gamePane.add(score1);
		gamePane.add(username_t1);
		gamePane.add(score_t1);

		gamePane.add(username2);
		gamePane.add(score2);
		gamePane.add(username_t2);
		gamePane.add(score_t2);

		gamePane.add(username3);
		gamePane.add(score3);
		gamePane.add(username_t3);
		gamePane.add(score_t3);

		gamePane.add(username4);
		gamePane.add(score4);
		gamePane.add(username_t4);
		gamePane.add(score_t4);

		gamePane.add(this.currentUser);
		gamePane.add(this.currName);

		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				gamePane.add(grids[i][j]);
				grids[i][j].setBounds(200 + i * 20, 20 + j * 20, 20, 20);
				grids[i][j].setActionCommand(i + "," + j);
				grids[i][j].setText("");
				if (turn == false)
					grids[i][j].setEnabled(false);

			}
		}

		close.setBounds(500, 480, 100, 20);
		pass.setBounds(380, 480, 100, 20);
		vote_r.setBounds(500, 440, 100, 20);
		vote_c.setBounds(380, 440, 100, 20);
		charInput.setBounds(200, 430, 80, 15);
		charInput.setEditable(false);
		charInput.setBackground(UIManager.getColor("Button.background"));
		charText.setBounds(280, 430, 15, 15);

		username1.setBounds(20, 20, 100, 15);
		username1.setBackground(UIManager.getColor("Button.background"));
		username1.setEditable(false);
		score1.setBounds(20, 80, 80, 15);
		score1.setBackground(UIManager.getColor("Button.background"));
		score1.setEditable(false);
		username_t1.setBounds(20, 50, 150, 15);
		username_t1.setText(username);
		score_t1.setBounds(100, 80, 70, 15);
		username_t1.setEditable(false);
		score_t1.setEditable(false);

		username2.setBounds(20, 120, 100, 15);
		username2.setBackground(UIManager.getColor("Button.background"));
		username2.setEditable(false);
		score2.setBounds(20, 180, 80, 15);
		score2.setBackground(UIManager.getColor("Button.background"));
		score2.setEditable(false);
		username_t2.setBounds(20, 150, 150, 15);
		if (this.playerNum >= 2)
			username_t2.setText(this.players.get(0));
		score_t2.setBounds(100, 180, 70, 15);
		username_t2.setEditable(false);
		score_t2.setEditable(false);

		username3.setBounds(20, 220, 100, 15);
		username3.setBackground(UIManager.getColor("Button.background"));
		username3.setEditable(false);
		score3.setBounds(20, 280, 80, 15);
		score3.setBackground(UIManager.getColor("Button.background"));
		score3.setEditable(false);
		username_t3.setBounds(20, 250, 150, 15);
		if (this.playerNum >= 3)
			username_t3.setText(this.players.get(1));
		score_t3.setBounds(100, 280, 70, 15);
		username_t3.setEditable(false);
		score_t3.setEditable(false);

		username4.setBounds(20, 320, 100, 15);
		username4.setBackground(UIManager.getColor("Button.background"));
		username4.setEditable(false);
		score4.setBounds(20, 380, 80, 15);
		score4.setBackground(UIManager.getColor("Button.background"));
		score4.setEditable(false);
		username_t4.setBounds(20, 350, 150, 15);
		if (this.playerNum == 4)
			username_t2.setText(this.players.get(2));
		score_t4.setBounds(100, 380, 70, 15);
		username_t4.setEditable(false);
		score_t4.setEditable(false);

		this.currentUser.setBounds(20, 410, 150, 15);
		this.currentUser.setBackground(UIManager.getColor("Button.background"));
		this.currentUser.setEditable(false);
		this.currName.setBounds(20, 440, 150, 15);

		if (this.playerNum <= 3) {
			username4.setVisible(false);
			score4.setVisible(false);
			username_t4.setVisible(false);
			score_t4.setVisible(false);

			if (this.playerNum <= 2) {
				username3.setVisible(false);
				score3.setVisible(false);
				username_t3.setVisible(false);
				score_t3.setVisible(false);

				if (this.playerNum <= 1) {
					username2.setVisible(false);
					score2.setVisible(false);
					username_t2.setVisible(false);
					score_t2.setVisible(false);
				}
			}
		}

		getContentPane().add(gamePane);

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				x = -1;
				y = -1;
				countFlag = 0;
				commonx = -1;
				commony = -1;
				voteFlag1 = true;
				voteFlag2 = true;
				gamePane.removeAll();
				myClient.closeGame();

			}
		});

		pass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				flag = myClient.isMyTurn();
				if (flag) {
					x = -1;
					y = -1;
					countFlag = 0;
					commonx = -1;
					commony = -1;
					voteFlag1 = true;
					voteFlag2 = true;
					myClient.pass();
				} else {
					JOptionPane.showMessageDialog(null, "it's not your turn!", "please wait",
							JOptionPane.ERROR_MESSAGE);
				}

				// myClient.listening();

			}
		});

		vote_r.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				flag = myClient.isMyTurn();
				if (flag) {
					if (x != -1 && y != -1) {
						if (voteFlag1) {
							applyVote(x, y, true);
							voteFlag1 = false;
						} else {
							JOptionPane.showMessageDialog(null, "you have applied for row vote!", "invalid vote",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "can't apply for vote!", "empty input",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "it's not your turn!", "please wait",
							JOptionPane.ERROR_MESSAGE);
				}
				// myClient.listening();
			}
		});

		vote_c.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				flag = myClient.isMyTurn();
				if (flag) {
					if (x != -1 && y != -1) {
						if (voteFlag2) {
							applyVote(x, y, false);
							voteFlag2 = false;
						} else {
							JOptionPane.showMessageDialog(null, "you have applied for row vote!", "invalid vote",
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "can't apply for vote!", "empty input",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "it's not your turn!", "please wait",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		inputJudge();

	}

	public void closeGame(int maxScore, String winnerName, int myScore) {

		JOptionPane.showMessageDialog(null,
				"Max Score: " + maxScore + "\nWinner: " + winnerName + "\nyour score: " + myScore, "Game over!",
				JOptionPane.WARNING_MESSAGE);
		scorelist.add(myScore);
		this.scoreBoard.setText("scoreBoard");

		for (int i = 0; i < scorelist.size(); i++)
			this.scoreBoard.append("\n" + scorelist.get(i));
		this.start = false;
		this.gamePane.removeAll();
		this.gamePane.setVisible(false);
		this.logPane.setVisible(false);
		this.choosePane.setVisible(true);

	}

	public void updateGrid(int x, int y, char character) {

		grids[x][y].setText(String.valueOf(character));
		grids[x][y].setEnabled(false);

		if (this.start == false) {
			for (int i = 0; i < 20; i++)
				for (int j = 0; j < 20; j++)
					grids[i][j].setEnabled(false);
			this.start = true;
		}

		if (grids[x - 1][y].getText().equals(""))
			grids[x - 1][y].setEnabled(true);
		if (grids[x + 1][y].getText().equals(""))
			grids[x + 1][y].setEnabled(true);
		if (grids[x][y - 1].getText().equals(""))
			grids[x][y - 1].setEnabled(true);
		if (grids[x][y + 1].getText().equals(""))
			grids[x][y + 1].setEnabled(true);
	}

	public void voteWord(String word) {
		boolean answer = false;
		if (JOptionPane.showConfirmDialog(null, "Does " + word + " is a word?", "vote",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			answer = true;
		}
		myClient.vote(answer);
	}

	public void updateScore(String currentPlayer, int score) {
		if (currentPlayer.equals(username_t1.getText()))
			score_t1.setText(String.valueOf(score));
		if (currentPlayer.equals(username_t2.getText()))
			score_t2.setText(String.valueOf(score));
		if (currentPlayer.equals(username_t3.getText()))
			score_t3.setText(String.valueOf(score));
		if (currentPlayer.equals(username_t4.getText()))
			score_t4.setText(String.valueOf(score));
	}

	public void startfalse() {
		JOptionPane.showMessageDialog(null, "sorry! there is a game running!", "please wait",
				JOptionPane.ERROR_MESSAGE);
	}

	public void inputJudge() {

		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {

				grids[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						flag = myClient.isMyTurn();
						if (flag) {
							JButton button = (JButton) e.getSource();
							String s = charText.getText();
							if (s.length() == 1) {
								String index = e.getActionCommand();
								String[] indexs = index.split(",");
								x = Integer.parseInt(indexs[0]);
								y = Integer.parseInt(indexs[1]);
								charText.setText(null);
								System.out.println("count:" + countFlag);
								System.out.println("x:" + x);
								System.out.println("y:" + y);
								System.out.println("comx:" + commonx);
								System.out.println("comy:" + commony);
								if (countFlag == 0) {
									commonx = x;
									commony = y;
									if (voteFlag1 && voteFlag2) {
										myClient.placeChar(x, y, s.charAt(0));
										countFlag = countFlag + 1;
									} else
										JOptionPane.showMessageDialog(null, "you have applied for vote!", "can't input",
												JOptionPane.ERROR_MESSAGE);

								} else if (countFlag == 1) {
									if (x == commonx) {
										if (voteFlag1 && voteFlag2) {
											commony = -1;
											myClient.placeChar(x, y, s.charAt(0));
											countFlag = countFlag + 1;
										} else
											JOptionPane.showMessageDialog(null, "you have applied for vote!",
													"can't input", JOptionPane.ERROR_MESSAGE);

									} else if (y == commony) {
										if (voteFlag1 && voteFlag2) {
											commonx = -1;
											myClient.placeChar(x, y, s.charAt(0));
											countFlag = countFlag + 1;
										} else
											JOptionPane.showMessageDialog(null, "you have applied for vote!",
													"can't input", JOptionPane.ERROR_MESSAGE);

									} else
										JOptionPane.showMessageDialog(null, "Please input at same row or column!",
												"input error", JOptionPane.ERROR_MESSAGE);
								} else {
									if (commonx != -1) {
										if (x == commonx) {
											if (voteFlag1 && voteFlag2) {
												myClient.placeChar(x, y, s.charAt(0));
												countFlag = countFlag + 1;
											} else
												JOptionPane.showMessageDialog(null, "you have applied for vote!",
														"can't input", JOptionPane.ERROR_MESSAGE);

										} else
											JOptionPane.showMessageDialog(null, "Please input at same row or column!",
													"input error", JOptionPane.ERROR_MESSAGE);

									} else if (commony != -1) {
										if (y == commony) {
											if (voteFlag1 && voteFlag2) {
												myClient.placeChar(x, y, s.charAt(0));
												countFlag = countFlag + 1;
											} else
												JOptionPane.showMessageDialog(null, "you have applied for vote!",
														"can't input", JOptionPane.ERROR_MESSAGE);

										} else
											JOptionPane.showMessageDialog(null, "Please input at same row or column!",
													"input error", JOptionPane.ERROR_MESSAGE);
									}
								}

							} else {
								charText.setText(null);
								JOptionPane.showMessageDialog(null, "Please input one character!", "input error",
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							charText.setText(null);
							JOptionPane.showMessageDialog(null, "it's not your turn!", "please wait",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
		}
	}

	public void updatePlayer(String name) {

		this.currName.setText(name);
	}

	public void applyVote(int x, int y, boolean row) {

		word1 = grids[x][y].getText();
		word2 = grids[x][y].getText();

		for (int i = x - 1; i >= 0; i--) {
			if (grids[i][y].getText().isEmpty()) {
				break;
			}
			word1 = grids[i][y].getText() + word1;
		}
		for (int i = x + 1; i < 20; i++) {
			if (grids[i][y].getText().isEmpty()) {
				break;
			}
			word1 = word1 + grids[i][y].getText();
		}

		for (int j = y - 1; j >= 0; j--) {
			if (grids[x][j].getText().isEmpty()) {
				break;
			}
			word2 = grids[x][j].getText() + word2;
		}
		for (int j = y + 1; j < 20; j++) {
			if (grids[x][j].getText().isEmpty()) {
				break;
			}
			word2 = word2 + grids[x][j].getText();
		}

		System.out.println("word1:" + word1);
		System.out.println("word2:" + word2);

		if (row == true) {
			System.out.println("GUI palceword1: " + word1);
			myClient.placeWord(word1);

			// myClient.listening();
		} else {
			System.out.println("GUI palceword2: " + word2);
			myClient.placeWord(word2);
			// myClient.listening();
		}

	}

}
