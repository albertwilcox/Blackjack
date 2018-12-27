/*****************************************************
 * Albert Wilcox
 * IB Computer Science
 * 
 * SplitHand class
 ****************************************************/
package blackjack;

import blackjack.Engine.HandTypes;
import wilcox.cards.*;

public class SplitHand {
	private Hand hand1, hand2;
	
	public SplitHand() {
		hand1 = new Hand(HandTypes.SPLIT_ONE);
		hand2 = new Hand(HandTypes.SPLIT_TWO);
	}
	
	public void addCard(int splitHand, Card card){
		if (splitHand == 1) {
			hand1.addCard(card);
		} else if (splitHand == 2) {
			hand2.addCard(card);
		} else System.out.println("Something has gone horribly wrong");
	}
	
	public Hand getHand(int num) {
		switch (num) {
		case 1:
			return hand1;
		case 2:
			return hand2;
		default:
			return hand1;
		}
	}
	
	public void reset() {
		hand1.reset();
		hand2.reset();
	}
}
