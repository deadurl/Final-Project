public enum Suit
{
    //basic suit info
    SPADES("♠", false),
    HEARTS("♥", true),
    DIAMONDS("♦", true),
    CLUBS("♣", false);


    private final String symbol; //suit symbol
    private final boolean red; //suit color

    //constructor
    Suit(String symbol, boolean red) 
    { 
        this.symbol = symbol; 
        this.red = red; 
    }

    public String symbol() { return symbol; } //check suit symbol
    public boolean isRed() { return red; } //check if suit is red
}