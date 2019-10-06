import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    List<Card> deck;
    
    /*
     * Initializes the deck, adds all cards to deck. This card game is played with two standard decks
     */
    public Deck() {
        deck = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            for(int suit = 0; suit < 4; suit++) {
                for(int val = 2; val <= 15; val++) {
                    deck.add(new Card(suit, val));
                }
            }
        }
        shuffle();
    }
    
    /*
     * Randomly shuffles the deck
     */
    public void shuffle() {
        Random rand = new Random();
        for(int i = 0; i < deck.size(); i++) {
            int j = i + rand.nextInt(deck.size() - i);
            Collections.swap(deck, i, j);
        }
    }
}
