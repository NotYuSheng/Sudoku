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
	GameBoard board = new GameBoard();
	
	// Hint Button
	JButton btnHint = new JButton("Hint");
	
	// menu
	static JMenu menu;
	static JMenu filesubmenu;
	static JMenu newgamesubmenu;
	// menubar
	static JMenuBar menubar;
	// Menu items
	static JMenuItem easy, medium, hard;
    static JMenuItem resetgame, exit;
    static JMenuItem options, help;
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
	static Timer timer;
	
	// Constructor
	public SudokuMain() {
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		cp.add(board, BorderLayout.CENTER);
		
		// Default start at Easy difficulty
		board.init(GameBoard.DEFAULT_DIFFICULTY);
		
		// Set icon (change path)
		Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");    
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
		
		// Create Menubar
		menubar = new JMenuBar();
		
		// Create menuitems
		easy = new JMenuItem("Easy");
		medium = new JMenuItem("Medium");
		hard = new JMenuItem("Hard");
        resetgame = new JMenuItem("Reset Game");
        exit = new JMenuItem("Exit");
        options = new JMenuItem("Options");
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
        options.addActionListener(menubarlistener);
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
        
        // Add to menu
        menu.add(options);
        menu.add(help);
        
        // Add menu to menubar
        menubar.add(menu);
        
		// add menubar to frame
		setJMenuBar(menubar);
		
		// timer
		JLabel timeLabel = new JLabel("00:00:00");
		add(timeLabel, BorderLayout.NORTH);
		
		Timer timer = new Timer(1000, new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				elaspedTime=elaspedTime+1000;
				hours = (elaspedTime/3600000);
				minutes = (elaspedTime/60000) % 60;
				seconds = (elaspedTime/1000) % 60;
				seconds_string = String.format("%02d", seconds);
				minutes_string = String.format("%02d", minutes);
				hours_string = String.format("%02d", hours);
				timeLabel.setText(hours_string+":"+ minutes_string+":"+seconds_string);
				
			}
		});
		//timer.restart();
		timer.start();
		
	}

	/** The entry main() entry method */
	public static void main(String[] args) {
		// [TODO 1] Check Swing program template on how to run the constructor
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SudokuMain(); // Let the constructor do the job
				music();
			}
		});
	}
	
	private class newGameBtnListener implements ActionListener {
		// ActionEvent handler ‐ Called back upon button‐click.
		@Override
		public void actionPerformed(ActionEvent evt) {
			String action = evt.getActionCommand();
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
				board.loadPuzzle();
	        }
	        if (action == "Help") {
	        	board.hintPuzzle();
	        }
		}
	}
	
	public static void music() {
		 try {
	         // Open an audio input stream.
			// from a wave File
			 File soundFile = new File("C:\\javaproject\\tutorial7\\sudoku\\backgroundmusic.wav");
			 AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
	         // Get a sound clip resource.
	         Clip clip = AudioSystem.getClip();
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
	
}