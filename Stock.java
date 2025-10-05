import java.util.Collections;
import java.util.List;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Stock extends CardPile 
{

    public Stock() 
    {
        super("Stock"); //name
        initializeView();
    }

    
    public void shuffle() { Collections.shuffle(cards); } //shuffle stock

    
    public Card draw() { return removeTopCard(); } //draw top card

   //add cards to stock
    public void addAll(List<Card> list) 
    {
        if (list != null) //null check
        {
            cards.addAll(list); //add all cards
            updateView();
        }
    }

    @Override
    public StackPane getView() { return view; } //get pile view

    @Override
    protected void initializeView() //base pile view (no cards)
    {
        Rectangle placeholder = new Rectangle(80, 110);
        placeholder.setArcWidth(15);
        placeholder.setArcHeight(15);
        placeholder.setFill(Color.DARKGREEN);
        placeholder.setStroke(Color.BLACK);

        view.getChildren().add(placeholder);
    }
}
