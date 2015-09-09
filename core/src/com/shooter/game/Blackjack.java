package com.shooter.game;

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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.StringBuilder;

public class Blackjack extends ApplicationAdapter {
	private OrthographicCamera camera; // make a camera
	private SpriteBatch batch;
	private Texture background;
	private int screenWidth;
	private int screenHeight;
	
	private Texture cardSS;
	private Sprite[][] cards = new Sprite[4][13];
	
	private Stage stage;
	private Skin skin;
	private TextField betField;

	private Deck deck;
	
	private int playerTotal;
	private int aiTotal;
	private int playerBalance;
	
	private Label pScore;
	private Label aScore;
	private Label pCash;
	private Label pBet;
	private Label result;
	
	private boolean playing; // trigger to turn on/off buttons based on game state.
	
	@Override
	public void create () {	
		
	 // setting up the game and the cards
		playing = false;
		playerBalance = 500;
		playerTotal = 0;
		aiTotal = 0;
		
		batch = new SpriteBatch();
		stage = new Stage(); // set up the stage so i can add buttons
		
		// load the background 
        background = new Texture(Gdx.files.internal("marion_co_gaming_table_felt_blackjack.jpg"));
        screenWidth = background.getWidth();
		screenHeight = background.getHeight();
		
        // load the card sprite sheet
        cardSS = new Texture(Gdx.files.internal("deck-of-playing-cards-all.png"));
        deck = new Deck(cardSS);
        
        
		// set up a camera, set to background size.
		camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);        
        
  
        
        
      // Setting up the UI and what a nightmare!
        // start with a skin, and a 1x1 px square
        skin = new Skin();
		Pixmap pixmap = new Pixmap(100, 50, Format.RGBA8888); // sets the button default size
		pixmap.setColor(Color.CYAN); // button default color
		pixmap.fill();
		skin.add("btnskin", new Texture(pixmap)); // make the button skin using the pixmap
 
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("tfskin", new Texture(pixmap));
		
		
		// Store the default libgdx font under the name "default".
		BitmapFont bfont = new BitmapFont();
		skin.add("default",bfont);
 
		// make the button style  
		TextButtonStyle tbs = new TextButtonStyle();
		tbs.up = skin.newDrawable("btnskin", Color.LIGHT_GRAY); // the way the button will look
		tbs.down = skin.newDrawable("btnskin", Color.WHITE); // when it's clicked
		tbs.font = skin.getFont("default");
 
		
		// make the textfield style
		TextFieldStyle tfs = new TextFieldStyle();
		tfs.background = skin.newDrawable("tfskin", Color.WHITE);
		tfs.fontColor = Color.BLACK;
		tfs.font = new BitmapFont();	
		
		
		// make the label style
		LabelStyle ls = new LabelStyle();
		ls.font = new BitmapFont();
		
		
	  // making the UI
		// the labels (hand totals and cash)
		pScore = new Label("Player: ", ls);
		pScore.setPosition(500, 25);
		aScore = new Label("Dealer: ", ls);
		aScore.setPosition(500, 275);
		pCash = new Label("Cash: " + playerBalance, ls);
		pCash.setPosition(50, 100);
		pBet = new Label("Bet: ", ls);
		pBet.setPosition(50, 80);
		result = new Label(" ", ls);
		result.setPosition(500, 150);
			
		// the DEAL button
		final TextButton dealButton = new TextButton("Deal",tbs);
		dealButton.setPosition(20, 20);
 
		dealButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
		        Deal();
		    }			
		});
		
		// the HIT button
		final TextButton hitButton = new TextButton("Hit",tbs);
		hitButton.setPosition(130, 20);
 
		hitButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
		        Hit();
		    }			
		});

		// the STAND button
		final TextButton standButton = new TextButton("Stand",tbs);
		standButton.setPosition(240, 20);
 
		standButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
		        Stand();
		    }			
		});

		
		// the bet/text field
		betField = new TextField("",tfs);
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
		
		// add the labels to the stage
		stage.addActor(pScore);
		stage.addActor(aScore);
		stage.addActor(pBet);
		stage.addActor(pCash);
		stage.addActor(result);
        
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.input.setInputProcessor(stage); // this NEEDS to be here. so stage can have events

		batch.begin();
		batch.draw(background, 0, 0);
		deck.draw(batch);
		batch.end();
		
		stage.draw();
		
	}
	
	public void Deal(){
		//System.out.println("dealt! bet = " + betField.getText());
		if (!playing){
			playing = true;
			deck.Deal();
			updateScores();
			result.setText(" ");
		}
	}
	
	public void Hit(){
		//System.out.println("hit!");
		if (playing){
			deck.Hit(0);
			updateScores();
		}
	}
	
	public void Stand(){
		//System.out.println("stand!");
		// dealer will draw to 17. updateScores() also evaluates bust conditions.
		if (playing){
			while (aiTotal <=17){
				deck.Hit(1);
				updateScores();
			}
			
			
			// if nobody bust. busted? bustered...
			// then playing == true. now we evaluate who won based on card values.
			
			if (playing){
				int betAmount = Integer.parseInt(betField.getText());
				if (aiTotal > playerTotal){
					result.setText("DEALER WINS! -" + betAmount);
					playerBalance -= betAmount;
				} else if (aiTotal < playerTotal){
					result.setText("YOU WIN! +" + betAmount);
					playerBalance += betAmount;
				} else if (aiTotal == playerTotal){
					result.setText("PUSH! + 0");
				}
			}
			pCash.setText("Player: "+ playerBalance);
			playing = false;
		}
			
	}	
	
	public void updateScores(){
		playerTotal = 0;
		aiTotal = 0;
		int betAmount = Integer.parseInt(betField.getText());
		
		// display the player's hand and evaluate win/loss conditions
		for(Card c : deck.phand){
			playerTotal += c.getValue();
			pScore.setText("Player: "+ playerTotal);
			if (playerTotal >21){
				result.setText("BUST! -" + betAmount);
				playing = false;
				playerBalance -= betAmount;
				pCash.setText("Player: "+ playerBalance);
			}
		}
		
		// display the dealer's hand and win/loss conditions
		for(Card c : deck.ahand){
			aiTotal += c.getValue();
			aScore.setText("Dealer: "+ aiTotal);
			if (aiTotal >21){
				result.setText("DEALER BUST! +" + betAmount);
				playing = false;
				playerBalance += betAmount;
				pCash.setText("Player: "+ playerBalance);
			}
		}

	} // end updateScores()
	
}
