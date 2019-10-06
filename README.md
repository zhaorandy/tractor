# tractor
A simple console playable version of the popular Chinese card game Tractor (tuo la ji) made with Java

Tractor Design Document:
# About the project:
Tractor, or tuo la ji, is a Chinese card game that is immensely popular. In this project, I have recreated a simplified game using Java to be played in the console. I will discuss some of the design choices and rules and instructions to play.

# How to play the game:
In this game, four players are split into two teams and can earn points through rounds. The game is played with two decks and begins with each player starting with a rank of 2. The goal is to win the most points and rank up, while ensuring that your opponents do not earn points. At the start of the game, each player is dealt 25 cards, and one player is allowed to swap some of their cards with the 12 remaining cards. Then, this player will declare a trump suit, which then becomes a suit of higher value during the game. In this simplified version of the game, each player will take turns playing either a single card or a pair of cards. Each player must match the same format of the first move (i.e. if a single card is played, then each player must follow up with a single, and similarly for a pair). Furthermore, the following cards must match the suit of the card played unless the player does not possess any more cards of that suit. Similarly, if the player possess a pair in the suit, they must play it, but they do not have to play a pair if they do not have any more cards of that suit. However, if they have at least one card of the suit that was originally played, they must play as many as two cards of that suit even if they do not form a pair (i.e. if the original pair was 6H 6H, and I possess only the 7H, I could play 7H 3C. However, if I owned 7H 8H, then I must play those two cards even if they are not a pair).

Once all the cards are played, then the rounds are scored depending on the existence of point cards, which are 5s, 10s, or Ks. Each of those cards are worth as many points as their value, except for Ks which are worth 15 points. To determine the winner, the cards are compared based on a series of criteria.

For single cards, the high card will win, where the cards are ranked from A to 2. However, if a card is the same value as the rank of the person who declared the suit, then those cards also become trumps (i.e. if my rank is 2 and I declared the suit, then all 2s are trumps, thus 2s are more valuable than As). So, if all the cards are of the same suit, then the winner is simply whoever played the highest card. Including trumps, the final ranking for a single card play would look like this:
Single card of non-trump suit but different suit than start (this card can never win) < Single card of starting suit for the round (ordered by high card) < trump card (ordered by ranking) < cards of the current rank of player (i.e. 2 if the player’s rank is 2) < card of the rank and declared trump suit < small joker (denoted by joker with clubs or spades in this game) < big joker (denoted by joker with diamonds or hearts)

The same applies for pairs: the higher pair will win, and any pair of cards that are played that are not a true pair cannot win. Again, each play must be valid, so trump cards can only be played if the player is completely out of the suit of the starting cards(s).

The winner of the round earns however many points were played in that round for the team. At the end of the game, the number of points are tallied, and the player’s ranks are adjusted according to the following table:

Opponent's teams points | Result       
------------------------|-------
0 | Declarer's rank increases by three
5-35 | Declarer's rank increases by two
40-75 | Declarer's rank increases by one
80-115 | Opponents become declarers, ranks stays the same
120-155 | Opponents become declarers and move up one rank
160-195 | Opponents become declarers and move up two ranks
200-235 | Opponents become declarers and move up three ranks

Once the round ends, the game repeats but with updated ranks and a new declarer.

# Instructions for playing in the console version:
The game starts by displaying the assigned cards to each of the players. Then, the declarer is allowed to see the extra deck and pick and choose which cards to swap from their existing hand. This is done by typing the cards to choose delimited by a space. The cards should be typed with the following format: {value}{suit} (i.e. 6C for six of clubs, KH for King of Hearts, or JokerC, for small joker). If an invalid swap is given, which can happen if the cards picked do not exist in the extra deck or in the player’s hand, or if the card is of the incorrect formats, the player must re-enter a valid series of cards to swap.

Once the swap is completed (note: a swap does not need to happen), the declarer will choose the trump suit, either inputting C (for clubs), D (for diamonds), S (for spades), or H (for hearts). If an invalid suit is given, the player must correctly input it again.

Then, the game can start, beginning with the declarer choosing which cards to play. If the starting cards are valid (i.e. a single or a valid pair), then the players will take turns choosing cards that follow the aforementioned rules. The process of selecting cards follows the process for choosing which cards to swap, with the same format. Once the round is over, the console will declare which team won, the current points standings, as well as which player will go next. The rounds repeat, until all the cards have been exhausted, then the console inquires whether the player wants to continue. If not, it thanks them for playing. Otherwise, the process repeats.

# Design choices/features:
Due to limited time (three hours recommended, total of about 3.5 hours spent), I had to simplify some aspects of the game. However, the core mechanics of the game were all implemented, and the game still functions exactly the same as the true card game, but just that some moves cannot be interpreted, which may create more rounds.

To implement the card games, first I had to design the Card and Deck class. The card class is a simple class that contains two variables: the card’s suit and the card’s value. The suits ranged from 0 to 3, where 0 is clubs, 1 is diamonds, 2 is spades, and 3 is hearts. Similarly, the values represent the actual values, with face cards following the integer progression of the previous cards (J = 11, Q = 12, K = 13, A = 14, Joker = 15). The Deck class holds a List of Cards. The constructor for this class simply adds two decks worth of cards and randomly shuffles them. Using a list is preferable to an array because it allows for removal as well as addition of elements without imposing a strict bound. Although we have a finite known number of cards, Lists are slightly more flexible than arrays, without losing too much functionality (since we don’t need to know the exact card at certain indices, we only need a container for the cards).

Then, I implemented a Player class, which would represent a singular player in the game. Thus, the class has a List of Cards which denotes the player’s hand, the player’s current rank and ID (i.e. player 0, player 1, player 2, or player 3). Furthermore, the class has a series of functions for utility, such as adding cards to the hand, or removing cards from the hand (when a card is played), as well as checking if a player has a certain card. In order to implement a checker for whether a move is valid, I had the Player class hold an array that kept count of the number of cards of each suit a player had. When a card was added to or removed from the player’s hand, the count would be adjusted accordingly.

The next step was to implement the game. For this, I broke down the game into two subparts for clarity of viewing and coding: one part was the start of the game where the declarer would swap cards with the extra deck and declare the suit, and then the second part would be the actual rounds that were played.

First, the start of the game. This class has a List of Players, which represented the players in the game. Furthermore, there is a Deck of cards, as well as two variables that hold the current declarer and the trump suit. The constructor takes the list of players from the main game class, as well as the winner, who will be assigned to the declarer. The main purpose of this class is to facilitate the start of the round, so it outputs the player’s current hand as well as the cards in the extra deck and prompts the declarer for which cards to swap. Since the inputs are of a specified format, a helper method parses the value and suits from the string, and if it cannot recognize it, it prompts the user for a valid input. Basic logic and checking the strings can accomplish this perfectly fine, and since the user can input multiple cards separated by spaces, the program will split the string into an array of strings split by the space and check each string. Once the start is processed, the main game can begin.

The main game consists of the players in the game (List of Players), an array that keeps track of the points, the current trump suit and winner, as well as a Rules object (will be discussed later). The program first creates an instance of the StartGame class, and inputs the players list as well the previous round winner. Then, it processes the rounds.

First, it prompts the declarer to set the starting cards. Then, the rules object will check if the played move is valid. This is accomplished in the Rules class, which consists of the current trump suit, the starting move, and the rank. This class checks if a starting move is valid, and if it is, then it stores the starting move so that the next moves can be compared to this move. To check whether a move is valid, I used a series of conditional logic statements to check the multitude of cases. The Rules class was created also for a cleaner, separate individual module to test valid moves rather than clutter the MainGame class.

If a move is valid, then it is added to an array of List of Cards, since a move can consist of either one or two moves. I used an array because there are a fixed number of players, and a List of Cards since the players can play either one or two cards. Once all the moves have been counted, the Rules object then searches for a winner. This is accomplished using logic statements again and some crafty math to assign values depending on the suit and value. I utilized math because it allows me to control the outcome of the series of comparisons without creating a sequence of logic, and with a certain rank and trump suit, there is only one ordering of values.

At the end of the round, the points are compared and the ranks and subsequent declarer are assigned based on the table. The program then checks if the user wishes to continue to play, and if yes, then the process repeats, otherwise, it quits.


# Conclusion / Notes:
Given an advised time limit of 3 hours, I programed a simple version of the game. The program specifies certain formats it expects, but accounts for nonconforming inputs by requesting a valid input. Some features that I would have liked to add would have required more time, such as making the game more multiplayer friendly by shielding the cards, or creating an actual interface rather than the text. However, the overall product is a functioning game with the core mechanics of tuo la ji, and serves as a good foundation for further projects, whether it be implementing other versions of the game such as zhao peng you, which allows for more players and choosing your partner, or sishi fen which uses only a single deck.

