package sudoku;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * The main Sudoku program
 */
public class SudokuMain extends JFrame {
	// private variables
	GameBoard board = new GameBoard(this);

	// Hint Button
	JButton btnHint = new JButton("Hint");
	
	Container cp = getContentPane();

	// menu
	static JMenu menu;
	static JMenu filesubmenu;
	static JMenu newgamesubmenu;
	static JMenu optionssubmenu;
	// menubar
	static JMenuBar menubar;
	// Menu items
	static JMenuItem easy, medium, hard;
	static JMenuItem resetgame, exit;
	static JMenuItem resumePause, enableDisable;
	static JMenuItem help;
	// create a frame
	static JFrame f;

	// JButton btnNewGame = new JButton("New Game");
	public static final int WINDOW_WIDTH = 300;
	public static final int WINDOW_HEIGHT = 150;
	// private Button newGameBtn;

	// Timer Variables
	int elaspedTime = 0;
	int seconds = 0;
	int minutes = 0;
	int hours = 0;
	boolean started = false;
	String seconds_string = String.format("%02d", seconds);
	String minutes_string = String.format("%02d", minutes);
	String hours_string = String.format("%02d", hours);
	JLabel timeLabel = new JLabel("00:00:00");
	Timer timer;

	boolean isGamePaused = false;
	boolean isSoundDisabled = false;

	// Open an audio input stream.
	// from a wave File
	static File soundFile = new File("C:\\Users\\Yu Sheng\\Desktop\\Java\\JavaTutorial\\src\\backgroundmusic.wav");
	static AudioInputStream audioIn;
	// Get a sound clip resource.
	static Clip clip;
	
	// Constructor
	public SudokuMain() {
		cp.setLayout(new BorderLayout());

		cp.add(board, BorderLayout.CENTER);
		
		// Default start at Easy difficulty
		board.init(GameBoard.DEFAULT_DIFFICULTY);
		updateStatus();

		// Set icon (change path)
		Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Yu Sheng\\Desktop\\Java\\JavaTutorial\\src\\icon.png");
		setIconImage(icon);

		pack(); // Pack the UI components, instead of setSize()
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle window closing
		setTitle("Sudoku");
		setVisible(true);

		// Create Menu
		menu = new JMenu("Menu");

		// Create Submenu
		filesubmenu = new JMenu("Files");
		newgamesubmenu = new JMenu("New Game");
		optionssubmenu = new JMenu("Options");

		// Create Menubar
		menubar = new JMenuBar();

		// Create menuitems
		easy = new JMenuItem("Easy");
		medium = new JMenuItem("Medium");
		hard = new JMenuItem("Hard");
		resetgame = new JMenuItem("Reset Game");
		exit = new JMenuItem("Exit");
		resumePause = new JMenuItem("Resume/Pause");
		enableDisable = new JMenuItem("Enable/Disable sound");
		help = new JMenuItem("Help");

		// Add listener
		menubarListener menubarlistener = new menubarListener();
		newGameBtnListener newgamebtnlistener = new newGameBtnListener();

		// Add action to listener
		easy.addActionListener(newgamebtnlistener);
		medium.addActionListener(newgamebtnlistener);
		hard.addActionListener(newgamebtnlistener);
		resetgame.addActionListener(menubarlistener);
		exit.addActionListener(menubarlistener);
		resumePause.addActionListener(menubarlistener);
		enableDisable.addActionListener(menubarlistener);
		help.addActionListener(menubarlistener);

		// Add to sub menu
		newgamesubmenu.add(easy);
		newgamesubmenu.add(medium);
		newgamesubmenu.add(hard);

		// Add submenu to menu
		menu.add(filesubmenu);

		// Add submenu to submenu
		filesubmenu.add(newgamesubmenu);

		// Add to sub menu
		filesubmenu.add(resetgame);
		filesubmenu.add(exit);

		// Add to sub menu
		optionssubmenu.add(resumePause);
		optionssubmenu.add(enableDisable);

		// Add to menu
		menu.add(optionssubmenu);
		menu.add(help);

		// Add menu to menubar
		menubar.add(menu);

		// add menubar to frame
		setJMenuBar(menubar);

		// timer
		add(timeLabel, BorderLayout.NORTH);

		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				elaspedTime = elaspedTime + 1000;
				hours = (elaspedTime / 3600000);
				minutes = (elaspedTime / 60000) % 60;
				seconds = (elaspedTime / 1000) % 60;
				seconds_string = String.format("%02d", seconds);
				minutes_string = String.format("%02d", minutes);
				hours_string = String.format("%02d", hours);
				timeLabel.setText(hours_string + ":" + minutes_string + ":" + seconds_string);
			}
		});
		timer.start();
	
	}

	public void updateStatus() {
		JTextField statusField = new JTextField("Status: " + board.incomplete_cell);
		cp.add(statusField, BorderLayout.SOUTH);
		System.out.println("Test");
	}
	
	/** The entry main() entry method */
	public static void main(String[] args) {
		// [TODO 1] Check Swing program template on how to run the constructor
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SudokuMain(); // Let the constructor do the job
				try {
					// Open an audio input stream.
					// from a wave File
					audioIn = AudioSystem.getAudioInputStream(soundFile);
					// Get a sound clip resource.
					clip = AudioSystem.getClip();
					// Open audio clip and load samples from the audio input stream.
					clip.open(audioIn);
					clip.start();
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private class newGameBtnListener implements ActionListener {
		// ActionEvent handler ‐ Called back upon button‐click.
		@Override
		public void actionPerformed(ActionEvent evt) {
			String action = evt.getActionCommand();
			elaspedTime = 0;
			isGamePaused = false;
			timer.start();
			if (action == "Easy") {
				board.init(GameBoard.DEFAULT_DIFFICULTY);
			}
			if (action == "Medium") {
				board.init(GameBoard.DEFAULT_DIFFICULTY + 10);
			}
			if (action == "Hard") {
				board.init(GameBoard.DEFAULT_DIFFICULTY + 20);
			}
		}
	}

	private class menubarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			String action = evt.getActionCommand();

			// Debug
			System.out.println(action + " selected");
			if (action == "Exit") {
				dispose();
			}
			if (action == "Reset Game") {
				elaspedTime = 0;
				board.loadPuzzle();
			}
			if (action == "Help") {
				board.hintPuzzle();
			}
			if (action == "Resume/Pause") {
				if (isGamePaused) {
					isGamePaused = false;
					board.resume();
					timer.start();
				} else {
					isGamePaused = true;
					board.pause();
					timer.stop();
				}

			}
			if (action == "Enable/Disable sound") {
				if (isSoundDisabled) {
					board.enableSound();
					isSoundDisabled = false;
					clip.start();
				} else {
					isSoundDisabled = true;
					board.disableSound();
					clip.stop();
				}
			}
		}
	}
	/*
	 * public static void music() { try { // Open an audio input stream. // from a
	 * wave File File soundFile = new
	 * File("C:\\Users\\Yu Sheng\\Desktop\\Java\\JavaTutorial\\src\\backgroundmusic.wav"
	 * ); AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile); //
	 * Get a sound clip resource. Clip clip = AudioSystem.getClip(); // Open audio
	 * clip and load samples from the audio input stream. clip.open(audioIn);
	 * clip.start(); } catch (UnsupportedAudioFileException e) {
	 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch
	 * (LineUnavailableException e) { e.printStackTrace(); } }
	 */
}