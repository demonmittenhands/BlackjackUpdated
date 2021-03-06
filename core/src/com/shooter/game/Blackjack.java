package com.shooter.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Blackjack extends ApplicationAdapter {
	private String username = "Guest";
	private OrthographicCamera camera; // make a camera
	private SpriteBatch batch;
	private Texture background;
	private Texture titleBackground;
	private Texture gameOverBackground;
	private int screenWidth;
	private int screenHeight;

	private Texture cardSpriteSheet;

	private Stage stage;
	private Stage titleStage;
	private Stage gameOverStage;
	private Skin skin;


	private Deck deck;

	private int playerTotal;
	private int dealerTotal;
	private int playerBalance = 1;
	private int betAmount = 0;
	
	private float elapsedTime = 0f;

	private Label playerScore;
	private Label dealerScore;
	private Label playerCash;
	private Label playerBet;
	private Label result;
	private Label chip1, chip5, chip25, chip100, chip500;
	private Label userText;
	
	private TextField userField;

	private boolean playing; // trigger to turn on/off buttons based on game
								// state.
	private boolean titleScreen = true; // toggle to load titlescreen
	private boolean gameOverScreen = false; // will be toggled on when player loses

	private String sPlayerScore = "Player: ";
	private String sPlayerCash = "Cash: $";
	private String sDealerScore = "Dealer: ";
	private String sPlayerBet = "Bet: $";
	
	
	private HashMap<String, Runnable> buttonMap = new HashMap<String, Runnable>();

	@Override
	public void create() {
		
		// setting up the game and the cards
		playing = false;
		playerTotal = 0;
		dealerTotal = 0;

		batch = new SpriteBatch();
		stage = new Stage(); // set up the stage so i can add buttons
		titleStage = new Stage(); // set up stage for the title screen
		gameOverStage = new Stage(); // set up stage for the game over screen

		// load the background
		background = new Texture(Gdx.files.internal("table.jpg"));
		titleBackground = new Texture(Gdx.files.internal("TitleScreen.png"));
		gameOverBackground = new Texture(Gdx.files.internal("gameOver.jpg"));
		screenWidth = background.getWidth();
		screenHeight = background.getHeight();

		// load the card sprite sheet
		cardSpriteSheet = new Texture(
				Gdx.files.internal("deck-of-playing-cards-all.png"));
		deck = new Deck(cardSpriteSheet);

		// set up a camera, set to background size.
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);

		// Setting up the UI and what a nightmare!
		// start with a skin, and a 1x1 px square
		skin = new Skin();
		Pixmap pixmap = new Pixmap(100, 50, Format.RGBA8888); // sets the button
																// default size

		skin.add("buttonDown", new Texture(Gdx.files.internal("btnDown.png")));
		skin.add("buttonUp", new Texture(Gdx.files.internal("btnUp.png")));
		skin.add("betLess", new Texture(Gdx.files.internal("btnLess.png")));
		skin.add("betMore", new Texture(Gdx.files.internal("btnMore.png")));
		skin.add("chip1", new Texture(Gdx.files.internal("chip1.png")));
		skin.add("chip5", new Texture(Gdx.files.internal("chip5.png")));
		skin.add("chip25", new Texture(Gdx.files.internal("chip25.png")));
		skin.add("chip100", new Texture(Gdx.files.internal("chip100.png")));
		skin.add("chip500", new Texture(Gdx.files.internal("chip500.png")));

		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("textFieldSkin", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		BitmapFont bitmapfont = new BitmapFont();
		skin.add("default", bitmapfont);

		// make the button style
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("buttonUp"); // the way the button
															// will look
		textButtonStyle.down = skin.newDrawable("buttonDown"); // when it's
																// clicked
		textButtonStyle.font = skin.getFont("default");

		// make the increase/decrease bet button style
		TextButtonStyle betLessButtonStyle = new TextButtonStyle();
		betLessButtonStyle.up = skin.newDrawable("betLess");
		TextButtonStyle betMoreButtonStyle = new TextButtonStyle();
		betMoreButtonStyle.up = skin.newDrawable("betMore");
		betLessButtonStyle.font = skin.getFont("default");
		betMoreButtonStyle.font = skin.getFont("default");

		// make the label style
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = new BitmapFont();
		LabelStyle chipLabelStyle = new LabelStyle();
		chipLabelStyle.font = new BitmapFont();
		
		// make textFieldStyle
		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.background = skin.newDrawable("textFieldSkin",
				Color.WHITE);
		textFieldStyle.fontColor = Color.BLACK;
		textFieldStyle.font = new BitmapFont();

		// making the UI
		// the labels (hand totals and cash)
		playerScore = new Label(sPlayerScore, labelStyle);
		playerScore.setPosition(515, 25);
		dealerScore = new Label(sDealerScore, labelStyle);
		dealerScore.setPosition(515, 275);
		playerCash = new Label(sPlayerCash + playerBalance, labelStyle);
		playerCash.setPosition(250, 75);
		playerBet = new Label(sPlayerBet + betAmount, labelStyle);
		playerBet.setPosition(25, 77);
		result = new Label(" ", labelStyle);
		result.setPosition(500, 410);

		userText = new Label("Username", labelStyle);
		userText.setPosition(520, 130);

		// the chip sprites/icons and labels
		chip1 = new Label("1", labelStyle);
		chip1.setPosition(83, 135);
		chip5 = new Label("5", labelStyle);
		chip5.setPosition(83, 200);
		chip25 = new Label("25", labelStyle);
		chip25.setPosition(78, 265);
		chip100 = new Label("100", labelStyle);
		chip100.setPosition(74, 330);
		chip500 = new Label("500", labelStyle);
		chip500.setPosition(74, 395);

		Image chip1Img = new Image(skin.newDrawable("chip1"));
		chip1Img.setPosition(62, 122);
		Image chip5Img = new Image(skin.newDrawable("chip5"));
		chip5Img.setPosition(62, 187);
		Image chip25Img = new Image(skin.newDrawable("chip25"));
		chip25Img.setPosition(62, 252);
		Image chip100Img = new Image(skin.newDrawable("chip100"));
		chip100Img.setPosition(62, 317);
		Image chip500Img = new Image(skin.newDrawable("chip500"));
		chip500Img.setPosition(62, 382);

		// add Buttons to map
		buttonMap.put("hitButton", new Runnable() {
			public void run() {
				hit();
			}
		});
		buttonMap.put("dealButton", new Runnable() {
			public void run() {
				if(betAmount > 0)
				deal();
			}
		});
		buttonMap.put("standButton", new Runnable() {
			public void run() {
				stand();
			}
		});
		
		buttonMap.put("contMode", new Runnable() {
			public void run() {
				difficulty(0);
			}
		});

		buttonMap.put("easyMode", new Runnable() {
			public void run() {
				difficulty(1);
			}
		});
		
		buttonMap.put("mediumMode", new Runnable() {
			public void run() {
				difficulty(2);
			}
		});
		buttonMap.put("hardMode", new Runnable() {
			public void run() {
				difficulty(3);
			}
		});
		buttonMap.put("exit", new Runnable() {
			public void run() {
				gameOver(1);
			}
		});
		buttonMap.put("playAgain", new Runnable() {
			public void run() {
				gameOver(0);
			}
		});

		// the increase/decrease bet buttons
		buttonMap.put("betLess1", new Runnable() {
			public void run() {
				changeBet(-1);
			}
		});
		buttonMap.put("betMore1", new Runnable() {
			public void run() {
				changeBet(1);
			}
		});
		buttonMap.put("betLess5", new Runnable() {
			public void run() {
				changeBet(-5);
			}
		});
		buttonMap.put("betMore5", new Runnable() {
			public void run() {
				changeBet(5);
			}
		});
		buttonMap.put("betLess25", new Runnable() {
			public void run() {
				changeBet(-25);
			}
		});
		buttonMap.put("betMore25", new Runnable() {
			public void run() {
				changeBet(25);
			}
		});
		buttonMap.put("betLess100", new Runnable() {
			public void run() {
				changeBet(-100);
			}
		});
		buttonMap.put("betMore100", new Runnable() {
			public void run() {
				changeBet(100);
			}
		});
		buttonMap.put("betLess500", new Runnable() {
			public void run() {
				changeBet(-500);
			}
		});
		buttonMap.put("betMore500", new Runnable() {
			public void run() {
				changeBet(500);
			}
		});

		// make buttons
		final TextButton standButton = getButton("Stand", 240, 20,
				"standButton", textButtonStyle);
		final TextButton hitButton = getButton("Hit", 130, 20, "hitButton",
				textButtonStyle);
		final TextButton dealButton = getButton("Deal", 20, 20, "dealButton",
				textButtonStyle);

		final TextButton contMode = getButton("Continue", 500, 20, "contMode",
				textButtonStyle);
		final TextButton easyMode = getButton("Easy", 20, 20, "easyMode",
				textButtonStyle);
		final TextButton mediumMode = getButton("Normal", 130, 20,
				"mediumMode", textButtonStyle);
		final TextButton hardMode = getButton("Hard", 240, 20, "hardMode",
				textButtonStyle);
		final TextButton exit = getButton("Exit", 976, 20, "exit",
				textButtonStyle);
		final TextButton playAgain = getButton("Play Again", 20, 20,
				"playAgain", textButtonStyle);

	
		// increase/decrease bet buttons
		final TextButton betLess1 = getButton(" ", 20, 130, "betLess1",
				betLessButtonStyle);
		final TextButton betMore1 = getButton(" ", 120, 130, "betMore1",
				betMoreButtonStyle);
		final TextButton betLess5 = getButton(" ", 20, 195, "betLess5",
				betLessButtonStyle);
		final TextButton betMore5 = getButton(" ", 120, 195, "betMore5",
				betMoreButtonStyle);
		final TextButton betLess25 = getButton(" ", 20, 260, "betLess25",
				betLessButtonStyle);
		final TextButton betMore25 = getButton(" ", 120, 260, "betMore25",
				betMoreButtonStyle);
		final TextButton betLess100 = getButton(" ", 20, 325, "betLess100",
				betLessButtonStyle);
		final TextButton betMore100 = getButton(" ", 120, 325, "betMore100",
				betMoreButtonStyle);
		final TextButton betLess500 = getButton(" ", 20, 390, "betLess500",
				betLessButtonStyle);
		final TextButton betMore500 = getButton(" ", 120, 390, "betMore500",
				betMoreButtonStyle);

		// the Username entry box
		userField = new TextField(username, textFieldStyle);
		userField.setTextFieldListener(new TextFieldListener() {

			  public void keyTyped(TextField textField, char c)  {
				  username = userField.getText();
			}

			});
		userField.setPosition(500, 100);
		userField.setSize(100, 25);
		userField.setAlignment(1);

		// add the buttons to the stage
		stage.addActor(dealButton);
		stage.addActor(hitButton);
		stage.addActor(standButton);
		titleStage.addActor(contMode);
		titleStage.addActor(easyMode);
		titleStage.addActor(mediumMode);
		titleStage.addActor(hardMode);
		titleStage.addActor(userField);
		gameOverStage.addActor(exit);
		gameOverStage.addActor(playAgain);

		stage.addActor(betLess1);
		stage.addActor(betMore1);
		stage.addActor(betLess5);
		stage.addActor(betMore5);
		stage.addActor(betLess25);
		stage.addActor(betMore25);
		stage.addActor(betLess100);
		stage.addActor(betMore100);
		stage.addActor(betLess500);
		stage.addActor(betMore500);

		// add the labels to the stage
		stage.addActor(playerScore);
		stage.addActor(dealerScore);
		stage.addActor(playerBet);
		stage.addActor(playerCash);
		stage.addActor(result);
		titleStage.addActor(userText);

		// add the chip sprites to the stage
		stage.addActor(chip1Img);
		stage.addActor(chip5Img);
		stage.addActor(chip25Img);
		stage.addActor(chip100Img);
		stage.addActor(chip500Img);

		// now add the chip labels
		stage.addActor(chip1);
		stage.addActor(chip5);
		stage.addActor(chip25);
		stage.addActor(chip100);
		stage.addActor(chip500);

	}

	@Override
	public void render() {
		if (playerBalance == 0 && betAmount == 0) {
			testGameOver();
		}
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		if (titleScreen)
			batch.draw(titleBackground, 0, 0);
		else if (gameOverScreen) {
			batch.draw(gameOverBackground, 0, 0);
		} else {
			batch.draw(background, 0, 0);
			deck.draw(batch);
		}

		batch.end();

		if (titleScreen) {
			Gdx.input.setInputProcessor(titleStage); // this NEEDS to be here so
														// stage can have events
			titleStage.draw();
		} else if (gameOverScreen) {
			Gdx.input.setInputProcessor(gameOverStage); // this NEEDS to be here
														// so stage can have
														// events
			gameOverStage.draw();
		} else {
			Gdx.input.setInputProcessor(stage); // this NEEDS to be here so
												// stage can have events
			stage.draw();
		}

	}
	
	private TextButton getButton(String buttonText, int xPosition,
			int yPosition, final String id, TextButtonStyle textButtonStyle) {
		TextButton button = new TextButton(buttonText, textButtonStyle);
		button.setPosition(xPosition, yPosition);

		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				buttonMap.get(id).run();
			}
		});
		return button;
	}

	private void difficulty(int diff) {
		try {
			createUser();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Failed to create user.");
		}
		
		if (diff == 0){
			try {
				getBalance();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failed to receive balance.");
			}
		}
		else if (diff == 1)
			playerBalance = 5000;
		else if (diff == 2)
			playerBalance = 2500;
		else
			playerBalance = 500;

		try {
			saveBalance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to save balance");
		}
		
		playerCash.setText(sPlayerCash + playerBalance);
		titleScreen = false;

	}

	private void gameOver(int choice) {
		if (choice == 1) { // if exit is clicked then the application closes
			System.exit(0);
		}
		gameOverScreen = false; // otherwise the game over screen is removed
		titleScreen = true; // and replaced with the title screen
		deck.resetShuffling();
	}

	private void deal() {
		deck.cancelShuffle();
		if (!playing) {
			playing = true;
			deck.holeCard.setCover(true); // the hole card will cover up the dealer's second card
			deck.deal();
			updateScores();
			result.setText(" ");
		}
	}

	private void hit() {
		deck.cancelShuffle();
		if (playing) {
			deck.hit(0);
			updateScores();
		}
	}

	private void stand() {
		deck.cancelShuffle();
		// dealer will draw to 16, will stand on all 17's. updateScores() also
		// evaluates bust conditions.
		if (playing) {
			deck.holeCard.setCover(false); // Dealer can now reveal the hole
											// card
			while (dealerTotal < 17) {
				deck.hit(1);
				updateScores();
			}

			// if nobody bust. busted? bustered...
			// then playing == true. now we evaluate who won based on card values.
			if (playing) {
				updateScores(); // necessary to reveal the dealer's total if the hole card put his value over 17
				if (dealerTotal > playerTotal) {
					result.setText("DEALER WINS! - $" + betAmount);
				} else if (dealerTotal < playerTotal) {
					result.setText("YOU WIN! + $" + betAmount*2);
					playerBalance += betAmount*2;
				} else if (dealerTotal == playerTotal) {
					playerBalance += betAmount; // (CALEB) Added to correctly refund a tie
					result.setText("PUSH! + $0");
				}
			}
			playerCash.setText(sPlayerCash + playerBalance);
			betAmount = 0;
			playerBet.setText(sPlayerBet + betAmount);
			playing = false;
			// check to see if the player has any money left to play with
			if (playerBalance == 0 && betAmount == 0) {
				testGameOver();
			}
		}

	}

	private void updateScores() {
		playerTotal = 0;
		dealerTotal = 0;
		// display the player's hand and evaluate win/loss conditions
		playerTotal = handValue(deck.playerHand);
		playerScore.setText(sPlayerScore + playerTotal);
		if (playerTotal > 21) {
			result.setText("PLAYER BUST! - $" + betAmount);
			playing = false;
			deck.holeCard.setCover(false);
			playerCash.setText(sPlayerCash + playerBalance);
			betAmount = 0;
			playerBet.setText(sPlayerBet + betAmount);
		}
		// check to see if the player has any money left to play with
		if (playerBalance == 0 && betAmount == 0) {
			testGameOver();

		}

		// display the dealer's hand and win/loss conditions
		dealerTotal = handValue(deck.dealerHand);
		dealerScore.setText(sDealerScore + dealerTotal);
		if (dealerTotal > 21) {
			result.setText("DEALER BUST! + $" + betAmount*2);
			playing = false;
			playerBalance += betAmount*2;
			playerCash.setText(sPlayerCash + playerBalance);
			//betField.setText("0");
			betAmount = 0;
			playerBet.setText(sPlayerBet + betAmount);
		}

		// will only display the value of the faced up card (the one in
		// position 0) as long as holeCard is true (visible)
		if (deck.holeCard.isCover()) {
			dealerScore.setText(sDealerScore
					+ deck.dealerHand.get(0).getValue());
		}
		
		try {
			saveBalance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to save balance");
		}

	} // end updateScores()
	
	// (CALEB) Used to evaluate the total value of the hand, handles aces with no further code
	private static int handValue(List<Card> hand){
	      int total = 0;
	      int numOfAces = 0;
	      for(Card c: hand){
	         if(c.getValue() == 11){
	            numOfAces++;
	         }
	         total += c.getValue();;
	      }
	      
	      while(total > 21 && numOfAces > 0){
	         numOfAces--;
	         total -= 10;
	      }
	      
	      return total;
	   }
	
	// used for the bet increase/decrease buttons
	private void changeBet(int amount) {
		if (!playing) {
			if (amount > 0 && (playerBalance - amount) >= 0) {
				betAmount += amount; // Positive
				playerBalance -= amount;
			} else if (amount < 0 && betAmount >= Math.abs(amount)) {
				betAmount += amount; // Negative
				playerBalance -= amount;
			}
			
			playerBet.setText(sPlayerBet + betAmount);
			playerCash.setText(sPlayerCash + playerBalance);
		}
	} // end changeBet()
	
	
	private void testGameOver(){
		if(elapsedTime > 3f){
			gameOverScreen = true;
			deck = new Deck(cardSpriteSheet);	
			elapsedTime = 0;
			playerBalance = 1;
			betAmount = 0;
			playerBet.setText(sPlayerBet + betAmount);
			playerCash.setText(sPlayerCash + playerBalance);
			playerScore.setText(sPlayerScore);
			dealerScore.setText(sDealerScore);
			result.setText("");
		} else
			elapsedTime += Gdx.graphics.getDeltaTime();
	}
	
	private void createUser() throws IOException{
		URL u = new URL("http://icarus.cs.weber.edu/~ll02508/forms/" + 
				"blackjackBalance.php?option=create&username=" + username);
		URLConnection c = u.openConnection();
		InputStream r = c.getInputStream();
	}
	
	private void getBalance() throws IOException{
		URL u = new URL("http://icarus.cs.weber.edu/~ll02508/forms/" + 
				"blackjackBalance.php?option=getBalance&username=" + username);
		URLConnection c = u.openConnection();
		InputStream r = c.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(r));
		String read = "";
		for(String line; ( line = reader.readLine() ) != null;)
			read += line;
		playerBalance = Integer.parseInt(read);
	}
	
	private void saveBalance() throws IOException {
		URL u = new URL("http://icarus.cs.weber.edu/~ll02508/forms/blackjack" + 
				"Balance.php?option=updateBalance&username="
				+ username + "&balance=" + playerBalance);
		URLConnection c = u.openConnection();
		InputStream r = c.getInputStream();

	}
	
}
