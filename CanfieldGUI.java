import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CanfieldGUI extends Application 
{

    //create models and views
    private final PlayingField model = new PlayingField();
    private final StackPane stockView = new StackPane();
    private final StackPane reserveView = new StackPane();
    private final StackPane discardView = new StackPane();
    private final HBox foundationBox = new HBox(10);
    private final HBox tableauBox = new HBox(10);
    private final Label scoreLabel = new Label("Score: 0");
    
    private BorderPane root;

    private static PlayingField playingField; //static access for Card drag-and-drop
    public static PlayingField getPlayingFieldInstance() { return playingField; }

    @Override
    public void start(Stage primaryStage) 
    {
        playingField = model; //set static instance
        root = new BorderPane();
        root.setPadding(new Insets(20));

        root.setStyle("-fx-background-color: #006400;"); //green background

        //title and score
        Text title = new Text("Canfield!");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        title.setFill(javafx.scene.paint.Color.WHITE); //white text

        scoreLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        VBox topBox = new VBox(10, title, scoreLabel);
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        //button for starting game
        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        startButton.setOnAction(e -> startGameUI());

        StackPane startPane = new StackPane(startButton);
        startPane.setAlignment(Pos.CENTER);
        root.setCenter(startPane);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Canfield"); //window name
        primaryStage.show();
    }

    private void startGameUI() 
    {
        model.startGame();
        //set up views
        stockView.getChildren().clear();
        reserveView.getChildren().clear();
        discardView.getChildren().clear();
        foundationBox.getChildren().clear();
        tableauBox.getChildren().clear();

        //get card views
        stockView.getChildren().add(model.getStock().getView());
        reserveView.getChildren().add(model.getReserve().getView());
        discardView.getChildren().add(model.getDiscardPile().getView());

        //add labels to piles
        VBox stockBox = new VBox(5, new Label("Stock"), stockView);
        VBox reserveBox = new VBox(5, new Label("Reserve"), reserveView);
        VBox discardBox = new VBox(5, new Label("Discard"), discardView);

        //style labels for visibility
        for (VBox box : new VBox[]{stockBox, reserveBox, discardBox}) 
        {
            Label label = (Label) box.getChildren().get(0);
            label.setTextFill(javafx.scene.paint.Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            box.setAlignment(Pos.CENTER);
        }

        //get foundation views
        for (var foundation : model.getFoundations()) { foundationBox.getChildren().add(foundation.getView()); }
        //get tableau views
        for (var tableau : model.getTableaus()) { tableauBox.getChildren().add(tableau.getView()); }

        foundationBox.setAlignment(Pos.CENTER);
        tableauBox.setAlignment(Pos.CENTER);
    
        //layout
        HBox upperRow = new HBox(50, stockBox, reserveBox, discardBox);
        upperRow.setAlignment(Pos.CENTER);
        upperRow.setPadding(new Insets(20));

        VBox gameLayout = new VBox(30, upperRow, foundationBox, tableauBox);
        gameLayout.setAlignment(Pos.CENTER);

        root.setCenter(gameLayout);

        root.setStyle("-fx-background-color: #006400;"); //green background

        scoreLabel.setText("Score: " + model.getScore()); //score display

        enableDragDrop();
    }

    
    private void enableDragDrop() 
    {
        //drag-and-drop support for tableau piles
        for (var node : tableauBox.getChildren()) { addDragHandlers(node);}

       //drag-and-drop support for foundation piles
        for (var node : foundationBox.getChildren()) { addDragHandlers(node); }

        //drag-and-drop support for stock, reserve, discard
        addDragHandlers(stockView);
        addDragHandlers(reserveView);
        addDragHandlers(discardView);
    }

    //helper to add drag handlers
    private void addDragHandlers(javafx.scene.Node pile) 
    {
        pile.setOnDragOver(event -> {
            if (event.getGestureSource() != pile) 
            {
                event.acceptTransferModes(TransferMode.MOVE); //moving card
            }
            event.consume();
        });

        pile.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard(); //get dragboard
            if (db.hasString()) //check if has card data
            {
                model.moveCard(model.getSelectedPile(), model.getPileForView(pile)); //attempt move
                refreshUI(); //refresh views
                event.setDropCompleted(true); //indicate success
            } else {
                event.setDropCompleted(false); //indicate failure
            }
            event.consume(); //consume event
        });
    }

    private void refreshUI() //refresh all views
    {
        foundationBox.getChildren().clear(); 
        tableauBox.getChildren().clear();

        for (var foundation : model.getFoundations()) { foundationBox.getChildren().add(foundation.getView()); } //refresh foundation views
        for (var tableau : model.getTableaus()) { tableauBox.getChildren().add(tableau.getView()); } //refresh tableau views

        scoreLabel.setText("Score: " + model.getScore()); //update score
    }

    public static void main(String[] args) {
        launch(args);
    }
}
