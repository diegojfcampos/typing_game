package application;


/*
 * 
 * 
 * Diego de Jesus Faria Campos 3087950
 * 
 * 
 * */


//Imports
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.print.DocFlavor.URL;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TypingGame extends Application {

	// Setting components

	// Elements
	Label lblInstruction, lblErrors, lblCountErrors, lblSentence, lblSentenceChecked, lblTimeLeft, lblSelectLevel, lblStartTitle;
	TextField txtInput;
	Button btnStart, btnInitial, btnLevel, btnHard, btnEasy, btnMenu;
	ProgressBar progBar;
	ProgressIndicator progIndicator;

	String phraseToType;

	private static int countErrors, countHits;

	String phraseTyped;
	
	//TimeLine
	Timeline timeline;
	private static int elapsedTime;
	int elapsedTimeEasy, elapsedTimeHard;
	
	//Icon Image
	Image icon;
	
	//ArrayList with phrases
	ArrayList<String> phraselist;
	boolean gameOver, displayGlame, hard;
	
	long maxtaskHard, maxtaskEasy;

	// Task
	Task<Void> task;

	// Stages and Scenes - Scene Navigation
	private static Stage guiStage;
	private static int countPhrases = 0;
	private static long max;

	Scene sceneGame, sceneMenu, sceneLevelConfig;
	
	//Constructor instancing elements
	public TypingGame() {

		lblStartTitle = new Label("Improving  your typping skills while getting fun!");
		btnInitial = new Button("Start");

		// Instancing components
		lblInstruction = new Label("Type each sentence into the lower textfield");
		lblErrors = new Label("Errors: ");
		lblTimeLeft = new Label("Time Left ");
		lblCountErrors = new Label("0");
		lblSentence = new Label("");
		lblSentenceChecked = new Label("");
		lblSelectLevel = new Label("Choose the Size of your Challenge.");
		txtInput = new TextField();
		btnStart = new Button("Start");		
		btnLevel = new Button("Level");
		btnHard = new Button("Hard");
		btnEasy = new Button("Easy");
		btnMenu = new Button("Menu");
	
		progBar = new ProgressBar(0);
		progIndicator = new ProgressIndicator(0);
		countErrors = 0;
		gameOver = true;
		phraselist = new ArrayList<>();

		hard = false;
		maxtaskHard = 770000000 / 2;
		maxtaskEasy = 770000000;
		elapsedTimeEasy = 30;
		elapsedTimeHard = 15;
		
	
		// Adjusting Elements
		txtInput.setDisable(true);
		btnStart.setPrefHeight(40);
		btnStart.setPrefWidth(120);
		
		btnLevel.setPrefHeight(40);
		btnLevel.setPrefWidth(120);
		
		btnInitial.setPrefHeight(40);
		btnInitial.setPrefWidth(120);
		
		btnHard.setPrefHeight(40);
		btnHard.setPrefWidth(120);
		
		btnEasy.setPrefHeight(40);
		btnEasy.setPrefWidth(120);
		
		
		
		btnMenu.setId("btnMenu");
		
		
		progIndicator.setMinSize(50, 50);

		lblStartTitle.setStyle("-fx-font-size:  1.2em");
		lblSentence.setStyle("-fx-font-size:  1.5em");
		lblSentence.setTextFill(Color.BLACK);

	}
	
	//Init setting up actions
	public void init() {
		//Start button will provide the game start
		btnStart.setOnAction(event -> {
			System.out.println("State into button" + gameOver);
			if (gameOver) {
				startGame();

			} else {
				stopTimeLine();				
				cancelTask();
				resetGame();
			}

		});
		//SetonKeyPressed event so when the user press enter his phrase will be verified
		txtInput.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				checkPhrase();

			}
		});
		
		//initial Button on the scene Menu the first scene that will open and aim the level scene
		btnInitial.setOnAction(event -> {
			TypingGame.getStage().setScene(sceneLevelConfig);

		});		
		
		//When the user is playing and click to go back to the menu
		btnMenu.setOnAction(event ->{
			stopTimeLine();
			cancelTask();
			lblCountErrors.setText("Errors: ");
			lblSentenceChecked.setText(" ");
			lblSentence.setText(" ");
			lblTimeLeft.setText("Elapsed Time ");
			TypingGame.getStage().setScene(sceneMenu);
			
		});
		//Action that will set up the level choosed by the player
		btnEasy.setOnAction(event ->{			
			TypingGame.getStage().setScene(sceneGame);
			hard = false;
		});
		//Action that will set up the level choosed by the player
		btnHard.setOnAction(event ->{			
			TypingGame.getStage().setScene(sceneGame);
			hard = true;
		});

	}
	//Cancel thread binded with progress bar and indicator
	private void cancelTask() {
		if (task != null) {
			task.cancel();
		}
	}
	//Start methode that initialize the  thread binded with progress bar and indicator
	private void startTask() {
		task = new Task<Void>() {
			@Override
			public Void call() throws Exception {	
				
				if(hard) {
					max = maxtaskHard;
					elapsedTime = elapsedTimeHard;
				}else {
					max = maxtaskEasy;
					elapsedTime = elapsedTimeEasy;
				}
				
				for (long i = 1; i <= max; i++) {
					if (isCancelled()) {
						updateProgress(0, 0);
						break;
					}
					updateProgress(i, max);
					if (i == max) {
						createScoreTable();
						resetGame();
						break;
					}
				}
				return null;
			}
		};
		
		//Biding and starting the thread
		new Thread(task).start();
		progIndicator.progressProperty().bind(task.progressProperty());
		progBar.progressProperty().bind(task.progressProperty());

	}
	
	//Gettin phrases from the file A to Z on hard mode
	public void generatePhrase() {

		try {
			// Reading file setences.csv
			BufferedReader bufferedFile = new BufferedReader(new FileReader("./assets/sentences.csv"));
			String phrase = "";

			// Adding phrases into ArrayList
			while ((phrase = bufferedFile.readLine()) != null) {
				phraselist.add(phrase);

			}

			// Closing file
			bufferedFile.close();

		} catch (IOException e) {
			System.out.println("Something went wrong reading the file");
			e.printStackTrace();
		}

	}
	
	//Get phrase to aply into the game
	public String getPhrase() {
		try {
			countPhrases++;
			Random rand = new Random();
			int randomNum = rand.nextInt(phraselist.size());

			String randomPhrase = phraselist.get(randomNum);
			phraselist.remove(randomNum);

			System.out.println(randomPhrase);
			return randomPhrase;
		} catch (Exception e) {
			System.out.println("Probaly array list is empty");
			e.printStackTrace();
			return "error";
		}
	}
	//Checking phrases that was typed
	public void checkPhrase() {

		phraseTyped = txtInput.getText();
		if (!phraseToType.equals(phraseTyped)) {
			countErrors++;
			lblSentenceChecked.setText(phraseToType);
			lblSentenceChecked.setTextFill(Color.RED);
			lblErrors.setText("Errors: " + countErrors);
		}else {
			lblSentenceChecked.setText(phraseToType);
			lblSentenceChecked.setTextFill(Color.GREEN);
			countHits++;
		}

		stopTimeLine();
		cancelTask();
		
		if(!hard) {
			if (countPhrases == 10) {
				resetGame();
			} else {
				startGame();
			}
		}else {
			startGame();
		}		
		

	}
	//Starting game
	public void startGame() {

		System.out.println("State into StarteGame()" + gameOver);
		gameOver = false;
		txtInput.setDisable(false);
		txtInput.clear();
		btnStart.setText("Stop");
		if(hard) {
			elapsedTime = elapsedTimeHard;
		}else {
			elapsedTime = elapsedTimeEasy;
		}
		lblTimeLeft.setText("Time Left  ");

		generatePhrase();

		if (phraselist.isEmpty()) {
			resetGame();
			generatePhrase();
		} else {
			phraseToType = getPhrase();
			lblSentence.setText(phraseToType);
		}

		if (elapsedTime == 0) {
			resetGame();
		}

		startTask();
		startTimeLine();
	}
	
	//Methode to start the timeline
	public void startTimeLine() {

		timeline = new Timeline(new KeyFrame(Duration.millis(1000), timerTick -> {
			elapsedTime--;
			
			  if (elapsedTime == 0) {
				    timeline.stop();
				    
				  }

			lblTimeLeft.setText("Time Left  " + elapsedTime);

		}));

		timeline.setCycleCount(Animation.INDEFINITE);
		
		timeline.play();

	}
	
	//Mehode to stop the timeline
	public void stopTimeLine() {

		if (timeline != null) {
			timeline.stop();
		}

	}
	
	//Mehode to reset the game
	public void resetGame() {

		
		gameOver = true;
		btnStart.setText("Start");
		txtInput.setDisable(true);
		countPhrases = 0;
		
		if(hard) {
			elapsedTime = elapsedTimeHard;
		}else {
			elapsedTime = elapsedTimeEasy;
		}
		
		countErrors = 0;
		lblErrors.setText("Errors: 0");
		;
		lblTimeLeft.setText("Time Left ");
		cancelTask();
		stopTimeLine();
		createScoreTable();

	}

	public static Stage getStage() {
		/*
		 * When I was changing the scene into the Stage i was having the error bellow
		 * 
		 * Incomplete attachment. (GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT)(FBO - 820)
		 * Error creating framebuffer object with Object 7).
		 * 
		 * But I noticed that when I move the window it's was coming back to work
		 * 
		 * So i just setWidth(601) changing the minimum of the Stage's size and works
		 */
		guiStage.setWidth(601);
		return guiStage;
	}
	//Scoreboard Dialog Pane
	public void createScoreTable() {		

		Alert scoreTable = new Alert(AlertType.INFORMATION);
		scoreTable.setTitle("Score Board");
		scoreTable.setHeaderText("Game Over!");
		scoreTable.setContentText("Errors: " + countErrors + "\n" + "Hits: " + countHits);
		scoreTable.setGraphic(null);
		DialogPane dialogPane = scoreTable.getDialogPane();
		
		dialogPane.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		dialogPane.setId("score-table-alert");
	
		
		scoreTable.showAndWait();

	}

	public void start(Stage primaryStage) {
		try {
			// Setting Up Primary Stage
			primaryStage.setTitle("Typing Game");

			// Setting Default Size
			primaryStage.setHeight(400);
			primaryStage.setWidth(600);

			// Setting Minimum size avoiding to broke the screen, keeping the size always
			// useful to play.
			primaryStage.setMinWidth(650);
			primaryStage.setMinHeight(370);

			// Adding Icon			
			icon = new Image("./assets/icon.png");
			primaryStage.getIcons().add(icon);
			
			

			// Creating containers

			// TypingGame Layout as root
			BorderPane root = new BorderPane();

			// Grid Layout to put into center
			GridPane grid = new GridPane();

			// Containers to put into bottom
			VBox vbBotton = new VBox();

			// Containers to set up the bottom
			VBox vbProg = new VBox();
			HBox hbButton = new HBox();

			// Setting up responsive functions
			grid.maxHeightProperty().bind(primaryStage.widthProperty());
			grid.maxWidthProperty().bind(primaryStage.widthProperty());
			progBar.maxWidthProperty().bind(primaryStage.widthProperty());
			txtInput.minWidthProperty().bind(primaryStage.widthProperty().divide(1.5));

			// Setting up paddings and spaces
			root.setPadding(new Insets(10));
			grid.setPadding(new Insets(15));
			grid.setVgap(10);
			grid.setHgap(10);
			hbButton.setPadding(new Insets(10));
			vbProg.setSpacing(5);
			vbProg.setPadding(new Insets(10));

			// Filling up and setting positions of bottom containers

			// @grid
			grid.add(lblInstruction, 0, 0);
			grid.add(lblSentenceChecked, 0, 4);
			grid.add(lblSentence, 0, 5);
			grid.add(progIndicator, 2, 5);
			grid.add(txtInput, 0, 7);
			grid.add(lblErrors, 2, 7);

			// @Caninters
			vbProg.getChildren().addAll(lblTimeLeft, progBar);
			hbButton.getChildren().addAll(btnStart, btnMenu);
			hbButton.setAlignment(Pos.CENTER);
			hbButton.setSpacing(50);
			hbButton.setPadding(new Insets(20));
			vbBotton.getChildren().addAll(vbProg, hbButton);

			// Setting up root layout
			root.setCenter(grid);
			root.setBottom(vbBotton);

			// TScene Menu - A second Scene to be displayed if the user start the

			// Creating new Scene and Copying Stage
			BorderPane paneMenu = new BorderPane();
			VBox vboxMenu = new VBox();
			guiStage = primaryStage;

			// Setting VBox
			vboxMenu.getChildren().addAll(lblStartTitle, btnInitial);
			vboxMenu.setAlignment(Pos.CENTER);
			vboxMenu.setSpacing(50);
			vboxMenu.setPadding(new Insets(100));

			paneMenu.setCenter(vboxMenu);
			
			//Creating Pane Level
			BorderPane paneLevelConfig = new BorderPane();
			VBox vboxLevelConfig = new VBox();
			
			vboxLevelConfig.getChildren().addAll(lblSelectLevel, btnEasy, btnHard);
			vboxLevelConfig.setAlignment(Pos.CENTER);
			vboxLevelConfig.setSpacing(50);
			vboxLevelConfig.setPadding(new Insets(100));
			
			paneLevelConfig.setCenter(vboxLevelConfig);
			
			// Creating setting scenes into the primaryStage and CSS
			sceneGame = new Scene(root, 400, 600);
			sceneMenu = new Scene(paneMenu, 400, 600);
			sceneLevelConfig = new Scene(paneLevelConfig, 400, 600);

			sceneGame.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			sceneMenu.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			sceneLevelConfig.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(sceneMenu);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
