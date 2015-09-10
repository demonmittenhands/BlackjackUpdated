package com.shooter.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Blackjack extends ApplicationAdapter {
	private OrthographicCamera camera; // make a camera
	private SpriteBatch batch;
	private Texture background;
	private Texture titleBackground;
	private int screenWidth;
	private int screenHeight;
	
	private Texture cardSpriteSheet;
	private Sprite[][] cards = new Sprite[4][13];
	
	private Stage stage;
	private Stage titleStage;
	private Skin skin;
	private TextField betField;

	private Deck deck;
	
	private int playerTotal;
	private int dealerTotal;
	private int playerBalance;
	
	private Label playerScore;
	private Label dealerScore;
	private Label playerCash;
	private Label playerBet;
	private Label result;
	
	private boolean playing; // trigger to turn on/off buttons based on game state.
	private boolean titleScreen = true; //toggle to load titlescreen
	
	private HashMap<String, Runnable> buttonMap = new HashMap<String, Runnable>();
	
	@Override
	public void create () {	
		
	 // setting up the game and the cards
		playing = false;
		//playerBalance = 500;
		playerTotal = 0;
		dealerTotal = 0;
		
		batch = new SpriteBatch();
		stage = new Stage(); // set up the stage so i can add buttons
		titleStage = new Stage(); //set up stage for the title screen
		
		// load the background 
        background = new Texture(Gdx.files.internal("table.jpg"));
        titleBackground = new Texture(Gdx.files.internal("TitleScreen.png"));
        screenWidth = background.getWidth();
		screenHeight = background.getHeight();
		
        // load the card sprite sheet
        cardSpriteSheet = new Texture(Gdx.files.internal("deck-of-playing-cards-all.png"));
        deck = new Deck(cardSpriteSheet);
        
        
		// set up a camera, set to background size.
		camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);        
        
  
        
        
      // Setting up the UI and what a nightmare!
        // start with a skin, and a 1x1 px square
        skin = new Skin();
		Pixmap pixmap = new Pixmap(100, 50, Format.RGBA8888); // sets the button default size
		pixmap.setColor(Color.CYAN); // button default color
		pixmap.fill();
		skin.add("buttonskin", new Texture(pixmap)); // make the button skin using the pixmap
 
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("textFieldSkin", new Texture(pixmap));
		
		
		// Store the default libgdx font under the name "default".
		BitmapFont bitmapfont = new BitmapFont();
		skin.add("default",bitmapfont);
 
		// make the button style  
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("buttonskin", Color.LIGHT_GRAY); // the way the button will look
		textButtonStyle.down = skin.newDrawable("buttonskin", Color.WHITE); // when it's clicked
		textButtonStyle.font = skin.getFont("default");
 
		
		// make the textfield style
		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.background = skin.newDrawable("textFieldSkin", Color.WHITE);
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.font = new BitmapFont();	
		
		
		// make the label style
		LabelStyle lableStyle = new LabelStyle();
		lableStyle.font = new BitmapFont();
		
		
	  // making the UI
		// the labels (hand totals and cash)
		playerScore = new Label("Player: ", lableStyle);
		playerScore.setPosition(500, 25);
		dealerScore = new Label("Dealer: ", lableStyle);
		dealerScore.setPosition(500, 275);
		playerCash = new Label("Cash: " + playerBalance, lableStyle);
		playerCash.setPosition(50, 100);
		playerBet = new Label("Bet: ", lableStyle);
		playerBet.setPosition(50, 80);
		result = new Label(" ", lableStyle);
		result.setPosition(500, 150);
	
		//add Buttons to map
		buttonMap.put("hitButton", new Runnable() {
				public void run() { hit();}
			});
		buttonMap.put("dealButton", new Runnable() {
			public void run() { deal();}
		});
		buttonMap.put("standButton", new Runnable() {
			public void run() { stand();}
		});
		buttonMap.put("easyMode", new Runnable() {
			public void run() { difficulty(1);}
		});
		buttonMap.put("mediumMode", new Runnable() {
			public void run() { difficulty(2);}
		});
		buttonMap.put("hardMode", new Runnable() {
			public void run() { difficulty(3);}
		});
		
		
		// make buttons
		final TextButton standButton = getButton("Stand", 240, 20, "standButton", textButtonStyle);
		final TextButton hitButton = getButton("Hit", 130, 20, "hitButton", textButtonStyle);
		final TextButton dealButton = getButton("Deal", 20, 20, "dealButton", textButtonStyle);
		final TextButton easyMode = getButton("Easy", 20, 20, "easyMode", textButtonStyle);
		final TextButton mediumMode = getButton("Normal", 130, 20, "mediumMode", textButtonStyle);
		final TextButton hardMode = getButton("Hard", 240, 20, "hardMode", textButtonStyle);
		
		// the bet/text field
		betField = new TextField("100",textFieldStyle);
		//betField.setText("test");
		//betField.setMessageText("0");
		betField.setPosition(100, 75);
		betField.setSize(100, 25);
		betField.setAlignment(1);

		// add the buttons to the stage
		stage.addActor(dealButton);
		stage.addActor(hitButton);
		stage.addActor(standButton);
		stage.addActor(betField);
		titleStage.addActor(easyMode);
		titleStage.addActor(mediumMode);
		titleStage.addActor(hardMode);
		
		// add the labels to the stage
		stage.addActor(playerScore);
		stage.addActor(dealerScore);
		stage.addActor(playerBet);
		stage.addActor(playerCash);
		stage.addActor(result);
        
		
	}

	private TextButton getButton(String buttonText, int xPosition, int yPosition, final String id, TextButtonStyle textButtonStyle) {
		TextButton button = new TextButton(buttonText, textButtonStyle);
		button.setPosition(xPosition, yPosition);
 
		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
		        buttonMap.get(id).run();
		    }			
		});
		return button;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
		batch.begin();
		if(titleScreen)
			batch.draw(titleBackground, 0, 0);
		else
			batch.draw(background, 0, 0);
		deck.draw(batch);
		batch.end();
		
		if(titleScreen){
			Gdx.input.setInputProcessor(titleStage); // this NEEDS to be here. so stage can have events
			titleStage.draw(); 
		} else {
			Gdx.input.setInputProcessor(stage); // this NEEDS to be here. so stage can have events
			stage.draw(); 
		}
		
	}
	
	public void difficulty(int diff){
		if(diff == 1)
			playerBalance = 5000;
		else if (diff == 2)
			playerBalance = 2500;
		else
			playerBalance = 500;
		
		playerCash.setText("Player: "+ playerBalance);
		titleScreen = false;

	}
	
	public void deal(){
		//System.out.println("dealt! bet = " + betField.getText());
		if (!playing){
			playing = true;
			deck.Deal();
			updateScores();
			result.setText(" ");
		}
	}
	
	public void hit(){
		//System.out.println("hit!");
		if (playing){
			deck.Hit(0);
			updateScores();
		}
	}
	
	public void stand(){
		//System.out.println("stand!");
		// dealer will draw to 17. updateScores() also evaluates bust conditions.
		if (playing){
			while (dealerTotal <=17){
				deck.Hit(1);
				updateScores();
			}
			
			
			// if nobody bust. busted? bustered...
			// then playing == true. now we evaluate who won based on card values.
			
			if (playing){
				int betAmount = Integer.parseInt(betField.getText());
				if (dealerTotal > playerTotal){
					result.setText("DEALER WINS! -" + betAmount);
					playerBalance -= betAmount;
				} else if (dealerTotal < playerTotal){
					result.setText("YOU WIN! +" + betAmount);
					playerBalance += betAmount;
				} else if (dealerTotal == playerTotal){
					result.setText("PUSH! + 0");
				}
			}
			playerCash.setText("Player: "+ playerBalance);
			playing = false;
		}
			
	}	
	
	public void updateScores(){
		playerTotal = 0;
		dealerTotal = 0;
		int betAmount = Integer.parseInt(betField.getText());
		
		// display the player's hand and evaluate win/loss conditions
		for(Card card : deck.playerHand){
			playerTotal += card.getValue();
			playerScore.setText("Player: "+ playerTotal);
			if (playerTotal >21){
				result.setText("BUST! -" + betAmount);
				playing = false;
				playerBalance -= betAmount;
				playerCash.setText("Player: "+ playerBalance);
			}
		}
		
		// display the dealer's hand and win/loss conditions
		for(Card card : deck.dealerHand){
			dealerTotal += card.getValue();
			dealerScore.setText("Dealer: "+ dealerTotal);
			if (dealerTotal >21){
				result.setText("DEALER BUST! +" + betAmount);
				playing = false;
				playerBalance += betAmount;
				playerCash.setText("Player: "+ playerBalance);
			}
		}

	} // end updateScores()
	
}
