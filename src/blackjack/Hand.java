/*****************************************************
 * Albert Wilcox
 * IB Computer Science
 * 
 * Hand class
 ****************************************************/
package blackjack;

import java.util.ArrayList;

import blackjack.Engine.HandTypes;
import wilcox.cards.*;

public class Hand {
	private ArrayList<Card> cardList;
	private int softness;
	private int total;
	
	private HandTypes type;
	private boolean updateGUI;
	
	public Hand(HandTypes h) {
		type = h;
		
		cardList = new ArrayList<Card>();
		softness = 0;
		total = 0;
		
		updateGUI = true;
	}
	
	public Hand(HandTypes h, boolean linkGUI) {
		type = h;
		
		cardList = new ArrayList<Card>();
		softness = 0;
		total = 0;
		
		updateGUI = linkGUI;
	}
	
	public boolean getSoftness() {
		if (softness > 0) return true;
		else return false;
	}

	public int getSize() {
		return cardList.size();
	}
	
	public boolean getSplittable() {
		boolean splittable = false;
		
		if (type == HandTypes.SPLIT_ONE || type == HandTypes.SPLIT_TWO) return false;
		
		if (getSize() != 2) return false; 
		
		int value1 = cardList.get(0).getValue().toInt();
		if (value1 >= 10) value1 = 10;
		int value2 = cardList.get(1).getValue().toInt();
		if (value2 >= 10) value2 = 10;
		if (value1 == value2) splittable = true;
		
		return splittable;
	}
	
	public int getTotal() {
		return total;
	}
	
	public void addCard(Card card) {
		cardList.add(card);
		int val = card.getValue().toInt();
		if (val >= 10) val = 10;
		if (val == 1) {
			softness++;
			val = 11;
		}
		
		total += val;
		if (total > 21 && getSoftness()) {
			softness--;
			total -= 10;			
		}
		
		if (updateGUI) {
			switch (type) {
			case DEALER:
				Engine.getGUI().addDealerCard(card);
				break;
			case PLAYER:
				Engine.getGUI().addPlayerCard(card);
				break;
			case SPLIT_ONE:
				Engine.getGUI().addPlayerCard(1, card);
				break;
			case SPLIT_TWO:
				Engine.getGUI().addPlayerCard(2, card);
				break;
			}
		}
	}
	
	public Card get(int n) {
		return cardList.get(n);
	}
	
	public void reset() {
		total = 0;
		softness = 0;
		cardList.clear();
	}
	
	@Override
	public String toString() {
		return cardList.toString();
	}
}
