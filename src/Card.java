
public class Card {
    int suit; //0 for club, 1 for diamond, 2 for spade, 3 for heart, %2 == 0 means black, %2==1 means red
    int val; //2 to 14, 11  – jack, 12 – queen, 13 – king, 
    //14 – ace, 15 – joker (same color way applies, but smaller suit number represents small joker vs big joker)
    /*
     * Initializes a card with suit and value
     */
    public Card(int s, int v) {
        suit = s;
        val = v;
        //player = p;
    }
    
    @Override
    public String toString() {
        String string_suit = "";
        String string_val = "";
        if(val <= 10) {
            string_val = String.valueOf(val);
        }
        else if(val == 11) {
            string_val = "J";
        }
        else if(val == 12) {
            string_val = "Q";
        }
        else if(val == 13) {
            string_val = "K";
        }
        else if(val == 14) {
            string_val = "A";
        }
        else {
            string_val = "Joker";
        }
        if(suit == 0) {
            string_suit = "\u2663";
        }
        else if(suit == 1) {
            string_suit = "\u2666";
        }
        else if(suit == 2) {
            string_suit = "\u2660";
        }
        else {
            string_suit = "\u2665";
        }
        return string_val + string_suit;
    }
    
    @Override
    public boolean equals(Object c) {
        Card comp = (Card) c;
        return (comp.val == this.val) && (comp.suit == this.suit);
    }
    
    public boolean isTrump(int rank, int trump_suit) {
        if(this.val == rank || this.suit == trump_suit || this.val == 15) {
            return true;
        }
        return false;
    }
}
