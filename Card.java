import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;

public class Card 
{
    private final Suit suit;
    private final int rank;
    private boolean faceUp;
    private StackPane view;

    public Card(Suit suit, int rank) //constructor
    {
        this.suit = suit;
        this.rank = rank;
        this.faceUp = false;
        this.view = createView();
    }

    public Suit getSuit() { return suit; }
    public int getRank() { return rank; }
    public boolean isFaceUp() { return faceUp; }
    public StackPane getView() { return view; }

    public void setFaceUp(boolean faceUp) //set face up/down
    {
        this.faceUp = faceUp;
        updateView();
    }

    private StackPane createView() //create card view
    {
        StackPane stack = new StackPane();
        stack.setUserData(this);
        updateView();

        // Drag start
        stack.setOnDragDetected(event -> {
            PlayingField pf = CanfieldGUI.getPlayingFieldInstance();
            if (pf != null && faceUp) {
                pf.setSelectedCard(this);
                pf.setSelectedPile(pf.getPileForView(view));

                Dragboard db = stack.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(""); //placeholder
                db.setContent(content);

                WritableImage snapshot = stack.snapshot(new SnapshotParameters(), null);
                db.setDragView(snapshot);
            }
            event.consume();
        });

        return stack;
    }

    protected void updateView() //update card view
    {
        if (view == null) view = new StackPane();
        view.getChildren().clear();

        Rectangle rect = new Rectangle(80, 110);
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.setStroke(Color.BLACK);

        if (faceUp) {
            rect.setFill(Color.WHITE);
            Text label = new Text(rankToString() + "\n" + suitToString());
            label.setFont(Font.font(14));
            label.setFill(suit.isRed() ? Color.RED : Color.BLACK);
            view.getChildren().addAll(rect, label);
        } else {
            rect.setFill(Color.DARKGREEN);
            view.getChildren().add(rect);
        }
    }

    private String rankToString() //A, 2-10, J, Q, K 
    {
        return switch(rank) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> Integer.toString(rank);
        };
    }
    
    private String suitToString() { return suit.symbol();} //♠, ♥, ♦, ♣

    @Override
    public String toString() {
        return rankToString() + suitToString();
    }
}
