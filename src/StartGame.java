import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StartGame {
    List<Player> players;
    Deck card_deck;
    int declarer;
    int trump_suit;
    
    /*
     * Initializes the beginning of the game (round 1). Shuffles the deck and adds the players in.
     * Sets the declarer to the first player.
     */
    public StartGame() {
        players = new ArrayList<>();
        for(int player_id = 1; player_id <= 4; player_id++) {
            players.add(new Player(player_id));
        }
        card_deck = new Deck();
        declarer = 1;
        assignCards(1);
    }
    
    public StartGame(List<Player> game_players, int winner) {
        players = game_players;
        card_deck = new Deck();
        for(int player_id = 1; player_id <= 4; player_id++) {
            players.get(player_id - 1).clearHand();
        }
        
        declarer = winner;
        assignCards(declarer);
    }
    
    public void assignCards(int winner) {
        List<Card> extra_deck = new ArrayList<>();
        while(extra_deck.size() < 12) {
            extra_deck.add(card_deck.deck.remove(0));
        }
        for(int player_id = 1; player_id <= 4; player_id++) {
            Player cur_player = players.get(player_id - 1);
            for(int c = 0; c < 25; c++) {
                //Every player is assigned 25 cards
                cur_player.addCard(card_deck.deck.remove(0));
            }
            System.out.println("Player "+String.valueOf(player_id)+"'s hand");
            System.out.println(cur_player.showHand());
        }
        
        processStart(extra_deck, winner);
        
        CardComparator cc = new CardComparator(trump_suit, players.get(declarer).rank);
        for(int i = 0; i < 4; i++) {
            System.out.println("Player "+i+"'s hand");
            Collections.sort(players.get(i).hand, cc);
            System.out.println(players.get(i).showHand());
        }
    }
    
    public void processStart(List<Card> extra_deck, int winner) {
        
        System.out.printf("Player %d, pick from extra deck \n", winner);
        for(Card extra_card : extra_deck) {
            System.out.print(extra_card.toString() + " ");
        }
        System.out.println();
        System.out.println("This is your current hand");
        System.out.println(players.get(winner).showHand());
        System.out.println("Please select cards with the following format: #Suit (i.e. 6D for 6 of diamonds)");
        int replace = 0;
        String[] input = new String[2];
        
        Scanner scanner = new Scanner(System.in);
        boolean processed = false;
        try {
            while(!processed) {
                if(replace == 0) {
                    System.out.println("Please select which cards from the extra deck you want");
                }
                else {
                    System.out.println("Now please select which cards to replace");
                }
                String response = scanner.nextLine();
                input[replace] = response;
                if(replace == 1) {
                    processed = processSwap(winner, input, extra_deck);
                    if(!processed) {
                        System.out.println("Invalid swap, please try again.");
                        replace = -1;
                    }
                }
                replace++;
            }
            
        } catch(IllegalStateException | NoSuchElementException e) {
            System.out.println("No cards were selected");
        }
        
        System.out.println("Successfully swapped, this is your hand now.");
        System.out.println(players.get(winner).showHand());
        
        System.out.println("Please declare a suit (C, D, S, H)");
        scanner = new Scanner(System.in);
        boolean declared = false;
        try {
            while(!declared) {
                String response = scanner.nextLine().toUpperCase();
                declared = true;
                if(response.toUpperCase().equals("C")) {
                    trump_suit = 0;
                }
                else if(response.toUpperCase().equals("D")) {
                    trump_suit = 1;
                }
                else if(response.toUpperCase().equals("S")) {
                    trump_suit = 2;
                }
                else if(response.toUpperCase().equals("H")) {
                    trump_suit = 3;
                }
                else {
                    declared = false;
                }
                if(declared) {
                    System.out.println("Declared suit is "+response+", trump is " + players.get(declarer).rank);
                }
                else {
                    System.out.println("Invalid suit input, please try again.");
                }
            }
        } catch(IllegalStateException | NoSuchElementException e) {
            System.out.println("No cards were selected");
        }
        
    }
    
    public boolean processSwap(int winner, String[] card_string, List<Card> extra_deck) {
        Player cur = players.get(winner);
        String[] add = card_string[0].split(" ");
        String[] remove = card_string[1].split(" ");
        List<Card> temp_add = new ArrayList<>();
        List<Card> temp_remove = new ArrayList<>();
        if(add.length != remove.length || add.length > 12 || remove.length > 12) {
            return false;
        }
        for(String a : add) {
            if(!a.equals("")) {
                Card c = parseCardString(a);
                if(c.val == -1 || c.suit == -1 || !extra_deck.contains(c)) {
                    return false;
                }
                temp_add.add(c);
            }
        }
        
        for(String r : remove) {
            if(!r.equals("")) {
                Card c = parseCardString(r);
                if(c.val == -1 || c.suit == -1 || !players.get(winner).hasCard(c)){
                    return false;
                }
                temp_remove.add(c);
            }
        }
        
        //If there is no invalid card, then proceed with the swaps
        for(Card c : temp_add) {
            cur.addCard(c);
        }
        
        for(Card c : temp_remove) {
            cur.removeCard(c);
        }
        System.out.println(cur.hand.size());
        return true;
    }
    
    public Card parseCardString(String card_string) {
        if(card_string.length() > 3 && !card_string.toLowerCase().contains("joker")) {
            return new Card(-1, -1);
        }
        String card_val = "";
        String card_suit = "";
        int val = -1;
        int suit = -1;
        if(card_string.toLowerCase().contains("joker")) {
            card_val = "Joker";
            card_suit = card_string.substring(card_string.length() - 1).toUpperCase();
        }
        else {
            card_val = card_string.substring(0, card_string.length() - 1).toUpperCase();
            card_suit = card_string.substring(card_string.length() - 1).toUpperCase();
        }
        if(card_val.equals("J")) {
            val = 11;
        }
        else if(card_val.equals("Q")) {
            val = 12;
        }
        else if(card_val.equals("K")) {
            val = 13;
        }
        else if(card_val.equals("A")) {
            val = 14;
        }
        else if(card_val.equals("Joker")) {
            val = 15;
        }
        else {
            try{
                val = Integer.valueOf(card_val);
            } catch (IllegalStateException | NoSuchElementException| NumberFormatException nfe) {
                return new Card(-1, -1);
            }
        }
        
        if(card_suit.equals("C")) {
            suit = 0;
        }
        else if (card_suit.equals("D")) {
            suit = 1;
        }
        else if(card_suit.equals("S")) {
            suit = 2;
        }
        else if(card_suit.equals("H")) {
            suit = 3;
        }
        
        
        return new Card(suit, val);
    }
}
