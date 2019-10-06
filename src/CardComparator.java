import java.util.Comparator;
import java.util.List;

public class CardComparator implements Comparator<Card>{
    int trump_suit;
    int rank;
    
    public CardComparator(int suit, int r) {
        trump_suit = suit;
        rank = r;
    }
    
    public int compare(Card c1, Card c2) {
        if(c1.val == 15 && c2.val == 15) { //both cards are jokers
            return c1.suit - c2.suit;
        }
        else if(c1.val == 15 || c2.val == 15) { // joker trumps any other card, so if only 1 card is joker, that card is higher
            return c1.val == 15 ? 1 : -1; //if card 1 is joker, it is higher. otherwise, card 2 is higher
        }
        else if(c1.val == rank && c2.val == rank) { //both cards are trumps by value
            if(c1.suit == trump_suit && c2.suit == trump_suit) { //identical cards
                return 0;
            }
            else if(c1.suit == trump_suit || c2.suit == trump_suit) { //one card is both trump in value and suit
                return c1.suit == trump_suit ? 1 : -1; //if card 1 is trump suit and trump value, then it is higher
            }
            else {
                return c1.suit - c2.suit;
            }
        }
        else if(c1.val == rank || c2.val == rank) { //one card is matched by rank
            return c1.val == rank ? 1 : -1;
        }
        else if(c1.suit == trump_suit && c2.suit == trump_suit) { //Both cards are trumps, compare value
            if(c1.val == rank && c2.val == rank) { //cards are identical
                return 0;
            }
            else if(c1.val == rank || c2.val == rank) { //card 1 is higher
                return c1.val == rank ? 1 : -1;
            }
            else { //card 2 is higher
                return c1.val - c2.val;
            }
        }
        else if(c1.suit == trump_suit || c2.suit == trump_suit) { //only one card is trump suit
            return c1.suit == trump_suit ? 1 : -1; //if card 1 is trump suit, then card 2 isn't so card 1 is higher (and vice versa)
        }
        else if(c1.suit == c2.suit){ //neither cards are trump suit or jokers, but have same suit
            return c1.val - c2.val; //sort by value
        }
        else { //not same suit, then we want to sort by suit
            return c1.suit - c2.suit;
        }                           
    }
    
}
