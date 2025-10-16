import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;

public class PlayingField 
{
    private final Stock stock = new Stock();
    private final Reserve reserve = new Reserve();
    private final Waste waste = new Waste();
    private final List<Foundation> foundations = new ArrayList<>();
    private final List<Tableau> tableaus = new ArrayList<>();

    //field states
    private int moves = 0;
    private boolean gameStarted = false;
    private int score = 0; //score field

    //GUI drag-and-drop support
    private CardPile selectedPile = null;
    private Card selectedCard = null; //track dragged card

    public PlayingField() //initialize piles
    {
        for (int i = 0; i < 4; i++) foundations.add(new Foundation("Foundation " + (i + 1)));
        for (int i = 0; i < 4; i++) tableaus.add(new Tableau("Tableau " + (i + 1)));
    }

    //getters
    public Stock getStock() { return stock; } 
    public Reserve getReserve() { return reserve; }
    public Waste getWaste() { return waste; }
    public List<Foundation> getFoundations() { return foundations; }
    public List<Tableau> getTableaus() { return tableaus; }
    public int getMoves() { return moves; }
    public boolean isGameStarted() { return gameStarted; }
    public int getScore() { return score; } 

    //drag-and-drop support
    public CardPile getSelectedPile() { return selectedPile; }
    public void setSelectedPile(CardPile pile) { this.selectedPile = pile; }
    public Card getSelectedCard() { return selectedCard; }
    public void setSelectedCard(Card card) { this.selectedCard = card; }

    public CardPile getPileForView(Node view) //get pile by its view
    {
        for (Tableau t : tableaus) if (t.getView() == view) return t;
        for (Foundation f : foundations) if (f.getView() == view) return f;
        if (reserve.getView() == view) return reserve;
        if (stock.getView() == view) return stock;
        if (waste.getView() == view) return waste;
        return null;
    }

    public void startGame() //setup new game
    {
        //reset all piles
        stock.getCards().clear();
        reserve.getCards().clear();
        waste.getCards().clear();
        for (Foundation f : foundations) f.getCards().clear();
        for (Tableau t : tableaus) t.getCards().clear();

        //initialize deck
        for (Suit s : Suit.values()) {
            for (int r = 1; r <= 13; r++) stock.addCard(new Card(s, r));
        }
        stock.shuffle();

        //deal 13 cards to reserve face-up
        for (int i = 0; i < 13; i++) {
            Card c = stock.draw();
            if (c != null) { c.setFaceUp(true); reserve.addCard(c); }
        }

        //base card to first foundation
        Card base = stock.draw();
        if (base != null) {
            base.setFaceUp(true);
            foundations.get(0).addCard(base);
            foundations.get(0).setBaseRank(base.getRank());
        }

        //deal 1 card to each tableau
        for (Tableau t : tableaus) {
            Card c = stock.draw();
            if (c != null) { c.setFaceUp(true); t.addCard(c); }
        }

        moves = 0;
        score = 0;
        gameStarted = true;
    }

    //move card between piles (tableau/reserve/foundation)
    public boolean moveCard(CardPile from, CardPile to) 
    {
        if (from == null || to == null || from.isEmpty()) return false;
        Card c = from.topCard();
        if (c == null) return false;

        if (!to.canAccept(c)) return false;

        from.removeTopCard();
        to.addCard(c);
        moves++;

        checkWin();
        return true;
    }

    //rule-of-3s: draw from stock to waste
    public void drawFromStockRuleOf3()
    {
        int drawCount = Math.min(3, stock.size());
        List<Card> drawn = new ArrayList<>();
        for (int i = 0; i < drawCount; i++)
        {
            Card c = stock.draw();
            if (c != null)
            {
                c.setFaceUp(true);
                drawn.add(c);
            }
        }

        waste.addCardsRuleOf3(drawn);
    }

    private void checkWin() 
    {
        boolean allFoundationsFull = foundations.stream().allMatch(f -> f.size() == 13);
        if (allFoundationsFull) 
        {
            score++;
            gameStarted = false;
        }
    }

    //returns pile based on string id
    public CardPile getPileById(String id)
    {
        if (id.equals("reserve")) return reserve;
        if (id.equals("stock")) return stock;
        if (id.equals("waste")) return waste;
        if (id.startsWith("tableau")) return tableaus.get(Integer.parseInt(id.substring(7)));
        return null;
    }
}
