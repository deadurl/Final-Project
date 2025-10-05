import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Reserve extends CardPile 
{
    //constructor
    public Reserve() 
    {
        super("Reserve"); //pile name
        initializeView();
    }

    public Card dealCard() //deal top card from reserve
    {
        Card c = removeTopCard(); //remove top card
        if (c != null) c.setFaceUp(true); //set face up
        return c; //return dealt card
    }

    @Override
    public StackPane getView() { return view;} //get pile view

    @Override
    protected void initializeView() //base pile view (no cards)
    {
        Rectangle placeholder = new Rectangle(80, 110);
        placeholder.setArcWidth(15);
        placeholder.setArcHeight(15);
        placeholder.setFill(Color.LIGHTBLUE);
        placeholder.setStroke(Color.BLACK);

        view.getChildren().add(placeholder); 
    }
}
