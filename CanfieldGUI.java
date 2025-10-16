import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CanfieldGUI extends Application 
{
    // create models and views
    private final PlayingField model = new PlayingField();
    private final StackPane stockView = new StackPane();
    private final StackPane reserveView = new StackPane();
    private final StackPane wasteView = new StackPane();
    private final HBox foundationBox = new HBox(10);
    private final HBox tableauBox = new HBox(10);
    private final Label scoreLabel = new Label("Score: 0");
    private BorderPane root;

    private static PlayingField playingField;
    public static PlayingField getPlayingFieldInstance() { return playingField; }

    @Override
    public void start(Stage primaryStage) //start method
    {
        playingField = model;
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #006400;");

        Text title = new Text("Canfield!");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        title.setFill(javafx.scene.paint.Color.WHITE);

        scoreLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        VBox topBox = new VBox(10, title, scoreLabel);
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        startButton.setOnAction(e -> startGameUI());

        StackPane startPane = new StackPane(startButton);
        startPane.setAlignment(Pos.CENTER);
        root.setCenter(startPane);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Canfield");
        primaryStage.show();
    }

    private void startGameUI() //initialize game UI
    {
        model.startGame();

        stockView.getChildren().clear();
        reserveView.getChildren().clear();
        wasteView.getChildren().clear();
        foundationBox.getChildren().clear();
        tableauBox.getChildren().clear();

        // assign IDs for drag/drop recognition
        model.getStock().getView().setId("stock");
        model.getReserve().getView().setId("reserve");
        model.getWaste().getView().setId("waste");

        stockView.getChildren().add(model.getStock().getView());
        reserveView.getChildren().add(model.getReserve().getView());
        wasteView.getChildren().add(model.getWaste().getView());

        VBox stockBox = new VBox(5, new Label("Stock"), stockView);
        VBox wasteBox = new VBox(5, new Label("Waste"), wasteView);
        VBox reserveBox = new VBox(5, new Label("Reserve"), reserveView);

        for (VBox box : new VBox[]{stockBox, wasteBox, reserveBox})
        {
            Label label = (Label) box.getChildren().get(0);
            label.setTextFill(javafx.scene.paint.Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            box.setAlignment(Pos.CENTER);
        }

        // layout left row
        HBox leftRow = new HBox(20, stockBox, wasteBox, reserveBox);
        leftRow.setAlignment(Pos.TOP_LEFT);
        leftRow.setPadding(new Insets(20));

        //foundations
        for (int i = 0; i < model.getFoundations().size(); i++)
        {
            var foundation = model.getFoundations().get(i);
            foundation.getView().setId("foundation" + i);
            foundationBox.getChildren().add(foundation.getView());
        }

        //tableaus
        for (int i = 0; i < model.getTableaus().size(); i++)
        {
            var tableau = model.getTableaus().get(i);
            tableau.getView().setId("tableau" + i);
            tableauBox.getChildren().add(tableau.getView());
        }

        foundationBox.setAlignment(Pos.CENTER);
        tableauBox.setAlignment(Pos.CENTER);

        VBox gameLayout = new VBox(30, leftRow, foundationBox, tableauBox);
        gameLayout.setAlignment(Pos.TOP_LEFT);

        root.setCenter(gameLayout);
        root.setStyle("-fx-background-color: #006400;");
        scoreLabel.setText("Score: " + model.getScore());

        enableDragDrop();

        //Stock click to draw 3 cards into Waste
        stockView.setOnMouseClicked(e -> {
            model.drawFromStockRuleOf3();
            refreshUI();
        });
    }

    private void enableDragDrop()
    {
        //Drag cards from tableau
        for (int i = 0; i < tableauBox.getChildren().size(); i++)
        {
            var pileView = tableauBox.getChildren().get(i);
            makePileDraggable(pileView, "tableau" + i);
        }

        //Drag cards from reserve
        makePileDraggable(reserveView, "reserve");

        //Drag top card from waste only
        makePileDraggable(wasteView, "waste");

        //Enable drop targets on tableau and foundations
        for (var node : foundationBox.getChildren())
            addDropHandlers(node);

        for (var node : tableauBox.getChildren())
            addDropHandlers(node);
    }

    private void makePileDraggable(javafx.scene.Node pileView, String pileId)
    {
        pileView.setOnDragDetected((MouseEvent event) -> {
            PlayingField pf = getPlayingFieldInstance();
            CardPile sourcePile = pf.getPileById(pileId);
            if (sourcePile != null && !sourcePile.isEmpty())
            {
                Card card = sourcePile.topCard();
                pf.setSelectedPile(sourcePile);
                pf.setSelectedCard(card);

                Dragboard db = pileView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(pileId);
                db.setContent(content);

                event.consume();
            }
        });
    }

    private void addDropHandlers(javafx.scene.Node node) //make node a drop target
    {
        //Drag over
        node.setOnDragOver(event -> {
            if (event.getGestureSource() != node && event.getDragboard().hasString())
                event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });
        //Drop
        node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) //valid drop
            {
                PlayingField pf = getPlayingFieldInstance(); //get model instance
                CardPile sourcePile = pf.getSelectedPile(); //get source pile
                Card draggedCard = pf.getSelectedCard(); //get dragged card
                CardPile targetPile = pf.getPileForView(node); //get target pile

                if (sourcePile != null && targetPile != null && draggedCard != null) //valid move
                {
                    //attempt move
                    boolean moved = pf.moveCard(sourcePile, targetPile);
                    if (moved) refreshUI(); //refresh UI if moved
                }

                event.setDropCompleted(true);
            }
            event.consume();
        });
    }
    //Refresh UI after moves
    private void refreshUI()
    {
        foundationBox.getChildren().clear();
        tableauBox.getChildren().clear();

        for (int i = 0; i < model.getFoundations().size(); i++) //foundation views
        {
            var foundation = model.getFoundations().get(i);
            foundation.getView().setId("foundation" + i);//set ID
            foundationBox.getChildren().add(foundation.getView());//add to box
        }

        for (int i = 0; i < model.getTableaus().size(); i++)//tableau views
        {
            var tableau = model.getTableaus().get(i);
            tableau.getView().setId("tableau" + i);//set ID
            tableauBox.getChildren().add(tableau.getView());
        }

        stockView.getChildren().clear();
        reserveView.getChildren().clear();
        wasteView.getChildren().clear();

        stockView.getChildren().add(model.getStock().getView()); //stockView
        reserveView.getChildren().add(model.getReserve().getView()); //reserveView
        wasteView.getChildren().add(model.getWaste().getView()); //wasteView

        scoreLabel.setText("Score: " + model.getScore()); //update score
        enableDragDrop(); //re-enable drag/drop
    }

    public static void main(String[] args) { launch(args); }
}
