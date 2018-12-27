/*****************************************************
 * Albert Wilcox
 * IB Computer Science
 * 
 * PlayerDecisionMaker class
 ****************************************************/
package blackjack;

import wilcox.cards.Card;

public class PlayerDecisionMaker {
	
	/**
	 * Makes a decision without a card count
	 * @param playerHand - The player's hand
	 * @param dealerUpCard - The dealer's up card
	 * @return - The best decision for the situation
	 */
	public static Move decision(Hand playerHand, Card dealerUpCard) {
		Move move = Move.ERROR;
		boolean soft = playerHand.getSoftness();
		boolean splittable = playerHand.getSplittable();
		int playerTotal = playerHand.getTotal();
		int dealerValue = dealerUpCard.getValue().toInt();
		if (dealerValue > 10) dealerValue = 10;
		if (dealerValue == 1) dealerValue = 11;
		////System.out.println(new String(Boolean.toString(soft) +"/"+ Boolean.toString(splittable) +"/"+ playerTotal +"/"+dealerValue));
		
		/*
		 * Deciding for regular hands
		 */
		if (!soft && !splittable) {
			////System.out.println("Reg hand");
			move = Move.HIT;
			if (playerTotal <= 8) {
				move = Move.HIT;
			} 
			else if (playerTotal == 9) {
				if (dealerValue <= 6 && dealerValue >= 3) {
					move = Move.DOUBLE_DOWN;
				}
			}
			else if (playerTotal == 10) {
				if (dealerValue <= 9) {
					move = Move.DOUBLE_DOWN;
				}
			}
			else if (playerTotal == 11) {
				if (dealerValue <= 10) {
					move = Move.DOUBLE_DOWN;
				}
			}
			else if (playerTotal == 12) {
				if (dealerValue <= 6 && dealerValue >= 4) {
					move = Move.STAND;
				}
			}
			else if (playerTotal <= 16) {
				if (dealerValue <= 6) {
					move = Move.STAND;
				}
			} else {
				move = Move.STAND;
			}
		}
		/*
		 * Deciding for splittable hands
		 */
		else if (splittable) {
			////System.out.println("split hand");
			int value = playerHand.get(0).getValue().toInt();
			if (value == 1) {
				move = Move.SPLIT;
			}
			else if (value <= 3) {
				if (dealerValue >= 4 && dealerValue <= 7) {
					move = Move.SPLIT;
				} else {
					move = Move.HIT;
				}
			}
			else if (value == 4) {
				move = Move.HIT;
			}
			else if (value == 5) {
				if (dealerValue <= 9) {
					move = Move.DOUBLE_DOWN;
				} else {
					move = Move.HIT;
				}
			}
			else if (value == 6) {
				if (dealerValue >= 3 && dealerValue <= 6) {
					move = Move.SPLIT;
				} else {
					move = Move.HIT;
				}
			}
			else if (value == 7) {
				if (dealerValue <= 7) {
					move = Move.SPLIT;
				} else {
					move = Move.HIT;
				}
			}
			else if (value == 8) {
				move = Move.SPLIT;
			}
			else if (value == 9) {
				if (dealerValue <= 9 && dealerValue != 7) {
					move = Move.SPLIT;
				} else {
					move = Move.STAND;
				}
			}
			else {
				move = Move.STAND;
			}
		}
		/*
		 * Deciding for ace hands
		 */
		else {
			////System.out.println("ace hand");
			if (playerTotal <= 14) {
				if (dealerValue == 5 || dealerValue == 6) {
					move = Move.DOUBLE_DOWN;
				} else {
					move = Move.HIT; 
				}
			} 
			else if (playerTotal <= 16) {
				if (dealerValue <= 6 && dealerValue >= 4) {
					move = Move.DOUBLE_DOWN;
				} else {
					move = Move.HIT;
				}
			}
			else if (playerTotal == 17) {
				if (dealerValue <= 6 && dealerValue >= 3) {
					move = Move.DOUBLE_DOWN;
				} else {
					move = Move.HIT;
				}
			}
			else if (playerTotal == 18) {
				if (dealerValue <= 6 && dealerValue >= 3) {
					move = Move.DOUBLE_DOWN;
				} else if (dealerValue >= 9) {
					move = Move.HIT;
				} else {
					move = Move.STAND;
				}
			}
			else {
				move = Move.STAND;
			}
		}
		
		if (move == Move.DOUBLE_DOWN && playerHand.getSize() != 2) {
			if (playerTotal == 18 || playerTotal == 19) {
				move = Move.STAND;
			} else {
				move = Move.HIT;
			}
		}
		
		////System.out.println(move);
		
		return move;
	}
	
	/**
	 * Makes a decision with a given card count
	 * @param playerHand - The player's hand
	 * @param dealerUpCard - The dealer's up card
	 * @param count - The running count
	 * @return - The best decision to make
	 */
	public static Move decision(Hand playerHand, Card dealerUpCard, int count) {
		Move move = Move.ERROR;
		boolean soft = playerHand.getSoftness();
		boolean splittable = playerHand.getSplittable();
		int playerTotal = playerHand.getTotal();
		int dealerValue = dealerUpCard.getValue().toInt();
		if (dealerValue > 10) dealerValue = 10;
		if (dealerValue == 1) dealerValue = 11;

		//System.out.println("Player Hand: " + playerHand);
		//System.out.println("Dealer Card: " + dealerUpCard);
		
		boolean highCount = false, veryHighCount = false, superHighCount = false;
		
		if (count >= 5) {
			highCount = true;
		}
		if (count >= 10) {
			veryHighCount = true;
		}
		if (count >= 15) {
			superHighCount = true;
		}
		
		/*
		 * Deciding for regular hands
		 */
		if (!soft && !splittable) {
			////System.out.println("Reg hand");
			move = Move.HIT;
			if (playerTotal <= 8) {
				move = Move.HIT;
				// Condition for high crad count
				if (highCount && playerTotal == 8 && (dealerValue == 5 || dealerValue == 6)) {
					
					move = Move.DOUBLE_DOWN;
				}
			} 
			else if (playerTotal == 9) {
				if (dealerValue <= 6 && dealerValue >= 3) {
					move = Move.DOUBLE_DOWN;
				}
				// Condition for high count
				if (highCount && dealerValue == 5) {
					move = Move.DOUBLE_DOWN;
				}
			}
			else if (playerTotal == 10) {
				if (dealerValue <= 9) {
					move = Move.DOUBLE_DOWN;
				}
			}
			else if (playerTotal == 11) {
				if (dealerValue <= 10) {
					move = Move.DOUBLE_DOWN;
				}
			}
			else if (playerTotal == 12) {
				if (dealerValue <= 6 && dealerValue >= 4) {
					move = Move.STAND;
				}
				// Condition for high count
				if (highCount && dealerValue >= 3) {
					move = Move.STAND;//.out.println("Special Decision Made");
				}
			}
			else if (playerTotal <= 16) {
				if (dealerValue <= 6) {
					move = Move.STAND;
				}
				
				if (playerTotal >= 15 && dealerValue == 10) {
					move = Move.STAND;
				}
				
			} else {
				move = Move.STAND;
			}
			
			// Condition for very high count
			if (veryHighCount) {
				
				if (playerTotal >= 14 && playerTotal <= 16) {
					move = Move.STAND;
				}
				
				if (dealerValue == 4 && (playerTotal == 8 || playerTotal == 9)) {
					move = Move.DOUBLE_DOWN;
				}
				if (dealerValue == 10 && playerTotal == 10) {
					move = Move.DOUBLE_DOWN;
				}
				if (dealerValue == 11 && playerTotal == 11) {
					move = Move.DOUBLE_DOWN;
				}
			}
			
			// Condition for super high count
			if (superHighCount && playerTotal <= 11 && dealerValue <= 6) {
				move = Move.DOUBLE_DOWN;
			}
			
		}
		/*
		 * Deciding for splittable hands
		 */
		else if (splittable) {
			////System.out.println("split hand");
			int value = playerHand.get(0).getValue().toInt();
			if (value == 1) {
				move = Move.SPLIT;
			}
			else if (value <= 3) {
				if (dealerValue >= 4 && dealerValue <= 7) {
					move = Move.SPLIT;
				} else {
					move = Move.HIT;
				}
			}
			else if (value == 4) {
				move = Move.HIT;
				// Condition for very high count
				if (veryHighCount && dealerValue >= 3 && dealerValue <= 6) {
					move = Move.SPLIT;
					//
				}
			}
			else if (value == 5) {
				if (dealerValue <= 9) {
					move = Move.DOUBLE_DOWN;
				} else {
					move = Move.HIT;
				}
				// Condition for very high count
				if (veryHighCount && dealerValue >= 4 && dealerValue <= 6) {
					move = Move.SPLIT;//
				}
			}
			else if (value == 6) {
				if (dealerValue >= 3 && dealerValue <= 6) {
					move = Move.SPLIT;
				} else {
					move = Move.HIT;
				}
			}
			else if (value == 7) {
				if (dealerValue <= 7) {
					move = Move.SPLIT;
				} else {
					move = Move.HIT;
				}
			}
			else if (value == 8) {
				move = Move.SPLIT;
			}
			else if (value == 9) {
				if (dealerValue <= 9 && dealerValue != 7) {
					move = Move.SPLIT;
				} else {
					move = Move.STAND;
				}
			}
			else {
				move = Move.STAND;
				// Condition for very high count
				if (veryHighCount && dealerValue >= 5 && dealerValue <= 6) {
					move = Move.SPLIT;
				}
				// Condition for super high count
				if (superHighCount && dealerValue <= 9) {
					move = Move.SPLIT;
				}
			}
			if (superHighCount && value >= 4 && value <= 6 && dealerValue < 7) {
				move = Move.SPLIT;
			}
		}
		/*
		 * Deciding for ace hands
		 */
		else {
			////System.out.println("ace hand");
			if (playerTotal <= 14) {
				if (dealerValue == 5 || dealerValue == 6) {
					move = Move.DOUBLE_DOWN;
				} else {
					move = Move.HIT; 
				}
			} 
			else if (playerTotal <= 16) {
				if (dealerValue <= 6 && dealerValue >= 4) {
					move = Move.DOUBLE_DOWN;
				} else {
					move = Move.HIT;
				}
			}
			else if (playerTotal == 17) {
				if (dealerValue <= 6 && dealerValue >= 3) {
					move = Move.DOUBLE_DOWN;
				} else {
					move = Move.HIT;
				}
			}
			else if (playerTotal == 18) {
				if (dealerValue <= 6 && dealerValue >= 3) {
					move = Move.DOUBLE_DOWN;
				} else if (dealerValue >= 9) {
					move = Move.HIT;
				} else {
					move = Move.STAND;
				}
			}
			else {
				move = Move.STAND;
			}
			// Condition for high count
			if (highCount && playerTotal >= 13 && playerTotal <= 16 && dealerValue <= 6) {
				move = Move.DOUBLE_DOWN;
			}
			
			// Condition for very high count
			if (veryHighCount && playerTotal <= 19 && dealerValue <= 6) {
				move = Move.DOUBLE_DOWN;
			}
			// Condition for super high count
			if (superHighCount && playerTotal == 19 && dealerValue <= 6) {
				move = Move.DOUBLE_DOWN;
			}
		}
		
		if (move == Move.DOUBLE_DOWN && playerHand.getSize() != 2) {
			if (playerTotal == 18 || playerTotal == 19) {
				move = Move.STAND;
			} else {
				move = Move.HIT;
			}
		}
		
		//System.out.println(move);
		//System.out.println();
		
		return move;
	}
	
}
