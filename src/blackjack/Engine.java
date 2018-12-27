/*****************************************************
 * Albert Wilcox
 * IB Computer Science
 * 
 * Engine class (Main)
 ****************************************************/
package blackjack;

import wilcox.cards.*;

public class Engine {
	enum HandTypes{DEALER, PLAYER, SPLIT_ONE, SPLIT_TWO}
	
	private static BlackjackGUI gui;
	
	private static int playerMoney;
	private static int playerBet;
	
	private static Deck deck;
	
	private static Hand playerHand;
	private static Hand dealerHand;
	private static SplitHand splitHand;
	
	private static int splitHandNum = 0;
	private static int splitBetOne = 0;
	private static int splitBetTwo = 0;
	private static int insuranceBet = 0;
	
	public static void main (String[] args) {
		gui = new BlackjackGUI();
	}
	
	public static BlackjackGUI getGUI() {
		return gui;
	}
	
	public static void setupGame(int money, int deckCountIn) {
		deck = new Deck(deckCountIn);
		deck.shuffle();
		playerMoney = money;
		
		playerHand = new Hand(HandTypes.PLAYER);
		dealerHand = new Hand(HandTypes.DEALER);
	}
	
	public static void setMoney(int money) {
		playerMoney = money;
	}
	
	public static void setBet(int bet) {
		playerBet = bet;
	}
	
	public static int getPlayerMoney() {
		return playerMoney;
	}

	public static int getPlayerBet() {
		return playerBet;
	}
	
	public static void hit() {
		Card card = deck.draw();
		if(splitHandNum == 0) {
			playerHand.addCard(card);
		
			if (playerHand.getTotal() > 21) finishTurn()/*gui.updateGamePanel(true, false, false, false)*/;
			else gui.updateGamePanel(false, false, false, false);
		}
		else {
			splitHand.addCard(splitHandNum, card);
			if (splitHand.getHand(splitHandNum).getTotal() >= 21) stand();
			else gui.updateGamePanel(false, false, false, false);
		}
		gui.pack();
		gui.revalidate();
	}
	
	public static void stand() {
		if (splitHandNum == 0 || splitHandNum == 2) {
			gui.updateGamePanel(true, false, false, false);
			if (splitHandNum == 2) gui.setActiveSplitPanel(0);
			gui.pack();
		} else if (splitHandNum == 1) {
			gui.setActiveSplitPanel(2);
			splitHandNum = 2;
			gui.updateGamePanel(false, true, false, false);
		}
	}
	
	public static void doubleDown() {
		if (splitHandNum == 0) {
			playerBet *= 2;
		
			Card card = deck.draw();
			playerHand.addCard(card);
		
			if (playerHand.getTotal() > 21) finishTurn(); 
			else playDealerHand();
		
			gui.pack();
		} else {
			if (splitHandNum == 1) splitBetOne *= 2;
			else splitBetTwo *= 2;
			
			Card card = deck.draw();
			splitHand.addCard(splitHandNum, card);
			
			stand();
		}
	}
	
	public static void split() {
		splitBetOne = playerBet;
		splitBetTwo = playerBet;
		splitHandNum = 1;
		
		splitHand = new SplitHand();
		splitHand.addCard(1, playerHand.get(0));
		splitHand.addCard(2, playerHand.get(1));
		
		splitHand.addCard(1, deck.draw());
		splitHand.addCard(2, deck.draw());
		
		gui.updateGamePanel(false, true, false, false);
		
		gui.pack();
	}
	
	public static void insurance() {
		insuranceBet = playerBet / 2;
	}
	
	public static void deal(){
		playerHand.reset();
		dealerHand.reset();
				
		splitHandNum = 0;
		splitBetOne = 0;
		splitBetTwo = 0;
		insuranceBet = 0;
		
		deck.shuffle();
		
		//playerHand.addCard(new Card(20));
		//playerHand.addCard(new Card(20));
		playerHand.addCard(deck.draw());
		playerHand.addCard(deck.draw());
		dealerHand.addCard(deck.draw());
		dealerHand.addCard(deck.draw());
		//dealerHand.addCard(new Card(50));
		//dealerHand.addCard(new Card(1));
		
		gui.setDealerFirstCardVisible(false);
		gui.pack();
		
		boolean splittable = false, insurance = false;
		if (dealerHand.get(1).getValue() == Value.ACE) insurance = true;
		if (playerHand.getSplittable()) splittable = true;
		
		gui.updateGamePanel(false, true, splittable, insurance);
		
		gui.revalidate();
		gui.pack();
		//gui.revalidate();
	}
	
	public static void hint() {
		Hand playHand;
		
		if (splitHandNum == 0) {
			playHand = getPlayerHand();
		} else {
			playHand = getSplitHand().getHand(splitHandNum);
		}
		
		Card dealerUp = Engine.getDealerHand().get(1);
		Move move = PlayerDecisionMaker.decision(playHand, dealerUp);
		
		gui.setHint(move);
	}
	
	public static void playDealerHand() {
		gui.setDealerFirstCardVisible(true);
		
		while (dealerHand.getTotal() < 17) {
			Card temp = deck.draw();
			dealerHand.addCard(temp);
		}
		finishTurn();
	}
	
	public static void finishTurn() {
		if (splitHandNum == 0) {
			int playerTotal = getPlayerHand().getTotal();
			int dealerTotal = getDealerHand().getTotal();
			int playerSize = getPlayerHand().getSize();
			int dealerSize = getDealerHand().getSize();
		
			boolean dealerBusted = false, playerBusted = false, dealerBlackjack = false, playerBlackjack = false;
		
			if (playerTotal > 21) {
				playerBusted = true;
			}
			if (dealerTotal > 21) {
				dealerBusted = true;
			}
			if (playerTotal == 21 && playerSize == 2) {
				playerBlackjack = true;
			}
			if (dealerTotal == 21 && dealerSize == 2) {
				dealerBlackjack = true;
			}
		
			String s = "If you're reading this something went horribly wrong";
		
			// If neither player busted
			if (!playerBusted && !dealerBusted) {
				// If the player won with blackjack
				if (playerBlackjack && !dealerBlackjack) {
					s = "You got blackjack, and won 3:2...";
					playerMoney += ((playerBet * 3) / 2);
				// If the dealer won with blackjack
				} else if (dealerBlackjack && !playerBlackjack) {
					s = "The dealer got blackjack, so you lost...";
					playerMoney -= playerBet;
				// If the player and dealer got blackjack
				} else if (playerBlackjack && dealerBlackjack) {
					s = "Awkwardly enough, you and the dealer both have blackjack...";
				// If the player won without blackjack
				} else if (playerTotal > dealerTotal) {
					s = "You beat the dealer's total of " + Integer.toString(dealerTotal) + " with your score of " + Integer.toString(playerTotal) + "...";
					playerMoney += playerBet;
				// If the dealer won without blackjack
				} else if (dealerTotal > playerTotal){
					s = "The dealer beat your total of " + Integer.toString(playerTotal) + " with his score of " + Integer.toString(dealerTotal) + "...";
					playerMoney -= playerBet;
				// If there's a tie
				} else if (dealerTotal == playerTotal) {
					s = "You tied with the dealer, resulting in a push...";
				}
			// If the player busted
			} else if (playerBusted) {
				s = "You busted and lost automatically...";
				playerMoney -= playerBet;
			// If the dealer busted
			} else if (dealerBusted) {
				s = "The dealer busted and you won...";
				playerMoney += playerBet;
			}
		
			if (dealerBlackjack && insuranceBet != 0) {
				s += "You won your insurance bet.";
				playerMoney += insuranceBet * 2;
			}
			
			gui.setupResetButton(s);
		}
		else { 
			int dealerTotal = dealerHand.getTotal();
			int dealerSize = dealerHand.getSize();
			
			int splitOneTotal = splitHand.getHand(1).getTotal();
			int splitOneSize = splitHand.getHand(1).getSize();
			
			int splitTwoTotal = splitHand.getHand(2).getTotal();
			int splitTwoSize = splitHand.getHand(2).getSize();
			
			int reward = 0;
			
			boolean dealerBlackjack = false, splitOneBlackjack = false, splitTwoBlackjack = false;
			boolean dealerBust = false, splitOneBust = false, splitTwoBust = false;
			boolean splitOneWon = false, splitTwoWon = false, splitOnePush = false, splitTwoPush = false;
			
			if (dealerTotal == 21 && dealerSize == 2) dealerBlackjack = true;
			if (splitOneTotal == 21 && splitOneSize == 2) splitOneBlackjack = true;
			if (splitTwoTotal == 21 && splitTwoSize == 2) splitTwoBlackjack = true;
			
			if (dealerTotal > 21) dealerBust = true;
			if (splitOneTotal > 21) splitOneBust = true;
			if (splitTwoTotal > 21) splitTwoBust = true;
			
			if (!splitOneBust && !dealerBlackjack && (splitOneTotal > dealerTotal || dealerBust)) splitOneWon = true;
			else if (splitOneTotal == dealerTotal || splitOneBlackjack) splitOnePush = true;
			if (!splitTwoBust && !dealerBlackjack && (splitTwoTotal > dealerTotal || dealerBust)) splitTwoWon = true;
			else if (splitTwoTotal == dealerTotal || splitTwoBlackjack) splitTwoPush = true;
			
			String s = "";
			
			/*
			 * Process dealer results
			 */
			s += "Dealer ";
			if (dealerBust) {
				s += "busted. ";
			}
			else if (dealerBlackjack) {
				s += "had Blackjack. ";
			}
			else {
				s = s + "had " + Integer.toString(dealerTotal) + ". ";
			}
		
			/*
			 * Process player hand 1 results
			 */
			s += "Hand one ";
			if (splitOnePush) {
				s += "tied with ";
			}
			else if (splitOneWon) {
				s += "won with ";
				if (splitOneBlackjack) reward += (splitBetOne * 3) / 2;
				else reward += splitBetOne;
			}
			else {
				s += "lost with ";
				reward -= splitBetOne;
			}
			s += Integer.toString(splitOneTotal);
			s += ". ";
			
			/*
			 * Process player hand 2 results
			 */
			s += "Hand two ";
			if (splitTwoPush) {
				s += "tied with ";
			}
			else if (splitTwoWon) {
				s += "won with ";
				if (splitTwoBlackjack) reward += (splitBetTwo * 3) / 2;
				else reward += splitBetTwo;
			}
			else {
				s += "lost with ";
				reward -= splitBetTwo;
			}
			s += Integer.toString(splitTwoTotal);
			s += ". ";
			
			if (dealerBlackjack && insuranceBet != 0) {
				s += "You won your insurance bet.";
				playerMoney += insuranceBet * 2;
			}
			
			playerMoney += reward;
			gui.setupResetButton(s);
		}
	}
	
	public static int autoPlay(int bet, int games, int deckCount, boolean insure, boolean counting) {
		// Resultant money gain/loss
		int result = 0;
		
		int gamesWon = 0, gamesLost = 0, gamesPushed = 0, insuranceWon = 0, insuranceLost = 0, splits = 0;
		
		// Setting up deck and hands
		Deck deck = new Deck(deckCount);
		deck.shuffle();
		Hand playerHand = new Hand(HandTypes.PLAYER, false);
		Hand dealerHand = new Hand(HandTypes.DEALER, false);
		Hand splitHand1 = new Hand(HandTypes.SPLIT_ONE, false);
		Hand splitHand2 = new Hand(HandTypes.SPLIT_TWO, false);
		
		// Other necessary variables
		int count = 0;
		int rCount = 0;
		boolean turnOver;
		
		// Determining when to shuffle
		int fullCards = deckCount * 52;
		int shufflePoint = fullCards / 4;
		if (shufflePoint < 26) shufflePoint = 26;
		
		int thisBet, splitBet1 = 0, splitBet2 = 0;
		
		/*
		 * Loop that goes through the inputed amount of games and autoplays them
		 */
		for (int i = 0; i < games; i++) {
			//System.out.println("New hand");
			
			// If needed, shuffle. Shuffling resets the card count
			if (deck.getCount() < shufflePoint) {
				deck.shuffle();
				count = 0;
				//System.out.println("Shuffling");
			}
			
			// Reset hands
			playerHand.reset();
			dealerHand.reset();
			splitHand1.reset();
			splitHand2.reset();
			
			// Deal cards
			playerHand.addCard(deck.draw());
			playerHand.addCard(deck.draw());
			dealerHand.addCard(deck.draw());
			dealerHand.addCard(deck.draw());
			
			if (counting) {
				int dealer1Value = dealerHand.get(0).getValue().toInt();
				int dealer2Value = dealerHand.get(1).getValue().toInt();
				int card1Value = playerHand.get(0).getValue().toInt();
				int card2Value = playerHand.get(1).getValue().toInt();
				
				count += count(dealer1Value);
				count += count(dealer2Value);
				count += count(card1Value);
				count += count(card2Value);
			}
			
			// Give value to other variables
			Card dealerUpCard = dealerHand.get(1);
			turnOver = false;
			boolean split = false;
			thisBet = bet;
			
			// Changing bet according to count
			if (counting) {
				//System.out.println("Count: " + count);
				//System.out.println("Remaining Decks: " + ((deck.getCount() / 52) + 1));
				float realCount = (float)count / ((float)(deck.getCount() / 52) + 1);
				//System.out.println(/*"Running Count: " + count + "\nRemaining decks: " + remainingDecks + */"\nRC: " + realCount);
				if (realCount < 0) {
					thisBet /= (-1 * (realCount - 1));
				} else if (realCount > 0) {
					float factor = (float) (realCount * .5);
					factor += 1;
					float newBet = thisBet * factor;
					thisBet = (int) newBet;
				}
				
				rCount = (int) realCount;
				//System.out.println("Betting " + thisBet);
			}
			
			// Handle insurance
			if (insure && dealerUpCard.getValue() == Value.ACE) {
				if (dealerHand.get(0).getValue().toInt() >= 10) {
					result += (bet * 2);
				} else {
					result -= bet;
				}
			}
			
			// Loops until the hand is finished being executed
			do {
				// Find what move to do
				Move move;
				if (!counting) move = PlayerDecisionMaker.decision(playerHand, dealerUpCard);
				else move = PlayerDecisionMaker.decision(playerHand, dealerUpCard, rCount);
				//Move move = PlayerDecisionMaker.decision(playerHand, dealerUpCard);
				Card temp;
				
				// Act based on that move
				switch (move) {
				case STAND:
					turnOver = true;
					break;
				case HIT:
					temp = deck.draw();
					playerHand.addCard(temp);
					count += count(temp.getValue().toInt());
					break;
				case DOUBLE_DOWN:
					temp = deck.draw();
					thisBet *= 2;
					playerHand.addCard(temp);
					count += count(temp.getValue().toInt());
					turnOver = true;
					break;
				case SPLIT:
					// Add cards to the two split hands
					splitHand1.addCard(playerHand.get(0));
					splitHand1.addCard(deck.draw());
					splitHand2.addCard(playerHand.get(1));
					splitHand2.addCard(deck.draw());
					
					count += count (splitHand1.get(1).getValue().toInt());
					count += count (splitHand2.get(1).getValue().toInt());
					
					// Set up bet variables
					splitBet1 = bet;
					splitBet2 = bet;
					
					// Other variables
					boolean turn1Over = false;
					boolean turn2Over = false;
					
					split = true;
					
					// Loops through execution of the first hand
					do {
						Move move1;
						if (!counting) move1 = PlayerDecisionMaker.decision(splitHand1, dealerUpCard);
						else move1 = PlayerDecisionMaker.decision(splitHand1, dealerUpCard, rCount);
						//Move move1 = PlayerDecisionMaker.decision(playerHand, dealerUpCard);
						
						switch (move1) {
						case STAND:
							turn1Over = true;
							break;
						case HIT:
							temp = deck.draw();
							splitHand1.addCard(temp);
							count += count(temp.getValue().toInt());
							break;
						case DOUBLE_DOWN:
							splitBet1 *= 2;
							temp = deck.draw();
							splitHand1.addCard(temp);
							count += count(temp.getValue().toInt());
							turn1Over = true;
							break;
						default:
							//System.out.println("Something's gone horribly wrong");
							turn1Over = true;
							break;
						}
					} while (!turn1Over);
					
					// Loops through execution of the second hand
					do {
						Move move2;
						if (!counting) move2 = PlayerDecisionMaker.decision(splitHand2, dealerUpCard);
						else move2 = PlayerDecisionMaker.decision(splitHand2, dealerUpCard, rCount);
						//Move move2 = PlayerDecisionMaker.decision(playerHand, dealerUpCard);

						switch (move2) {
						case STAND:
							turn2Over = true;
							break;
						case HIT:
							temp = deck.draw();
							splitHand2.addCard(temp);
							count += count(temp.getValue().toInt());
							break;
						case DOUBLE_DOWN:
							splitBet2 *= 2;
							temp = deck.draw();
							splitHand2.addCard(temp);
							count += count(temp.getValue().toInt());
							turn2Over = true;
							break;
						default:
							//System.out.println("Something's gone horribly wrong");
							turn2Over = true;
							break;
						}
					} while (!turn2Over);
					
					turnOver = true;
					
					break;
				default:
					//System.out.println("Something went wrong");
					turnOver = true;
					break;
				}
			} while (!turnOver);
		
			while (dealerHand.getTotal() < 17) {
				//System.out.println("dealer drawing");
				Card temp = deck.draw();
				dealerHand.addCard(temp);
				count += count(temp.getValue().toInt());
			}
			
			/*
			 * Calculating the payout for an unsplit hand
			 */
			if (!split) {
				int playerTotal = playerHand.getTotal();
				int dealerTotal = dealerHand.getTotal();
				int playerSize = playerHand.getSize();
				int dealerSize = dealerHand.getSize();
			
				boolean dealerBusted = false, playerBusted = false, dealerBlackjack = false, playerBlackjack = false;
			
				if (playerTotal > 21) {
					playerBusted = true;
				}
				if (dealerTotal > 21) {
					dealerBusted = true;
				}
				if (playerTotal == 21 && playerSize == 2) {
					playerBlackjack = true;
				}
				if (dealerTotal == 21 && dealerSize == 2) {
					dealerBlackjack = true;
				}
			
			
				// If neither player busted
				if (!playerBusted && !dealerBusted) {
					// If the player won with blackjack
					if (playerBlackjack && !dealerBlackjack) {
						//System.out.println("won w bj");
						result += ((thisBet * 3) / 2);
						gamesWon++;
					// If the dealer won with blackjack
					} else if (dealerBlackjack && !playerBlackjack) {
						//System.out.println("lost to bj");
						result -= thisBet;
						gamesLost++;
					// If the player and dealer got blackjack
					} else if (playerBlackjack && dealerBlackjack) {
						//System.out.println("bj tie");
						gamesPushed++;
					// If the player won without blackjack
					} else if (playerTotal > dealerTotal) {
						//System.out.println("won");
						result += thisBet;
						gamesWon++;
					// If the dealer won without blackjack
					} else if (dealerTotal > playerTotal){
						//System.out.println("lost");
						result -= thisBet;
						gamesLost++;
					// If there's a tie
					} else if (dealerTotal == playerTotal) {
						//System.out.println("tie");
						gamesPushed++;
					}
				// If the player busted
				} else if (playerBusted) {
					//System.out.println("lost w bust");
					gamesLost++;
					result -= thisBet;
				// If the dealer busted and player didnt
				} else if (dealerBusted) {
					//System.out.println("won to bust");
					gamesWon++;
					result += thisBet;
				}
			}
			
			/*
			 * Calculating payout for a split hand
			 */
			else { 
				splits++;
				
				//System.out.println("split");
				
				int dealerTotal = dealerHand.getTotal();
				int dealerSize = dealerHand.getSize();
				
				int splitOneTotal = splitHand1.getTotal();
				int splitOneSize = splitHand1.getSize();
				
				int splitTwoTotal = splitHand2.getTotal();
				int splitTwoSize = splitHand2.getSize();
								
				boolean dealerBlackjack = false, splitOneBlackjack = false, splitTwoBlackjack = false;
				boolean dealerBust = false, splitOneBust = false, splitTwoBust = false;
				boolean splitOneWon = false, splitTwoWon = false, splitOnePush = false, splitTwoPush = false;
				
				if (dealerTotal == 21 && dealerSize == 2) dealerBlackjack = true;
				if (splitOneTotal == 21 && splitOneSize == 2) splitOneBlackjack = true;
				if (splitTwoTotal == 21 && splitTwoSize == 2) splitTwoBlackjack = true;
				
				if (dealerTotal > 21) dealerBust = true;
				if (splitOneTotal > 21) splitOneBust = true;
				if (splitTwoTotal > 21) splitTwoBust = true;
				
				if (!splitOneBust && !dealerBlackjack && (splitOneTotal > dealerTotal || dealerBust)) splitOneWon = true;
				else if (splitOneTotal == dealerTotal || splitOneBlackjack) splitOnePush = true;
				if (!splitTwoBust && !dealerBlackjack && (splitTwoTotal > dealerTotal || dealerBust)) splitTwoWon = true;
				else if (splitTwoTotal == dealerTotal || splitTwoBlackjack) splitTwoPush = true;
			
				/*
				 * Process player hand 1 results
				 */
				if (splitOneWon) {
					//System.out.println("won hand 1");
					gamesWon++;
					if (splitOneBlackjack) result += (splitBet1 * 3) / 2;
					else result += splitBet1;
				}
				else if (!splitOnePush){
					//System.out.println("lost hand 1");
					gamesLost++;
					result -= splitBet1;
				} else gamesPushed++;
				
				/*
				 * Process player hand 2 results
				 */
				if (splitTwoWon) {
					//System.out.println("won hand 2");
					gamesWon++;
					if (splitTwoBlackjack) result += (splitBet2 * 3) / 2;
					else result += splitBet2;
				}
				else if (!splitTwoPush){
					//System.out.println("lost hand 2");
					gamesLost++;
					result -= splitBet2;
				} else gamesPushed++;
				
			}
		
			//System.out.println("Game " + (i + 1) + " finished with " + result);
			int percentComplete = (i * 100) / games;
			////System.out.println(percentComplete);
			gui.updateStatusBar(percentComplete);
		}
		
		//System.out.println("Wins: " + gamesWon);
		//System.out.println("Lost: " + gamesLost);
		//System.out.println("Ties: " + gamesPushed);
		//System.out.println("Insurance Won: " + insuranceWon);
		//System.out.println("Insurance Lost: " + insuranceLost);
		//System.out.println("Splits: " + splits);
		
		gui.updateStatusBar(0);
		return result;
	}
	
	public static int count(int cardValue) {
		////System.out.println("Counting a " + cardValue);
		
		if (cardValue > 10) cardValue = 10;
		if (cardValue == 1) cardValue = 11;
		
		if (cardValue <= 6) {
			//System.out.println("+1");
			return 1;
		}
		if (cardValue >= 10) {
			//System.out.println("-1");
			return -1;
		}
		//System.out.println(0);
		return 0;
	}
	
	public static boolean getSplit() {
		if (splitHandNum == 0) return false;
		else return true;
	}
	
	public static int getSplitHandNum() {
		return splitHandNum;
	}
	
	public static int getSplitBet(int num) {
		switch(num) {
		case 1: 
			return splitBetOne;
		case 2:
			return splitBetTwo;
		default:
			return 109887461;
		}
	}
	
	public static Hand getPlayerHand() {
		return playerHand;
	}
	
	public static Hand getDealerHand() {
		return dealerHand;
	}

	public static SplitHand getSplitHand() {
		return splitHand;
	}
}