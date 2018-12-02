package View;


import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import gamelogic.ConnectLogic;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import playertype.Player;


public class GameManager extends Application{

  private static final int TILE_SIZE = 80;
  private static final int COLUMNS = 7;
  private static final int ROWS = 6;
  private Pane gamePane;
  private Scene gameScene;
  private Stage gameStage;
  private Shape gridShape;
  private Stage playerDetailsStage;
  private Disc[][] grid =  new Disc[COLUMNS][ROWS]; 
  private Pane discRoot =  new Pane(); 
  private ConnectLogic logic = new ConnectLogic(COLUMNS, ROWS); 
  private String winner = "";
  
  /******************************************************************
  * Creates the game panel for when you start a new game from
  * the Players Detail Manager;
  ******************************************************************/
  public GameManager() {

	  logic.addPlayer(play2);
	  logic.addPlayer(play4);
	//logic.addPlayer(play1);
	//logic.addPlayer(play3);
	
	  Pane gamePane = new Pane();
      gameScene = new Scene(gamePane, 800, 700);
      gameStage = new Stage();
      gameStage.setScene(gameScene);
      gridShape = makeGrid();
  	  gamePane.getChildren().add(discRoot); 
      gamePane.getChildren().add(gridShape);
  	  gamePane.getChildren().addAll(makeColumns());
  	  gridShape.setLayoutX(100);
      gridShape.setLayoutX(0);
    
  }
  
  /******************************************************************
  * This method returns gamePane, which acts as the rectangles
  * that allow you to pick what column to place your chip. 
  * Without this, the option to place another 
  ******************************************************************/
  private Parent createContent(){
  	
    createBackground();
    return gamePane;
  }
  
  Player play2 = new Player("r", 1, "Mr. Frodo");
  Player play4 = new Player("g", 2, "One-Eyed Willy");
  
//Player play1 = new Player("d", 1, "hackerman", "m", true);
//Player play3 = new Player("d", 3, "DESTROYEROFWORLDS", "m", true);

  
  /******************************************************************
  * Goes to the next page in the game menu.
  ******************************************************************/ 
  public void createNewGame(Stage playerDetailsStage) {
    this.playerDetailsStage = playerDetailsStage;
    this.playerDetailsStage.hide();
    gameStage.show();
    
  }
  
  /******************************************************************
   * Setter that takes in logic from ConnectLogic.
   ******************************************************************/
  public void setLogic(ConnectLogic logic) {
	  this.logic = logic;
  }
  
  /******************************************************************
  * Creates the game board grid.
  ******************************************************************/
  private Shape makeGrid(){
    Shape shape = new Rectangle((COLUMNS + 1) * TILE_SIZE, 
        (ROWS + 1) * TILE_SIZE);
    for(int y = 0; y < ROWS; y++) {
      for(int x = 0; x < COLUMNS; x++) {
        Circle circle = new Circle(TILE_SIZE / 2);
        circle.setCenterX(TILE_SIZE / 2);
        circle.setCenterY(TILE_SIZE / 2);
        circle.setTranslateX(x * (TILE_SIZE + 5) + TILE_SIZE / 3);
        circle.setTranslateY(y * (TILE_SIZE + 5) + TILE_SIZE / 3);
        
        shape = Shape.subtract(shape, circle);
      }
    } 
    
    Light.Distant light = new Light.Distant();
    light.setAzimuth(45.0);
    light.setElevation(30.0);

    Lighting lighting = new Lighting();
    lighting.setLight(light);
    lighting.setSurfaceScale(5.0);

    shape.setFill(Color.BLUE);
    shape.setEffect(lighting);
    return shape;


  }
  
  /******************************************************************
  * Transparent rectangle that indicates which column you are 
  * choosing to drop a chip into.
  ******************************************************************/
  private List<Rectangle> makeColumns(){
      List<Rectangle> list = new ArrayList<>();
     
      for(int x = 0; x < COLUMNS; x++) {
          Rectangle rect = new Rectangle(TILE_SIZE, (ROWS + 1) * TILE_SIZE);
          rect.setTranslateX(x * (TILE_SIZE + 5) + TILE_SIZE / 3);
          rect.setFill(Color.TRANSPARENT);
         
          rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(200, 200, 50, 0.3)));
          rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));
         
          final int column = x;
          rect.setOnMouseClicked(e -> placeDisc(new Disc(findColor()), column));
        
          list.add(rect);
        }
    	return list;
    }
  
  /******************************************************************
  * Method that implements game logic to determine winner.
  ******************************************************************/
  private void placeDisc(Disc disc, int column) {
	  int row = ROWS - 1;
	  if(!logic.getCurrentPlayer().getCompStatus()) {
		  do{
			  if(!getDisc(column, row).isPresent())
				  break;
			  
			  row--;
		  } while(row >= 0);
		  if(row < 0)
			  return;
		  
		  grid[column][row] = disc;
		  discRoot.getChildren().add(disc);
		  disc.setTranslateX(column * (TILE_SIZE + 5) 
				  + (TILE_SIZE / 3));
		  
		  TranslateTransition animation = new TranslateTransition
				  (Duration.seconds(0.5), disc);
		  animation.setToY(row * (TILE_SIZE + 5) + (TILE_SIZE / 3));
		  
		  logic.nextTurn(column);
		  if(logic.checkWin() == 1){ //if won
                ShowMessage(winner + " Wins!");
                  
		  }
		  winner = logic.getCurrentPlayer().getName();
		  animation.play();
		  
	  }
  }
  
  private void ShowMessage(String message) {
	  Alert alert = new Alert(AlertType.INFORMATION);
	  alert.setTitle("Game Finished");
	  alert.setHeaderText(message);
	  alert.setContentText("GAME IS OVER, RETURNING TO MENU.");
	  
	  alert.show();
  }
  
  private Optional<Disc> getDisc(int column, int row){
	  if(column < 0 || column >= COLUMNS 
			  || row < 0 || row >= ROWS)
		  return Optional.empty();
	  
	  return Optional.ofNullable(grid[column][row]);
  }
  
  /******************************************************************
  * Reference Logic: Method creates two disks, instead of viewing
  * them as players. 
  ******************************************************************/
  private static class Disc extends Circle{
	  public Disc(Color playcolor) {
		  super(TILE_SIZE /2, playcolor);
		  
		  setCenterX(TILE_SIZE / 2);
		  setCenterY(TILE_SIZE / 2);
	  }
  }
  
  private void createBackground() {
  
    Image backgroundImage = new Image("model/resources/"
        + "Slider-CL01-Background-256x256.png", 256,256,false,true);
    BackgroundImage background = new BackgroundImage(backgroundImage, 
        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, 
        BackgroundPosition.DEFAULT, null);
    gamePane.setBackground(new Background(background));
    
  }

  private Color findColor(){
	  Color color = Color.RED;
	  String playcolor = logic.getCurrentPlayer().getColor();
	  switch (playcolor) {
	  	case "r":
	  		color =  Color.RED;
	  		break;
	  	case "b":
	  		color = Color.BLUE;
	  		break;
	  	case "g":
	  		color = Color.GREEN;
	  		break;
	  	case "w":
	  		color = Color.WHITE;
	  		break;
	  }
	  return color;
	}

  @Override
  public void start(Stage stage) throws Exception{ 
	  stage.setScene(new Scene(createContent()));
	  stage.show();
	}

}