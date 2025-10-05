import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tableau extends CardPile 
{

    public Tableau(String name) 
    {
        super(name);
        initializeView();
    }

    @Override
    public StackPane getView() { return view;} //get pile view


    //check if can accept card (build down by alternating colors)
    public boolean canAccept(Card c) 
    {
        if (c == null) return false; //null check
        if (cards.isEmpty()) return true; //empty check (can accept any card if empty)

        Card top = topCard(); //get top card
        boolean oppositeColor = c.getSuit().isRed() != top.getSuit().isRed(); ///check opposite colors
        return oppositeColor && c.getRank() == top.getRank() - 1; //check rank down by 1
    }

   
    protected void initializeView() //base pile view (no cards)
    {
        Rectangle placeholder = new Rectangle(80, 110);
        placeholder.setArcWidth(15);
        placeholder.setArcHeight(15);
        placeholder.setFill(Color.SALMON);
        placeholder.setStroke(Color.BLACK);

        view.getChildren().add(placeholder);
    }
}
