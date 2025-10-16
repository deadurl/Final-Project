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

    @Override
    public boolean canAccept(Card c)
    {
        return false; //cards cannot be manually placed in stock
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
    protected void updateView() //show only top card
    {
        view.getChildren().clear();
        if (cards.isEmpty()) {
            initializeView();
        } else {
            Card top = topCard();
            top.setFaceUp(false); //always face down
            Rectangle faceDown = new Rectangle(80, 110);
            faceDown.setArcWidth(15);
            faceDown.setArcHeight(15);
            faceDown.setFill(Color.BLUE); //blue for stock
            faceDown.setStroke(Color.BLACK);
            view.getChildren().add(faceDown);
        }
    }


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
