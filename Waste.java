import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Waste extends CardPile 
{
    private final List<Card> wasteCards = new ArrayList<>(); //all drawn cards
    private int displayCount = 0; //how many cards currently shown (1-3)

    public Waste() 
    {
        super("Waste");
        initializeView();
    }

    //add drawn cards from stock following Rule-of-3s
    public void addCardsRuleOf3(List<Card> drawn)
    {
        if (drawn == null || drawn.isEmpty()) return;

        //add drawn cards to waste pile
        wasteCards.addAll(drawn);

        //update displayCount (max 3)
        displayCount = Math.min(3, wasteCards.size());
        updateView();
    }

    @Override
    public boolean canAccept(Card c)
    {
        return false; //cannot manually place cards in waste
    }

    @Override
    public StackPane getView() { return view; }

    @Override
    protected void initializeView()
    {
        view.getChildren().clear();
        Rectangle placeholder = new Rectangle(80, 110);
        placeholder.setArcWidth(15);
        placeholder.setArcHeight(15);
        placeholder.setFill(Color.LIGHTGRAY);
        placeholder.setStroke(Color.BLACK);
        view.getChildren().add(placeholder);
    }

    @Override
    protected void updateView()
    {
        view.getChildren().clear();

        double offset = 15; //horizontal offset for 3 cards
        int start = Math.max(0, wasteCards.size() - displayCount);

        for (int i = start; i < wasteCards.size(); i++) //show last 'displayCount' cards
        {
            Card c = wasteCards.get(i);
            c.setFaceUp(true);
            StackPane cardView = c.getView();

            // stack horizontally
            cardView.setTranslateX((i - start) * offset);

            // top card in front
            if (i == wasteCards.size() - 1) cardView.toFront();

            view.getChildren().add(cardView);
        }
    }

    //draw top card from waste (for moves to tableau/foundation)
    public Card drawTopCard()
    {
        if (wasteCards.isEmpty()) return null;
        Card c = wasteCards.remove(wasteCards.size() - 1);
        updateView();
        return c;
    }

    public boolean isEmpty() { return wasteCards.isEmpty(); }
    public int size() { return wasteCards.size(); }
    public Card topCard() { return wasteCards.isEmpty() ? null : wasteCards.get(wasteCards.size() - 1); }
}
