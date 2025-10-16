import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class CardPile 
{
    protected final List<Card> cards = new ArrayList<>();
    protected final String name;
    protected final StackPane view; //gui view

    //constructor
    public CardPile(String name) 
    {
        this.name = name;
        this.view = new StackPane();
        initializeView();
    }

    //add card to pile
    public void addCard(Card card) 
    {
        if (card != null) {
            cards.add(card);
            updateView();
        }
    }

    //remove top card from pile
    public Card removeTopCard() 
    {
        if (cards.isEmpty()) return null;
        Card removed = cards.remove(cards.size() - 1);
        updateView();
        return removed;
    }

    public boolean isEmpty() { return cards.isEmpty();}
    public int size() { return cards.size();}
    public Card topCard() { return cards.isEmpty() ? null : cards.get(cards.size() - 1);}
    public List<Card> getCards() { return cards; }
    public String getName() { return name; }
    public StackPane getView() { return view; }
    
    //initialize pile view (empty)
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

    //update pile view with stacked cards
    protected void updateView() 
    {
        view.getChildren().clear();
        if (cards.isEmpty()) {
            initializeView();
        } else {
            double offset = 20; //vertical offset between stacked cards
            for (int i = 0; i < cards.size(); i++) {
                Card c = cards.get(i);
                c.setFaceUp(true);
                StackPane cardView = c.getView();
                cardView.setTranslateY(i * offset); //stack with offset
                view.getChildren().add(cardView);
            }
        }
    }

    //abstract method each pile must implement
    public abstract boolean canAccept(Card c);
}
