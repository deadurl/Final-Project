import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;

public class PlayingField 
{
    private final Stock stock = new Stock();
    private final Reserve reserve = new Reserve();
    private final List<Foundation> foundations = new ArrayList<>();
    private final List<Tableau> tableaus = new ArrayList<>();
    private final CardPile discardPile = new CardPile("Discard") { };

    //field states
    private int moves = 0;
    private boolean gameStarted = false;
    private int score = 0;

    //GUI drag-and-drop event support
    private CardPile selectedPile = null;
    private Card selectedCard = null; // track dragged card

    public PlayingField() //initialize piles
    {
        for (int i = 0; i < 4; i++) foundations.add(new Foundation("Foundation " + (i + 1)));
        for (int i = 0; i < 4; i++) tableaus.add(new Tableau("Tableau " + (i + 1)));
        
        // Add drag event handling for each pile
        setupDragEvents();
    }

    //getters
    public Stock getStock() { return stock; } //stock
    public Reserve getReserve() { return reserve; } //reserve
    public List<Foundation> getFoundations() { return foundations; } //foundation
    public List<Tableau> getTableaus() { return tableaus; } //tableau
    public CardPile getDiscardPile() { return discardPile; } //discards
    public int getMoves() { return moves; } //moves
    public boolean isGameStarted() { return gameStarted; } //game state
    public int getScore() { return score; } //score count

    //drag-and-drop support
    public CardPile getSelectedPile() { return selectedPile; } //drag and drop
    public void setSelectedPile(CardPile pile) { this.selectedPile = pile; } //drag and drop
    public Card getSelectedCard() { return selectedCard; } //drag and drop
    public void setSelectedCard(Card card) { this.selectedCard = card; } //drag and drop

    public List<Tableau> getTableau() { return tableaus;} //tableau list
    public List<Foundation> getFoundationsList() { return foundations; } //foundation list

    //pile view corresponding to type
    public CardPile getPileForView(Node view) 
    {
        for (Tableau t : tableaus) {
            if (t.getView() == view) return t;
        }
        for (Foundation f : foundations) {
            if (f.getView() == view) return f;
        }
        if (reserve.getView() == view) return reserve;
        if (stock.getView() == view) return stock;
        if (discardPile.getView() == view) return discardPile;
        return null;
    }

    public void startGame() 
    {
        //reset values
        stock.getCards().clear();
        reserve.getCards().clear();
        discardPile.getCards().clear();
        for (Foundation f : foundations) { f.getCards().clear(); }
        for (Tableau t : tableaus) { t.getCards().clear(); }

        //initialize deck
        for (Suit s : Suit.values()) {
            for (int r = 1; r <= 13; r++) stock.addCard(new Card(s, r));
        }
        stock.shuffle();

        //deal cards to reserve
        for (int i = 0; i < 13; i++) {
            Card c = stock.draw();
            if (c != null) { c.setFaceUp(false); reserve.addCard(c); }
        }

        //base cards
        Card base = stock.draw();
        if (base != null) {
            base.setFaceUp(true);
            foundations.get(0).addCard(base);
            foundations.get(0).setBaseRank(base.getRank());
        }

        //deal cards to tableau
        for (Tableau t : tableaus) {
            Card c = stock.draw();
            if (c != null) { c.setFaceUp(true); t.addCard(c); }
        }

        //cards left in stock are face down
        List<Card> remaining = new ArrayList<>();
        Card c;
        while ((c = stock.draw()) != null) remaining.add(c);
        stock.addAll(remaining);
        stock.getCards().forEach(card -> card.setFaceUp(false));

        moves = 0;
        gameStarted = true;
    }

    public boolean moveCard(CardPile from, CardPile to) //move card between piles
    {
        if (from == null || to == null || from.isEmpty()) return false; //invalid move
        Card c = from.topCard(); //card to move
        if (c == null) return false; //no card available

        if (from instanceof Reserve) //reserve cards face up always 
        {
            c = from.removeTopCard(); 
            if (c == null) return false; //no card available
            c.setFaceUp(true); //set face up
        } else {
            c = from.removeTopCard(); //remove top card
            if (c == null) return false; //no card available
        }

        boolean allowed = false; //check if move allowed
        if (to instanceof Foundation) {
            allowed = ((Foundation) to).canAccept(c); //rules check
        } else if (to instanceof Tableau) {
            allowed = ((Tableau) to).canAccept(c); //rules check
        } else if (to == discardPile) {
            allowed = true; //can always discard
        } else if (to == stock) {
            allowed = true; //can always return to stock
        }

        if (!allowed) //move not allowed, return card back to original pile
        {
            from.addCard(c);
            return false;
        }

        to.addCard(c); //add card to destination pile
        moves++; //increment move count
        checkWin(); //check for win condition
        return true; //move successful
    }

    public Card drawFromStock() //draw card from stock to discard pile
    {
        Card c = stock.draw(); //draw top card
        if (c != null) c.setFaceUp(true); //set face up
        return c; //return drawn card
    }

    public void recycleDiscardToStock() //move all cards from discard pile back to stock
    {
        List<Card> toRecycle = new ArrayList<>(discardPile.getCards()); //copy cards
        discardPile.getCards().clear(); //clear discard pile
        for (Card rc : toRecycle) { rc.setFaceUp(false); stock.addCard(rc); } //add to stock face down
    }

    private void checkWin() //check win condition
    {
        boolean allFoundationsFull = foundations.stream().allMatch(f -> f.size() == 13); //all foundations full
        if (allFoundationsFull) //win condition met
        {
            score++;
            gameStarted = false; //end game
        }
    }

    private void setupDragEvents() 
    {
        //Set up drag detection for all piles
        for (CardPile pile : getAllPiles()) 
        {
            pile.getView().setOnDragDetected(event -> 
            {
                setSelectedPile(pile); //remember selected pile
                setSelectedCard(pile.topCard());//remember selected card
                event.consume();//consume event
            });
        }
    }
    
    //helper to get all piles
    private List<CardPile> getAllPiles() 
    {
        List<CardPile> all = new ArrayList<>();
        all.add(stock);
        all.add(reserve);
        all.add(discardPile);
        all.addAll(foundations);
        all.addAll(tableaus);
        return all;
    }
}
