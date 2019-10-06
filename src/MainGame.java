import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MainGame {
    
    List<Player> players;
    int[] points;
    int winner;
    int trump_suit;
    int cards_left;
    Rule rules;
    
    public void initialize() {
        players = new ArrayList<>();
        points = new int[2];
        for(int player_id = 1; player_id <= 4; player_id++) {
            players.add(new Player(player_id));
        }
        winner = 0;
        cards_left = 100;
        process();
    }
    
    public void process() {
        StartGame sg = new StartGame(players, winner);
        players = sg.players;
        trump_suit = sg.trump_suit;
        int round_winner = winner;
        int prev_winner = -1;
        while(cards_left > 0) {
            round_winner = round(round_winner);
            System.out.println("Round over");
        }
        int[] prev = new int[] {points[0], points[1]};
        if(prev_winner != -1 && prev_winner % 4 == round_winner % 4) {
            round_winner = (round_winner + 2) % 4; //if same player won as the round before, teammate should start next round
        }
        else { //prev winner is not initialized or new team won
            prev_winner = round_winner;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to continue (Y/N)?");
        boolean continue_play = false;
        while(!continue_play) {
            String response = scanner.nextLine();
            if(response.toUpperCase().equals("Y")) {
                continue_play = true;
                winner = round_winner;
                process();
            }
            else if(response.toUpperCase().equals("N")){
                System.out.println("Thanks for playing!");
                continue_play = true;
            }
            else {
                System.out.println("Not a valid choice, please select Y or N");
            }
        }
    }
    
    /**
     * Processes a round and returns the winner of the round
     * @param the previous winner and starter of this round
     * @return the winner of a given round
     */
    
    public int round(int winner) {
        System.out.println("Player "+winner+"'s turn. Please select cards to play");
        System.out.println("Your hand is "+players.get(winner).showHand());
        Scanner scanner = new Scanner(System.in);
        List<Card> [] moves = new ArrayList[4];
        int round_winner = -1;
        rules = new Rule(players, trump_suit, players.get(winner).rank); //define new rule set per round
        boolean valid_start = false;
        try { //Get starting move
            while(!valid_start) {
                String move_s = scanner.nextLine();
                if(move_s == null || move_s.equals("")) continue;
                List<Card> move = new ArrayList<>();
                try{
                    move = moveToList(move_s);
                }
                catch(IllegalStateException | NoSuchElementException | NumberFormatException nfe) {
                    System.out.println("Not a valid move, please try again.");
                }
                if(move != null && rules.isValidStartMove(move, winner)) { //check if it is a valid starting move for player
                    valid_start = true; //Valid first move, then accept
                    moves[winner] = move; //Add move to list of moves
                    cards_left -= move.size();
                    for(Card played : move) {
                        players.get(winner).removeCard(played);
                    }
                }
                else {
                    System.out.println("Not a valid move, please try again.");
                }
            }
            
        } catch(IllegalStateException | NoSuchElementException e) {
            System.out.println("No valid move was selected");
        }
        System.out.println("Player "+winner+"'s move: "+moves[winner].toString());
        for(int cur = winner + 1; cur < winner + 4; cur++) {
            int cur_player = cur % 4;
            System.out.println("Player "+cur_player+"'s turn: ");
            System.out.println("Your hand is "+players.get(cur_player).showHand());
            String player_move = scanner.nextLine();
            List<Card> move = new ArrayList<>();
            try{ 
                move = moveToList(player_move);
            }
            catch(IllegalStateException | NoSuchElementException| NumberFormatException nfe) {
                System.out.println("Not a valid move, please try again.");
            }
            while(move == null || !rules.isValidMove(move, cur_player)) {
                System.out.println("Not a valid move, please try again.");
                player_move = scanner.nextLine();
                move = moveToList(player_move);
            }
            cards_left -= move.size();
            for(Card played : move) {
                players.get(cur_player).removeCard(played);
            }
            moves[cur_player] = move;
            System.out.print("Current cards in play are ");
            for(int i = 0; i < 4; i++) {
                if(moves[i] != null) {
                    System.out.print(moves[i].toString()+" ");
                }
            }
            System.out.println();
        }
        round_winner = rules.findWinner(moves);
        System.out.println("The winner for this round is Team " + (round_winner%2 + 1));
        int points_won = rules.getScore(moves);
        points[round_winner % 2] += points_won;
        System.out.println("The current points are ");
        System.out.println("Team 1(Player 0 + Player 2) = "+points[0]+", Team 2(Player 1 + Player 3) = "+points[1]);
        return round_winner;
    }
    
    public void processWinners(int winner, int[] diff) {
        int winner_diff = diff[(winner + 1) % 2]; //get the points of opponents
        int rank_update = 0; //how many ranks to go up
        int player_to_update = winner; //which players to update at the end
        if(winner_diff == 0) {
            //opponents had 0 points, winner's ranks go up by 3
            rank_update = 3;
        }
        else if(winner_diff <= 35) {
            rank_update = 2;
        }
        else if(winner_diff <= 75) {
            rank_update = 1;
        }
        else if(winner_diff >= 120) {
            if(winner_diff <= 155) {
                rank_update = 1;
            }
            else if(winner_diff <= 195) {
                rank_update = 2;
            }
            else {
                rank_update = 3;
            }
            player_to_update = (winner + 1) % 4;
        }
        for(int i = 0; i < 4; i+=2) {
            int player = (player_to_update + i) % 4;
            players.get(player).updateRank(rank_update);
        }
    }
    
    public List<Card> moveToList(String move){
        String[] moves = move.split(" ");
        List<Card> moveList = new ArrayList<>();
        for(String m : moves) {
            if(m.equals("")) continue;
            Card c = parseCardString(m);
            if(c.val == -1 || c.suit == -1) {
                return null;
            }
            moveList.add(c);
        }
        return moveList;
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
    

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        MainGame game = new MainGame();
        game.initialize();
    }

}
