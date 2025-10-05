import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

public class Card 
{
    private final Suit suit;
    private final int rank; 
    private boolean faceUp;
    private StackPane view;

    //constructor
    public Card(Suit suit, int rank) 
    {
        this.suit = suit;
        this.rank = rank;
        this.faceUp = false;
        this.view = createView();
    }

    //getters
    public Suit getSuit() { return suit; }
    public int getRank() { return rank; }
    public boolean isFaceUp() { return faceUp; }
    public StackPane getView() { return view; }
    public int getRankInt() { return rank; }

    //set card face
    public void setFaceUp(boolean faceUp) 
    {
        this.faceUp = faceUp;
        updateView();
    }

    //card creation
    private StackPane createView() 
    { 
        Rectangle rect = new Rectangle(80, 110);
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.setFill(Color.LIGHTGRAY);
        rect.setStroke(Color.BLACK);

        Text label = new Text(rankToString() + "\n" + suitToString());
        label.setFont(Font.font(14));
        label.setFill(suit == Suit.HEARTS || suit == Suit.DIAMONDS ? Color.RED : Color.BLACK);

        StackPane stack = new StackPane(rect, label);
        stack.setUserData(this); //reference for event handling

        //drag detection
        stack.setOnDragDetected(event -> {
            PlayingField playingField = CanfieldGUI.getPlayingFieldInstance();
            if (playingField != null) 
            {
                playingField.setSelectedCard(this);
                playingField.setSelectedPile(playingField.getPileForView(view));
            }

            Dragboard db = stack.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(toString());
            db.setContent(content);

            // Add card image preview while dragging
            WritableImage snapshot = stack.snapshot(new SnapshotParameters(), null);
            db.setDragView(snapshot);

            event.consume();
        });

        //drag done
        stack.setOnDragDone(event -> event.consume());

        return stack;
    }

    //update view
    private void updateView()
    {
        view.getChildren().clear();

        Rectangle rect = new Rectangle(80, 110);
        rect.setArcWidth(15);
        rect.setArcHeight(15);

        if (faceUp) //show face
        {
            rect.setFill(Color.WHITE);
            Text label = new Text(rankToString() + "\n" + suitToString());
            label.setFont(Font.font(14));
            label.setFill(suit == Suit.HEARTS || suit == Suit.DIAMONDS ? Color.RED : Color.BLACK); //red for hearts/diamonds
            view.getChildren().addAll(rect, label);
        } else {
            rect.setFill(Color.DARKGREEN);
            view.getChildren().add(rect);
        }

        rect.setStroke(Color.BLACK); //black border
    }

    //rank to string
    private String rankToString() 
    {
        switch (rank) 
        {
            case 1: return "A";
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            default: return Integer.toString(rank);
        }
    }

    //suit to string
    private String suitToString() 
    {
        return switch (suit) 
        {
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
            case SPADES -> "♠";
        };
    }

    @Override
    public String toString() { return rankToString() + suitToString(); }
}
