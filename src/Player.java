import java.util.ArrayList;
import java.util.List;

public class Player {
    List<Card> hand;
    int score; //The current score of the player
    int rank; //The current rank of the player
    int id;
    int[] num_suits;
    String name;
    
    /*
     * Initializes the player class
     */
    public Player(int player_id) {
        hand = new ArrayList<>();
        score = 0;
        rank = 2;
        id = player_id;
        num_suits = new int[5]; //one extra for trump suit, for the cards with same value as rank
        name = "Player "+String.valueOf(id);
    }
    
    /*
     * Adds a card to this player's hand
     */
    public void addCard(Card c) {
        this.hand.add(c);
        if(c.val != rank && c.val != 15) num_suits[c.suit]++;
        else {
            num_suits[num_suits.length -1]++;
        }
    }
    
    /*
     * Removes a card from this player's hand
     */
    public void removeCard(Card c) {
        hand.remove(c);
        if(c.val != rank) num_suits[c.suit]--;
        else {
            num_suits[num_suits.length -1]--;
        }
    }
    
    public boolean hasCard(Card c) {
        return hand.contains(c);
    }
    
    public boolean hasPair(Card c1, Card c2) {
        int num = 0;
        for(Card c : hand) {
            if(c.val == c1.val && c.suit == c1.suit) {
                num++;
            }
        }
        return num == 2;
    }
    
    public boolean hasPairInSuit(int s) {
        int num = 0;
        Card prev = null;
        for(Card c : hand) {
            if(c.suit == s) {
                if(prev == null) {
                    prev = c;
                }
                else if(prev.val == c.val && prev.suit == c.suit && prev.val != rank) {
                    return true;
                }
                else {
                    prev = c;
                }
            }
            if(c.suit > s) { //assumes its sorted, have not found a pair already
                return false;
            }
        }
        return false;
    }
    
    /*
     * Returns the score of the player
     */
    public int getScore() {
        return score;
    }
    
    /*
     * Updates the score of the player
     */
    public void addScore(int s) {
        score += s;
    }
    
    public void updateRank(int num) {
        rank += num;
    }
    
    public void decreaseRank(int num) {
        rank -= num;
    }
    
    public void clearHand() {
        hand = new ArrayList<>();
    }
    
    public String showHand() {
        String player_hand = "";
        for(Card c : hand) {
            player_hand += c.toString()+" ";
        }
        return player_hand;
    }
}
