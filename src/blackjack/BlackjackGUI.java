/*****************************************************
 * Albert Wilcox
 * IB Computer Science
 * 
 * BlackjackGUI class
 ****************************************************/
package blackjack;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import wilcox.cards.Card;

/**
 * This class handles
 * @author Albert Wilcox
 *
 */
public class BlackjackGUI extends JFrame{	
	private static final long serialVersionUID = 1L;
	
	/*
	 * Declare various necessary objects.
	 * This is necessary because a BlackjackGUI is an aggregate object
	 */
	private HomeGUIPanel homeGUI;
	private SetupGUIPanel setupGUI;
	private GameGUIPanel gameGUI;
	private StatsGUIPanel statsGUI;
	
	private Font headerFont;
	
	/*
	 * These are used in multiple panels so they are declared at
	 * class level for optimization
	 */
	private SetupButtonListener SBL;
	private HomeButtonListener HBL;
	private RuleButtonListener RBL;
	
	/**
	 * Different types of panels for use with changePanel method
	 * @author Albert Wilcox
	 *
	 */
	private enum PanelType{
		HOME, SETUP, PLAY, STATS
	}

	/**
	 * Set up blackjack GUI
	 */
	public BlackjackGUI() {
		
		super("Blackjack");
		
		/*
		 * All of this is required when changing look and field of java applications
		 */
		/*try {
            // Set System L&F
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}*/
		
		// Instantiate Listeners
		HBL = new HomeButtonListener();
		SBL = new SetupButtonListener();
		RBL = new RuleButtonListener();
		
		// Instantiate universal header font
		headerFont = new Font("regular", Font.BOLD, 20);
		
		// Instantiate the various GUI panels to be used
		homeGUI = new HomeGUIPanel();
		setupGUI = new SetupGUIPanel();
		gameGUI = new GameGUIPanel();
		statsGUI = new StatsGUIPanel();
		
		// Set up and display frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		getContentPane().add(homeGUI);
		pack();
		setVisible(true);
	}
	
	/**
	 * Change the panel in the frame to a given type
	 * @param type What type of panel should be shown
	 */
	private void changePanel(PanelType type) {
		// Essentially an alias that will be used on a panel
		JPanel panel;
		
		// Assign a panel to that alias
		switch(type) {
		case HOME:
			panel = homeGUI;
			break;
		case SETUP:
			panel = setupGUI;
			break;
		case PLAY:
			panel = gameGUI;
			gameGUI.setupGamePanel();
			break;
		case STATS:
			panel = statsGUI;
			break;
		default:
			panel = homeGUI;
			break;
		}
		
		// Update frame with new panel
		getContentPane().add(panel);
		getContentPane().remove(getContentPane().getComponent(0));
		revalidate();
		pack();
	}
	
	/**
	 * Adds a card to the graphic display of dealer cards
	 * @param card - Which card to add
	 */
	public void addDealerCard(Card card) {
		gameGUI.dealerPanel.addCard(card);
	}
	
	/**
	 * Sets whether the dealer's first card is visible or concealed
	 * @param visible - whether the dealer's first card is visible or concealed
	 */
	public void setDealerFirstCardVisible(boolean visible) {
		gameGUI.dealerPanel.setFirstCardVisible(visible);
	}
	
	/**
	 * Adds a card to the graphical display of the player's hand
	 * @param card - which card to add
	 */
	public void addPlayerCard(Card card) {
		gameGUI.playerPanel.addCard(card);
	}
	
	/**
	 * Add a card to the player's split hand
	 * @param splitHand Which hand to add to
	 * @param card What card to add
	 */
	public void addPlayerCard(int splitHand, Card card) {
		if (splitHand == 0) addPlayerCard(card);
		else gameGUI.splitPanel.addCard(splitHand, card);
	}
	
	/**
	 * Sets up the panel to be displayed at the end of the turn
	 * @param out - what to tell the player
	 */
	public void setupResetButton(String out) {
		gameGUI.buttonPanel.setupResetPanel(out);
	}
	
	/**
	 * Updates the game panel midgame
	 * @param turnCompl - whether it's the end of the player's turn
	 * @param first - whether it's the player's first turn
	 * @param splittable - whether the player has a splittable hand
	 * @param insurance - whether insurance can be used
	 */
	public void updateGamePanel(boolean turnCompl, boolean first, boolean splittable, boolean insurance) {
		if (turnCompl) gameGUI.buttonPanel.setupNextPanel();
		else gameGUI.buttonPanel.setupPlayPanel(first, splittable, insurance);
	}
	
	public void setHint(Move m) {
		String s = m.toString();
		gameGUI.hintField.setText(s);
	}
	
	public void setActiveSplitPanel(int num) {
		gameGUI.splitPanel.setActiveHand(num);
	}
	
	public void updateStatusBar(int percent) {
		statsGUI.progress.setValue(percent);
	}
	
	/**
	 * Creates a home button with the little house icon. It's used
	 * alot so I made a method for it
	 * @return - A home button object
	 */
	private JButton createHomeButton() {
		URL url = BlackjackGUI.class.getResource("/images/home.png");
		ImageIcon homeIcon = new ImageIcon(url);
		homeIcon.setImage(homeIcon.getImage().getScaledInstance(17, 17, Image.SCALE_DEFAULT));
		JButton home = new JButton("Home", homeIcon);
		home.addActionListener(HBL);
		home.setBackground(new Color(247, 234, 197));
		
		return home;
	}
	
	/**
	 * The panel that will be opened up to, and which can be accessed through the home button.
	 * Gives access to game mode and statistics mode
	 * @author Albert Wilcox
	 *
	 */
	private class HomeGUIPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		// Necessary swing components
		private JButton play, stats, info1, info2;
		private JLabel header;
		private JSeparator sep;
		
		// The panel's layout
		GroupLayout layout;
		
		/**
		 * Construct a home GUI panel
		 */
		public HomeGUIPanel() {
			// Set up the four buttons
			play = new JButton("Play Mode");
			play.addActionListener(SBL);
			stats = new JButton("Statistics Mode");
			stats.addActionListener(new StatsButtonListener());
			info1 = new JButton("Info");
			info1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JOptionPane.showMessageDialog(play, "Allows you to play blackjack");
				}
			});
			info2 = new JButton("Info");
			info2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JOptionPane.showMessageDialog(stats, "Allows you to perform statistical\nanalysis on blackjack");	
				}
			});
			
			// Set up other components
			sep = new JSeparator(JSeparator.HORIZONTAL);
			header = new JLabel("Anti-Wormer Blackjack Application");
			header.setFont(headerFont);
						
			// Initiate layout
			layout = new GroupLayout(this);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			setLayout(layout);
			
			// Set up vertical layout information
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addComponent(header)
					.addComponent(sep)
					.addGroup(layout.createParallelGroup()
							.addComponent(play)
							.addComponent(stats))
					.addGroup(layout.createParallelGroup()
							.addComponent(info1)
							.addComponent(info2)));
			
			// Set up horizontal layout information
			layout.setHorizontalGroup(layout.createParallelGroup()
					.addComponent(header)
					.addComponent(sep)
					.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
									.addComponent(play)
									.addComponent(info1))
							.addGap(70)
							.addGroup(layout.createParallelGroup()
									.addComponent(stats)
									.addComponent(info2))));
		}
		
		private class StatsButtonListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(PanelType.STATS);
			}
		}
	}
	
	/**
	 * The GUI panel where users set up their blackjack game
	 * @author Albert Wilcox
	 *
	 */
	private class SetupGUIPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Necessary swing components
		JLabel header, decks, money;
		JSlider deckSlider;
		JSpinner moneySpinner;
		JSeparator sep1;
		JButton rules, play, home;
		
		/**
		 * Make a GUI panel just perfect for configuring a blackjack game
		 */
		public SetupGUIPanel() {
			// Initialize some components
			header = new JLabel("Game Setup");
			header.setFont(headerFont);
			
			sep1 = new JSeparator(JSeparator.HORIZONTAL);
			
			// Initialize other labels, buttons and checkbox
			decks = new JLabel("Decks: ");
			money = new JLabel("Starting Money: ");
			rules = new JButton("Rules");
			rules.addActionListener(RBL);
			play = new JButton("Play");
			play.addActionListener(new PlayButtonListener());
			home = createHomeButton();
			
			// Initialize spinners
			moneySpinner = new JSpinner(new SpinnerNumberModel(500, 100, 1000, 10));
			deckSlider = new JSlider(1, 8, 4);
			deckSlider.setMajorTickSpacing(1);
			deckSlider.setPaintLabels(true);
			
			// Initialize layout
			GroupLayout layout = new GroupLayout(this);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			setLayout(layout);
			
			// Set up verticle layout information
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addComponent(header)
					.addComponent(sep1)
					.addGroup(layout.createParallelGroup()
							.addComponent(decks)
							.addComponent(deckSlider))
					.addGroup(layout.createParallelGroup()
							.addComponent(money)
							.addComponent(moneySpinner))
					.addGroup(layout.createParallelGroup()
							.addComponent(home)
							.addComponent(rules)
							.addComponent(play)));
			
			// Set up horizontal layout information
			layout.setHorizontalGroup(layout.createParallelGroup()
					.addComponent(header)
					.addComponent(sep1)
					.addGroup(layout.createSequentialGroup()
							.addComponent(decks)
							.addComponent(deckSlider))
					.addGroup(layout.createSequentialGroup()
							.addComponent(money)
							.addComponent(moneySpinner))
					.addGroup(layout.createSequentialGroup()
							.addComponent(home)
							.addComponent(rules)
							.addGap(60)
							.addComponent(play)));
		}
		
		/**
		 * Listens for when the player presses the 'Play' button
		 * @author Albert Wilcox
		 */
		private class PlayButtonListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Engine.setupGame(Integer.parseInt(moneySpinner.getValue().toString()), deckSlider.getValue());
				changePanel(PanelType.PLAY);
			}
		}
	} 
	
	/**
	 * The panel where players play
	 * @author Albert Wilcox
	 *
	 */
	private class GameGUIPanel extends JPanel{
		/**
		 * Eclipse insists I have this
		 */
		private static final long serialVersionUID = 1L;
		/*
		 * Declare the necessary swing components
		 */
		JLabel header, dealer, playerCard, money, bet, info, blackjack, stand, ins;
		JSeparator sep1, sep2;
		JButton hint, setup, home, rules;
		JTextField moneyField, betField, hintField;
		
		/*
		 * These classes are children of JPanel, so that's basically what these are
		 */
		DealerCards dealerPanel;
		PlayerCards playerPanel;
		PlayButtons buttonPanel;
		SplitPlayerCards splitPanel;
		
		GroupLayout layout;
		
		BetFieldListener BFL;
		
		boolean insurance;
		Color c, light;
		
		/**
		 * Constructor for the game panel, where the blackjack game itself is played
		 */
		public GameGUIPanel() {				
			c = new Color(13, 53, 12);
			light = new Color(19, 79, 18);
			
			setBackground(c);
			
			// Initialize a font used commonly in the panel
			Font f = new Font("", Font.PLAIN, 16);
			
			// Initialize various JLabels with text
			header = new JLabel("Blackjack");
			header.setFont(headerFont);
			header.setForeground(Color.WHITE);
			dealer = new JLabel("Dealer Cards: ");
			dealer.setForeground(Color.white);
			playerCard = new JLabel("Your Cards: ");
			playerCard.setForeground(Color.white);
			money = new JLabel("Money: ");
			money.setForeground(Color.white);
			bet = new JLabel("Bet: ");
			bet.setForeground(Color.white);
			info = new JLabel();
			info.setFont(f);
			info.setForeground(Color.white);
			
			// Initialize the separators (horizontal lines)
			sep1 = new JSeparator(JSeparator.HORIZONTAL);
			sep2 = new JSeparator(JSeparator.HORIZONTAL);
			
			// Initialize the labels at the top which mimic those on a blackjack table
			blackjack = new JLabel("BLACKJACK PAYS 3 TO 2");
			blackjack.setFont(f);
			blackjack.setForeground(Color.yellow);
			stand = new JLabel("DEALER STANDS ON SOFT 17");
			stand.setFont(f);
			stand.setForeground(Color.white);
			ins = new JLabel("INSURANCE PAYS 2 TO 1");
			ins.setFont(f);
			ins.setForeground(Color.yellow);
			
			// Initialize the bottom buttons
			hint = new JButton("Hint");
			hint.addActionListener(new HintButtonListener());
			hint.setBackground(light);
			hint.setForeground(Color.white);
			setup = new JButton("Setup");
			setup.addActionListener(new SetupButtonListener());
			setup.setBackground(light);
			setup.setForeground(Color.white);
			rules = new JButton("Rules");
			rules.addActionListener(RBL);
			rules.setBackground(light);
			rules.setForeground(Color.white);
			home = createHomeButton();
			
			// Initialize the three special panels (classes below)
			dealerPanel = new DealerCards();
			playerPanel = new PlayerCards();
			buttonPanel = new PlayButtons();
			
			// Initialize the text fields showing money and bets
			moneyField = new JTextField(5);
			moneyField.setEditable(false);
			moneyField.setBackground(light);
			moneyField.setForeground(Color.white);
			betField = new JTextField (5);
			betField.setBackground(light);
			betField.setForeground(Color.white);
			hintField = new JTextField(12);
			hintField.setEditable(false);
			hintField.setBackground(light);
			hintField.setForeground(Color.white);
			
			// Add listeners to the text field with the bet
			BFL = new BetFieldListener();
			betField.addActionListener(BFL);
			betField.addCaretListener(BFL);
			
			// Set up the group layout for the panel
			layout = new GroupLayout(this);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			setLayout(layout);
			
			// Define the group layout's vertical setup
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addComponent(header)
					.addComponent(sep1)
					.addComponent(blackjack)
					.addComponent(stand)
					.addComponent(ins)
					.addComponent(sep2)
					.addGroup(layout.createParallelGroup()
							.addComponent(dealer)
							.addComponent(dealerPanel))
					.addGroup(layout.createParallelGroup()
							.addComponent(playerCard)
							.addComponent(playerPanel))
					.addComponent(info)
					.addComponent(buttonPanel)
					.addGroup(layout.createParallelGroup()
							.addComponent(money)
							.addComponent(moneyField)
							.addComponent(bet)
							.addComponent(betField))
					.addGroup(layout.createParallelGroup()
							.addComponent(home)
							.addComponent(setup)
							.addComponent(rules)
							.addComponent(hintField)
							.addComponent(hint)));
			
			// Define the group layout's horizontal group
			layout.setHorizontalGroup(layout.createParallelGroup()
					.addComponent(header)
					.addComponent(sep1)
					.addComponent(blackjack)
					.addComponent(stand)
					.addComponent(ins)
					.addComponent(sep2)
					.addGroup(layout.createSequentialGroup()
							.addComponent(dealer)
							.addComponent(dealerPanel))
					.addGroup(layout.createSequentialGroup()
							.addComponent(playerCard)
							.addGap(21)
							.addComponent(playerPanel))
					.addComponent(info)
					.addComponent(buttonPanel)
					.addGroup(layout.createSequentialGroup()
							.addComponent(money)
							.addComponent(moneyField)
							.addComponent(bet)
							.addComponent(betField))
					.addGroup(layout.createSequentialGroup()
							.addComponent(home)
							.addComponent(setup)
							.addComponent(rules)
							.addGap(330)
							.addComponent(hintField)
							.addComponent(hint)));
		}

		/**
		 * Sets up and resets a game
		 */
		public void setupGamePanel() {
			// Displays the bet panel
			buttonPanel.setupBetPanel();
			
			// Resets the dealer and player cards
			dealerPanel.reset();
			playerPanel.reset();
			
			insurance = false;
		}
		
		/**
		 * A panel that handles the graphical display of the cards of the dealer
		 * @author Albert Wilcox
		 *
		 */
		private class DealerCards extends PlayerCards{
			/**
			 * Eclipse insisted I add this
			 */
			private static final long serialVersionUID = 1L;
			
			// A JLabel holding a face down card
			JLabel faceDownCard;
			
			/**
			 * A contructor for the dealer cards panel
			 */
			public DealerCards() {
				// Invoke player cards constructor
				super();
								
				// Set up the JLabel with the face down card image and make it invisible
				URL url = BlackjackGUI.class.getResource("/images/back.png");
				faceDownCard = new JLabel(fixCardImageSize(new ImageIcon(url)));
				faceDownCard.setVisible(false);
				
				// Add the facedown card to the panel
				add(faceDownCard);
			}

			/**
			 * Set whether or not the dealer's first card is visible
			 * @param visible
			 */
			public void setFirstCardVisible(boolean visible) {
				// If the first card should be invisible
				if (visible) {
					// Make the dealer's actual card visible
					getComponent(1).setVisible(true);
					// Make the facedown card invisible
					faceDownCard.setVisible(false);
				// If the first card should not be visible
				}else {
					// Make the dealer's actual card invisible
					getComponent(1).setVisible(false);
					// Make the facedown card visible
					faceDownCard.setVisible(true);
				}
			}
			
			/**
			 * Reset the dealer panel
			 */
			@Override
			public void reset() {
				removeAll();
				add(faceDownCard);
				faceDownCard.setVisible(false);
				revalidate();
			}
		}
		
		/**
		 * A panel that handles the graphical display of the player's cards
		 * @author Albert Wilcox
		 *
		 */
		private class PlayerCards extends JPanel{			
			/**
			 * Eclipse insisted I add this
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * Constructor for the panel
			 */
			public PlayerCards() {
				// Set the size and border
				setPreferredSize(new Dimension(400, 120));
				setBorder(BorderFactory.createEtchedBorder());
				
				setBackground(light);
				
				// Setup layout manager
				FlowLayout playerCardLayout = new FlowLayout(FlowLayout.LEADING);
				setLayout(playerCardLayout);
			}
			
			/**
			 * Add a card to the panel
			 * @param card - which card to add
			 */
			public void addCard(Card card) {
				// Find the card's URL and assign it to a JLabel
				URL url = getCardImageURL(card);
				JLabel temp = new JLabel(fixCardImageSize(new ImageIcon(url)));
				// Add it to the panel and revalidate that panel
				add(temp);
				revalidate();
			}
			
			/**
			 * Reset the card panel
			 */
			public void reset() {
				removeAll();
				revalidate();
			}
		}
		
		/**
		 * A panel that handles the graphical display of a split hand
		 * @author Albert Wilcox
		 *
		 */
		private class SplitPlayerCards extends JPanel{
			/**
			 * Eclipse insisted I add this
			 */
			private static final long serialVersionUID = 1L;
			PlayerCards hand1, hand2;
			Color activeColor, inactiveColor;
			
			public SplitPlayerCards() {
				setBackground (c);
				
				// Set the size and border
				setPreferredSize(new Dimension(400, 250));
				setBorder(BorderFactory.createEtchedBorder());
				
				// Setup layout manager
				BoxLayout splitCardLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
				setLayout(splitCardLayout);
				
				hand1 = new PlayerCards();
				hand2 = new PlayerCards();
				
				add(Box.createRigidArea(new Dimension(0, 5)));
				add(hand1);
				add(Box.createRigidArea(new Dimension(0, 5)));
				add(hand2);
				add(Box.createRigidArea(new Dimension(0, 5)));

				//hand1.setPreferredSize(new Dimension(510, 140));
				//hand2.setPreferredSize(new Dimension(510, 140));
				
				inactiveColor = light;
				activeColor = new Color(64, 137, 81);
				
				setActiveHand(1);
			}
			
			public void addCard (int splitHand, Card card) {
				if (splitHand == 1) {
					hand1.addCard(card);
				} else if (splitHand == 2) {
					hand2.addCard(card);
				}
			}
			
			public void setActiveHand(int activeHand) {
				if (activeHand == 1) {
					hand1.setBackground(activeColor);
					hand2.setBackground(inactiveColor);
				}
				else if (activeHand == 2) {
					hand1.setBackground(inactiveColor);
					hand2.setBackground(activeColor);
				} else {
					hand1.setBackground(inactiveColor);
					hand2.setBackground(inactiveColor);
				}
			}
		}
		
		/**
		 * A panel which displays the game buttons/slider to the player,
		 * and handles what should be displayed when
		 * @author Albert Wilcox
		 *
		 */
		private class PlayButtons extends JPanel{
			/**
			 * Eclipse insisted I have this
			 */
			private static final long serialVersionUID = 1L;
			/*
			 * Aggregate objects
			 */
			JButton bet, next, reset, hit, stand, doub, split, insure;
			JSlider slider;
			JLabel betAmount;
			
			// An integer holding the player's last bet
			int lastBet;
			
			/**
			 * Construct the play buttons panel
			 */
			public PlayButtons() {
				setBackground(c);
				
				// Set up game buttons and give them listeners
				hit = new JButton("Hit");
				hit.addActionListener(new HitButtonListener());
				hit.setBackground(light);
				hit.setForeground(Color.yellow);
				stand = new JButton("Stand");
				stand.addActionListener(new StandButtonListener());
				stand.setBackground(light);
				stand.setForeground(Color.yellow);
				doub = new JButton("Double Down");
				doub.addActionListener(new DoubleButtonListener());
				doub.setBackground(light);
				doub.setForeground(Color.yellow);
				split = new JButton("Split");
				split.addActionListener(new SplitButtonListener());
				split.setBackground(light);
				split.setForeground(Color.yellow);
				insure = new JButton("Insurance");
				insure.addActionListener(new InsuranceButtonListener());
				insure.setBackground(light);
				insure.setForeground(Color.yellow);
				
				// Set up other buttons and give them listeners
				bet = new JButton("Place Bet");
				bet.addActionListener(new BetButtonListener());
				bet.setBackground(light);
				bet.setForeground(Color.yellow);
				next = new JButton("Next");
				next.addActionListener(new NextButtonListener());
				next.setBackground(light);
				next.setForeground(Color.yellow);
				reset = new JButton("New Round");
				reset.addActionListener(new ResetButtonListener());
				reset.setBackground(light);
				reset.setForeground(Color.yellow);
				
				// Set up a label for bet amount
				betAmount = new JLabel("Bet Amount: ");
				betAmount.setForeground(Color.white);
				
				// Give the last bet variable an initial value of 10
				lastBet = 10;
				
				// Set up the slider
				slider = new JSlider(10, 100, lastBet);
				slider.setMajorTickSpacing(10);
				slider.setMinorTickSpacing(5);
				slider.setPaintTicks(true);
				slider.setPaintLabels(true);
				slider.addChangeListener(new SliderListener());
				slider.setBackground(c);
				slider.setForeground(Color.white);
			}
			
			/**
			 * Configure the game buttons panel to handle taking a player's bet
			 */
			public void setupBetPanel() {
				// Update money and bet fields
				moneyField.setText(Integer.toString(Engine.getPlayerMoney()));
				betField.setText(Integer.toString(buttonPanel.lastBet));
				
				// Setup slider, make the bet field editable, and update output text
				slider.setValue(lastBet);
				betField.setEditable(true);
				info.setText("Please place your bet...");
				
				// Reset the split panel
				Component[] comps = gameGUI.getComponents();
				for (Component c: comps) {
					if (c == splitPanel) {
						layout.replace(splitPanel, playerPanel);
						break;
					}
				}
				
				// Remove other components from the panel and add those related to bet placing
				removeAll();
				add(betAmount);
				add(slider);
				add(bet);
				revalidate();
			}
			
			/**
			 * Configure the game buttons panel to handle the end of a player's turn
			 */
			public void setupNextPanel() {
				String s;
				
				if (!Engine.getSplit()) {
					// Find the player's final score and initialize string s
					int playerTotal = Engine.getPlayerHand().getTotal();
					s = "";
				
					// Define s's value and update the info label
					if (playerTotal > 21) s += "You busted!";
					else s += ("You finished with " + Integer.toString(playerTotal) + "!");
					s += " The dealer will now play.";
				}
				else {
					int totalOne = Engine.getSplitHand().getHand(1).getTotal();
					int totalTwo = Engine.getSplitHand().getHand(2).getTotal();
					s = "You finished with ";
					if (totalOne > 21) s += "bust"; else s += Integer.toString(totalOne);
					s += " and ";
					if (totalTwo > 21) s += "bust"; else s += Integer.toString(totalTwo);
					s += ". The dealer will now play.";
				}
				
				info.setText(s);
				
				// Remove other components and add the 'next' button
				removeAll();
				add(next);
				setVisible(false);
				setVisible(true);
				revalidate();
			}
			
			/**
			 * Setup the panel to tell the player how the turn went and prompt them to start new game
			 * @param out - What to tell the player
			 */
			public void setupResetPanel(String out) {
				// Remove other components and add the reset button
				removeAll();
				add(reset);
				setVisible(false);
				setVisible(true);
				
				// Make the dealer's first card visible
				dealerPanel.setFirstCardVisible(true);
				
				// Set the info field to whatever input was given
				info.setText(out);
			}
			
			/**
			 * Setup the panel with ingame buttons
			 * @param first - Whether it is the first round
			 * @param splittable - whether the hand is splittable
			 * @param insurance - whether the player can take insurance
			 */
			public void setupPlayPanel(boolean first, boolean splittable, boolean insurance) {
				// Remove other components and add the hit and stand buttons, which will always be there ingame
				removeAll();
				add(hit);
				add(stand);
				
				// Prompt player
				info.setText("Please choose your move...");
				
				// Determine whether or not to add insurance, split and double down buttons
				if(first) {
					add(doub);
					if (splittable)
						add(split);
					if(insurance) {
						add(insure);
					}
				}
				
				// Refresh panel
				setVisible(false);
				setVisible(true);
				revalidate();
			}
			
			/**
			 * A listener for changes to the bet slider
			 * @author Albert Wilcox
			 */
			private class SliderListener implements ChangeListener{
				/**
				 * Called when the slider is touched
				 */
				@Override
				public void stateChanged(ChangeEvent arg0) {
					// Update the bet field to the slider's value
					betField.setText(Integer.toString(slider.getValue()));
				}
			}
			
			/**
			 * A listener for the 'hit' button
			 * @author Albert Wilcox
			 */
			private class HitButtonListener implements ActionListener{
				/**
				 * Called when the button is pressed
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Go to the game engine and call 'hit' method
					Engine.hit();	

					resetHint();
				}
			}
			
			/**
			 * Listener for 'stand' button
			 * @author Albert Wilcox
			 */
			private class StandButtonListener implements ActionListener{
				/**
				 * Called when the button is pressed
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Go to the engine and call the 'stand' method
					Engine.stand();

					resetHint();
				}
			}
			
			/**
			 * A listener for the 'double down' button
			 * @author Albert Wilcox
			 */
			private class DoubleButtonListener implements ActionListener{
				/**
				 * Called when the button is pressed
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!Engine.getSplit()) {
						// Update the bet field to display the doubled bet
						int origBet = slider.getValue();
						String s = Integer.toString(origBet);
						String s2 = s + " + " + s;
						if (insurance) s2 += (" / Insurance: " + Integer.toString(Engine.getPlayerBet() / 2));
						betField.setText(s2);
					}
					else {
						String s;

						int num = Engine.getSplitHandNum();
						String bet1 = Integer.toString(Engine.getSplitBet(1));
						String bet2 = Integer.toString(Engine.getSplitBet(2));
						if (bet1.equals(bet2)) {
							switch (num) {
							case 1:
								s = "(" + bet1 + " + " + bet1 + ") + " + bet2;
								break;
							case 2:
								s = bet1 + " + (" + bet2 + " + " + bet2 + ")";
								break;
							default:
								s = "Error";	
							}
						} else {
							if (Integer.parseInt(bet1) > Integer.parseInt(bet2)) {
								s = "(" + bet2 + " + " + bet2 + ") + ("+ bet2 + " + " + bet2 + ")";
							} else {
								s = "(" + bet1 + " + " + bet1 + ") + ("+ bet1 + " + " + bet1 + ")";
							}
						}
						
						if (insurance) s += (" / Insurance: " + Integer.toString(Engine.getPlayerBet() / 2));
						
						betField.setText(s);
					}
					
					// Call doubleDown method in engine
					Engine.doubleDown();

					resetHint();
				}
			}
			
			/**
			 * A listener for the 'split' button
			 * @author Albert Wilcox
			 */
			private class SplitButtonListener implements ActionListener{
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Update the bet field to display the doubled bet
					int origBet = slider.getValue();
					String s = Integer.toString(origBet);
					String s2 = s + " + " + s;
					if (insurance) s2 += (" / Insurance: " + Integer.toString(Engine.getPlayerBet() / 2));
					betField.setText(s2);
					
					splitPanel = new SplitPlayerCards();
					
					layout.replace(playerPanel, splitPanel);
					//playerPanel.setVisible(false);
					
					Engine.split();

					resetHint();
				}
			}
			
			/**
			 * Listener for the 'Insurance' button
			 */
			private class InsuranceButtonListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent arg0) {
					insurance = true;
					int bet = Engine.getPlayerBet();
					bet /= 2;
					betField.setText(betField.getText() + " / Insurance: " + Integer.toString(bet));
					
					Engine.insurance();
				}
				
			}
			
			/**
			 * Listener for the 'next' button
			 * @author Albert Wilcox
			 */
			private class NextButtonListener implements ActionListener{
				/**
				 * Called when the button is pressed
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Call playDealerHand method in engine
					Engine.playDealerHand();
					resetHint();
				}
			}
			
			/**
			 * Listener for the 'reset' button
			 * @author Albert Wilcox
			 */
			private class ResetButtonListener implements ActionListener{
				/**
				 * Called when the button is pressed
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Reset the card panels and setup the playbuttons panel for bets
					dealerPanel.reset();
					playerPanel.reset();
					setupBetPanel();
					pack();
				}
			}
			
			/**
			 * Listener for the bet button
			 * @author Albert Wilcox
			 */
			private class BetButtonListener implements ActionListener{
				/**
				 * Called when the button is pressed
				 */
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Find the bet, and assign it to the last bet value
					int bet = slider.getValue();
					lastBet = bet;
					
					// Set the bet field to the last valid value it held
					betField.setText(Integer.toString(BFL.lastValidValue));
					
					// Set the bet in the engine
					Engine.setBet(bet);
					// Call 'deal' method in the engine
					Engine.deal();
					// Make the bet field uneditable
					betField.setEditable(false);
					
					resetHint();
				}
			}
		}
		
		private class HintButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Engine.hint();
			}
		}
		
		// A listener for the things going on with the bet field
		private class BetFieldListener implements ActionListener, CaretListener{
			// Define an integer for the last valid value
			int lastValidValue;
			
			/**
			 * Constructor for the bet field listener
			 */
			public BetFieldListener() {
				// Setup last valid value integer
				lastValidValue = buttonPanel.lastBet;
			}

			/**
			 * Called when someone presses enter while editing text
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Initialize integer i
				int i = 0;
				
				/*
				 * Try to parse the bet field into an integer.
				 */
				try {
					i = Integer.parseInt(betField.getText());
					
					/*
					 * Past here is only executed if the bet field is an integer
					 */
					
					// If the value is below 10 (minimum bet) set it to 10
					if (i < 10) {
						betField.setText("10");
						buttonPanel.slider.setValue(10);
						lastValidValue = 10;
					}
					// If the value is above 100 (maximum bet) set it to 100
					else if (i > 100) {
						betField.setText("100");
						buttonPanel.slider.setValue(100);
						lastValidValue = 100;
					} 
					// If it is a valid value between 10 and 100, update the slider
					else {
						buttonPanel.slider.setValue(i);
					}
				/*
				 * This code is executed if the bet field is not an integer
				 */
				} catch (NumberFormatException e) {
					// Reset the field to the last valid value
					betField.setText(Integer.toString(lastValidValue));
				}
			}

			/**
			 * Called when the field is clicked
			 */
			@Override
			public void caretUpdate(CaretEvent arg0) {
				// Update the last valid value variable
				lastValidValue = buttonPanel.slider.getValue();
			}	
		}
		
		/**
		 * Fixes a card icon's size so it is not too huge
		 * @param original - The original image icon
		 * @return - The fixed image icon
		 */
 		public ImageIcon fixCardImageSize(ImageIcon original) {
			Image image = original.getImage();
			image = image.getScaledInstance(80, 120, Image.SCALE_DEFAULT);
			ImageIcon resultant = new ImageIcon(image);
			return resultant;
		}
		
 		/**
 		 * Finds the image URL for the image of a given card
 		 * @param card - Card to find an image for
 		 * @return - That card's image's URL
 		 */
		private URL getCardImageURL(Card card) {
			String cString = card.toString();
			cString = cString.replace(' ', '_');
			URL url = BlackjackGUI.class.getResource("/images/" + cString + ".png");
			return url;
		}
	
		private void resetHint() {
			hintField.setText("");
		}
	}
	
	/**
	 * A panel which allows the user to have the computer play against itself for stats purpose
	 * @author Albert Wilcox
	 */
	private class StatsGUIPanel extends JPanel{
		/**
		 * Eclipse insisted I add this
		 */
		private static final long serialVersionUID = 1L;
		
		private JLabel header, gamesLabel, betLabel, resultLabel, decksLabel;
		private JButton home, info, play;
		private JSpinner games;
		private JSeparator sep1, sep2;
		private JCheckBox insurance, counting;
		private JTextField result, bet;
		private JSlider betSlider, decks;
		private JProgressBar progress;
				
		GroupLayout layout;
		
		public StatsGUIPanel() {
			header = new JLabel ("Statistics Mode");
			header.setFont(headerFont);
			gamesLabel = new JLabel("Games: ");
			betLabel = new JLabel("Bet: ");
			resultLabel = new JLabel("Resultant Money: ");
			decksLabel = new JLabel("Decks: ");
			
			home = createHomeButton();
			info = new JButton ("Info");
			info.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JOptionPane.showMessageDialog(info, "Allows you to perform statistical\nanalysis on blackjack");	
				}
			});
			play = new JButton ("Play!");
			play.setFont(headerFont);
			play.addActionListener(new PlayButtonListener());
			
			games = new JSpinner(new SpinnerNumberModel(100, 1, 10000000, 10));
			
			sep1 = new JSeparator(JSeparator.HORIZONTAL);
			sep2 = new JSeparator(JSeparator.HORIZONTAL);
			
			insurance = new JCheckBox("Insurance");
			counting = new JCheckBox("Card Counting");
			
			result = new JTextField(7);
			result.setEditable(false);
			
			bet = new JTextField (1);
			bet.setText("10");
			BetFieldListener betList = new BetFieldListener();
			bet.addActionListener(betList);
			bet.addCaretListener(betList);
			
			betSlider = new JSlider(10, 100, 10);
			betSlider.setMajorTickSpacing(10);
			betSlider.setMinorTickSpacing(5);
			//slider.setPaintTicks(true);
			betSlider.setPaintLabels(true);
			betSlider.addChangeListener(new SliderListener());
			
			decks = new JSlider(1, 8, 4);
			decks.setMajorTickSpacing(1);
			decks.setPaintTicks(true);
			decks.setPaintLabels(true);
			
			progress = new JProgressBar(SwingConstants.HORIZONTAL, 1, 100);
			//progress.setValue(50);
						
			// Set up the group layout for the panel
			layout = new GroupLayout(this);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			setLayout(layout);
			
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addComponent(header)
					.addComponent(sep1)
					.addGroup(layout.createParallelGroup()
							.addComponent(gamesLabel)
							.addComponent(games))
					.addGroup(layout.createParallelGroup()
							.addComponent(betLabel)
							.addComponent(bet)
							.addComponent(betSlider))
					.addGroup(layout.createParallelGroup()
							.addComponent(decksLabel)
							.addComponent(decks))
					.addGroup(layout.createParallelGroup()
							.addGroup(layout.createSequentialGroup()
									.addComponent(insurance)
									.addComponent(counting))
							.addComponent(play))
					.addComponent(sep2)
					.addGroup(layout.createParallelGroup()
							.addComponent(home)
							.addComponent(info)
							//.addGap(100)
							.addComponent(resultLabel)
							.addComponent(result))
					.addComponent(progress));
			
			layout.setHorizontalGroup(layout.createParallelGroup()
					.addComponent(header)
					.addComponent(sep1)
					.addGroup(layout.createSequentialGroup()
							.addComponent(gamesLabel)
							.addComponent(games))
					.addGroup(layout.createSequentialGroup()
							.addComponent(betLabel)
							.addComponent(bet)
							.addComponent(betSlider))
					.addGroup(layout.createSequentialGroup()
							.addComponent(decksLabel)
							.addComponent(decks))
					.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
									.addComponent(insurance)
									.addComponent(counting))
							.addGap(225)
							.addComponent(play))
					.addComponent(sep2)
					.addGroup(layout.createSequentialGroup()
							.addComponent(home)
							.addComponent(info)
							.addGap(75)
							.addComponent(resultLabel)
							.addComponent(result))
					.addComponent(progress));
		}
		
		/* A listener for changes to the bet slider
		 * @author Albert Wilcox
		 */
		private class SliderListener implements ChangeListener{
			/**
			 * Called when the slider is touched
			 */
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// Update the bet field to the slider's value
				bet.setText(Integer.toString(betSlider.getValue()));
			}
		}
		
		// A listener for the things going on with the bet field
		private class BetFieldListener implements ActionListener, CaretListener{
			// Define an integer for the last valid value
			int lastValidValue;
			
			/**
			 * Constructor for the bet field listener
			 */
			public BetFieldListener() {
				// Setup last valid value integer
				lastValidValue = 10;
			}
				/**
			 * Called when someone presses enter while editing text
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Initialize integer i
				int i = 0;
				
				/*
				 * Try to parse the bet field into an integer.
				 */
				try {
					i = Integer.parseInt(bet.getText());
					
					/*
					 * Past here is only executed if the bet field is an integer
					 */
					
					// If the value is below 10 (minimum bet) set it to 10
					if (i < 10) {
						bet.setText("10");
						betSlider.setValue(10);
						lastValidValue = 10;
					}
					// If the value is above 100 (maximum bet) set it to 100
					else if (i > 100) {
						bet.setText("100");
						betSlider.setValue(100);
						lastValidValue = 100;
					} 
					// If it is a valid value between 10 and 100, update the slider
					else {
						betSlider.setValue(i);
					}
				/*
				 * This code is executed if the bet field is not an integer
				 */
				} catch (NumberFormatException e) {
					// Reset the field to the last valid value
					bet.setText(Integer.toString(lastValidValue));
				}
			}
			/**
			 * Called when the field is clicked
			 */
			@Override
			public void caretUpdate(CaretEvent arg0) {
				// Update the last valid value variable
				lastValidValue = betSlider.getValue();
			}	
		}
	
		private class PlayButtonListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				MiniThread t = new MiniThread();
				
				play.setEnabled(false);
				t.start();
			}
			
			private class MiniThread extends Thread{
				int threadResult;
				int betInt, gameInt, deckInt;
				boolean insure, countBoolean;
								
				public MiniThread() {
					betInt = betSlider.getValue();
					gameInt = Integer.parseInt(games.getValue().toString());
					deckInt = decks.getValue();
					insure = insurance.isSelected();
					countBoolean = counting.isSelected();
				}
				
				@Override
				public void run() {
					threadResult = Engine.autoPlay(betInt, gameInt, deckInt, insure, countBoolean);
					result.setText(Integer.toString(threadResult));
					play.setEnabled(true);
				}
			}
			
		}
		
		
	}
	
	/**
	 * A listener for the home button
	 * @author Albert Wilcox
	 */
	private class HomeButtonListener implements ActionListener{
		/**
		 * Calld whenever a home button is pressed
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Set the panel to the home panel
			changePanel(PanelType.HOME);
		}	
	}
	
	/**
	 * Listens to the 'Setup' button and 'Play Mode' Button
	 * @author Albert Wilcox
	 */
	private class SetupButtonListener implements ActionListener{
		/**
		 * Called whenever either button is pressed
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Change to the stup panel
			changePanel(PanelType.SETUP);
		}
	}

	/**
	 *  Called whenever the 'Rules' button is pressed
	 * @author Albert Wilcox
	 */
	private class RuleButtonListener implements ActionListener{
		// Interestingly enough this button contains a JFrame, and here it is
		JFrame ruleFrame;
		
		/**
		 * Called when the rules button is pressed.
		 * Creates a scrollable JFrame with the rules
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			/*
			 * Setup frame, panel, text area, scroll pane, and close button in that order
			 */
			ruleFrame = new JFrame("Rules");
			ruleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JPanel panel = new JPanel();
			JTextArea rules = new JTextArea(1, 30);
			//JScrollPane scroll = new JScrollPane(rules, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			//JLabel label = new JLabel(new ImageIcon("src\\images\\rick_roll.jpg"));
			
			
			JButton close = new JButton("Close");
			close.addActionListener(new CloseButtonListener());
			
			// Set the layout
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			
			// Configure the rules text area
			rules.setEditable(false);
			rules.setFont(new Font("SansSerif", Font.PLAIN, 12));
			
			// Add the rules string
			rules.setText(" Welcome to blackjack!"
					+ "\n"
					+ "\n The game will be played with normal blackjack rules,"
					+ "\n and the following will be the only deviations from them."
					+ "\n  - You may not split a hand that has already been split"
					+ "\n  - You may double on a split hand"
					+ "\n  - You may not surrender"
					+ "\n  - You may not bet more than $100"
					+ "\n  - You may not bet less than $10");
			
			//Add the text area and close button to the panel
			panel.add(rules);
			//panel.add(label);
			panel.add(close);
			
			//Add the panel to the frame and make it visible
			ruleFrame.getContentPane().add(panel);
			ruleFrame.pack();
			ruleFrame.setVisible(true);
		}
		
		/**
		 * Listener for the 'Close' button
		 * @author Albert Wilcox
		 */
		private class CloseButtonListener implements ActionListener{
			/**
			 * Called whenever the 'Close' button is pressed
			 */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Get rid of the Rules Frame
				ruleFrame.dispose();
			}
		}
	}
}
