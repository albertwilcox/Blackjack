/*****************************************************
 * Albert Wilcox
 * IB Computer Science
 * 
 * Move Enumeration
 ****************************************************/
package blackjack;

public enum Move {
	HIT, STAND, DOUBLE_DOWN, SPLIT, ERROR;
	
	@Override
	public String toString() {
		String s = super.toString();
		s = s.replace('_', ' ');
		
		String s2 = s.substring(0, 1);
		String s3 = s.substring(1);
		s3 = s3.toLowerCase();
		
		s = s2 + s3;
		
		return s;
	}
}
