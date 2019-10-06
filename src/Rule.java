import java.util.ArrayList;
import java.util.List;

public class Rule {
    List<Player> players;
    int trump_suit;
    //int start_suit;
    int start_len;
    List<Card> start;
    int rank;
    
    public Rule(List<Player> p, int suit, int r) {
        start = new ArrayList<>();
        players = p;
        trump_suit = suit;
        rank = r;
    }
    
    public boolean isValidMove(List<Card> move, int player) {
        if(move.size() != start.size()) {
            return false;
        }
        if(start_len == 1) {
            Card c = move.get(0);
            if(!players.get(player).hasCard(c)) { //player does not have card
                return false;
            }
            int start_suit = start.get(0).suit;
            if(c.suit == start_suit && c.val != rank && c.val != 15) { //matches suit of non-special card
                return true; //valid move
            }
            else if(start_suit == trump_suit) { //trump was played, but card does not match trump
                if(c.val == rank || c.val == 15) { //card must be a special trump
                    return true;
                }
            }
            else if(start.get(0).val == 15 || start.get(0).val == rank) {
                //trump was played
                if(c.val == rank || c.val == 15) {
                    return true;
                }
                else if(c.suit == trump_suit) {
                    return true;
                }
                //do not have any trumps
                else if(players.get(player).num_suits[trump_suit] == 0 
                        && players.get(player).num_suits[4] == 0) {
                    return true;
                }
            }
            else if(players.get(player).num_suits[start_suit] == 0) { //different suit was played (either trump or diff suit), must be out of cards in that suit
                return true;
            }
            else {
                return false;
            }
        }
        else if(start_len == 2) {
            Card c1 = move.get(0);
            Card c2 = move.get(1);
            if(!players.get(player).hasCard(c1) || !players.get(player).hasCard(c2)) {
                //player does not have a played card
                return false;
            }
            if(c1.val == c2.val && c1.suit == c2.suit) { //is a pair
                //is a valid pair, need to check if pair matches the suit
                if(c1.suit == start.get(0).suit) {
                    return players.get(player).hasPair(c1, c2); //same suit, valid pair
                }
                else if(start.get(0).val == 15) {
                    //pair joker was played, trump pair needs to be played
                    if(c1.suit == trump_suit && c2.suit == trump_suit) {
                        return true;
                    }
                    else if(c1.val == rank && c2.val == rank) {
                        return true;   
                    }
                    else if(c1.val == 15 && c2.val == 15) {
                        return true;
                    }
                }
                else if(players.get(player).num_suits[start.get(0).suit] == 0){ //the player is out of the played suit, a valid pair becomes valid
                    return players.get(player).hasPair(c1, c2);
                }
            }
            else if(!players.get(player).hasPairInSuit(start.get(0).suit) 
                    && start.get(0).val != rank && start.get(0).val != 15) { 
                //player has no pair in the suit (separate case for pairs of trump by rank or joker)
                int check_suit = start.get(0).suit;
                //check if we have at least two cards in the suit
                if(players.get(player).num_suits[check_suit] >= 2) {
                    //have at least two cards in the suit, must put two cards of that suit
                    if(c1.suit != c2.suit || c1.suit != start.get(0).suit ) {
                        return false;
                    }
                }
                int num_cur_suit = c1.suit == start.get(0).suit ? 1 : 0;
                num_cur_suit = c2.suit == start.get(0).suit ? num_cur_suit + 1 : num_cur_suit;
                if(num_cur_suit <= players.get(player).num_suits[start.get(0).suit]) {
                    return true; //number of suits played matches the number of suits the player has
                }
            }
            else if(start.get(0).val == rank || start.get(0).val ==  15){
                int check_suit = 4;
                    //any trumps will work as well
                int num_trumps = players.get(player).num_suits[check_suit] +  
                        players.get(player).num_suits[trump_suit];
                if(num_trumps >= 2) {
                    //c1 and c2 must both be trumps
                    if(c1.isTrump(rank, trump_suit) && c2.isTrump(rank, trump_suit)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else if(num_trumps == 1) {
                    //expect 1 trump
                    if(c1.isTrump(rank, trump_suit) || c2.isTrump(rank, trump_suit)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    //no trumps left, any cards work
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isValidStartMove(List<Card> move, int player) {
        int cur_rank = players.get(player).rank;
        if(move.size() == 1) { //only one card is played
            if(players.get(player).hasCard(move.get(0))) { //check if the player has this card
                start_len = 1;
                start = move;
                return true;
            }
            else {
                return false;
            }
        }
        else if(move.size() == 2) { //a pair was played, so cards must be identical
            Card c1 = move.get(0);
            Card c2 = move.get(1);
            if(c1.suit == c2.suit && c1.val == c2.val && players.get(player).hasCard(c1) && players.get(player).hasCard(c2)) { //if the two cards are identical, then valid move
                start_len = 2;
                start = move;
                return true;
            }
            else if(c1.val == 15 && c2.val == 15) { //pair joker was played
                if(c1.suit % 2 == c2.suit) { //check if both small or both big
                    //same as big or small joker
                    return true;
                }
                else {
                    return false;
                }
            }
            else { //otherwise, not a valid move
                return false;
            }
        }
//        else if(move.size() == 4) {
//            
//        }
        return false;
    }
    
    public int findWinner(List<Card> [] moves) {
        int[] values = new int[4];
        int max = 0;
        int max_id = 0;
        if(start.size() == 1) {
            //find high card, we can accomplish through 3 comparisons
            for(int i = 0; i < 4; i++) {
                List<Card> p = moves[i];
                Card c = p.get(0);
                int temp_val = 0;
                if(c.val == 15) {
                    //joker
                    if(c.suit %2 == 0) {//small joker
                        temp_val += 42;
                    }
                    else {
                        temp_val += 43;
                    }
                }
                if(c.val == rank) {
                    //trump rank
                    temp_val += 28;
                }
                if(c.suit == trump_suit) {
                    temp_val += 14;
                }
                if(c.suit != start.get(0).suit && !(c.suit == trump_suit || c.val == rank || c.val == 15)) { //different suit than what was played but not trump
                    continue;
                }
                temp_val += c.val;
                if(max < temp_val) {
                    max = temp_val;
                    max_id = i;
                }
            }
        }
        else if(start.size() == 2) {
            int start_suit = start.get(0).suit;
            for(int i = 0; i < 4; i++) {
                List<Card> p = moves[i];
                Card c1 = p.get(0);
                Card c2 = p.get(1);
                int temp_val = 0;
                //check if pair, if not, cannot win
                if(c1.val != c2.val || c1.suit != c2.suit) {
                    if(c1.val == c2.val && c1.val == 15) {
                        //pair jokers
                        if(c1.suit % 2 == 0) {
                            //little joker
                            temp_val += 42;
                        }
                        else {
                            //big joker
                            temp_val += 43;
                        }
                    }
                    else {//not a pair of jokers or a pair
                        continue;
                    }
                }
                //it is a pair, check if it is the same suit as starting suit
                else if(c1.suit == start_suit && c2.suit == start_suit) {
                    //same as the starting suit
                    temp_val += 0;
                }
                //check if it is a pair of trump ranks
                else if(c1.val == c2.val && c1.val == rank) {
                    //trump ranks
                    temp_val += 28;
                }
                else {
                    //not the same suit, check if trump suit
                    if(c1.suit == c2.suit && c1.suit == trump_suit) {
                        //pairs are trumps
                        temp_val += c1.val + 14;
                    }
                    else { //not same suit and not trump suit, cannot win
                        continue;
                    }
                }
                temp_val += c1.val;
                if(max < temp_val) {
                    max = temp_val;
                    max_id = i;
                }
            }
        }
        return max_id;
    }
    
    public int getScore(List<Card>[] moves) {
        int total_points = 0;
        for(int i = 0; i < 4; i++) {
            List<Card> move = moves[i];
            for(Card c : move) {
                if(c.val == 5) {
                    total_points += c.val;
                }
                else if(c.val == 10) {
                    total_points += c.val;
                }
                else if(c.val == 13) { //Kings are worth 15 points
                    total_points += 15;
                }
            }
        }
        return total_points;
    }
}
