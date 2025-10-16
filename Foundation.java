import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Foundation extends CardPile 
{
    private Integer baseRank = null; //foundations build up by suit starting at base

    //constructor
    public Foundation(String name) 
    {
        super(name);
        initializeView();
    }

    public void setBaseRank(int r) { baseRank = r;}
    public Integer getBaseRank() { return baseRank;}

    @Override
    public boolean canAccept(Card c) //card check (can only add cards in sequence by suit)
    {
        if (c == null) return false;

        if (cards.isEmpty()) 
        {
            return baseRank != null && c.getRank() == ((baseRank % 13) + 1);
        } else {
            Card top = topCard();
            return c.getSuit() == top.getSuit() && c.getRank() == (top.getRank() % 13) + 1;
        }
    }

    @Override
    public StackPane getView() { return view; } //get pile view

    @Override
    protected void updateView() //base pile view (show only top card)
    {
        view.getChildren().clear();
        if (cards.isEmpty()) {
            initializeView();
        } else {
            Card top = topCard();
            top.setFaceUp(true);
            view.getChildren().add(top.getView());
        }
    }

    @Override
    protected void initializeView() //base pile view (no cards)
    {
        Rectangle placeholder = new Rectangle(80, 110);
        placeholder.setArcWidth(15);
        placeholder.setArcHeight(15);
        placeholder.setFill(Color.BEIGE);
        placeholder.setStroke(Color.BLACK);
        view.getChildren().add(placeholder);
    }
}
