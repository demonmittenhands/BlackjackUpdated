package com.shooter.game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Deck {
	
	List<Card> cards = new ArrayList<Card>();
	List<Card> playerHand = new ArrayList<Card>();
	List<Card> dealerHand = new ArrayList<Card>();
	
	Sprite cardBack = new Sprite(new Texture(Gdx.files.internal("cardBack.png")));
	HoleCard holeCard = new HoleCard(cardBack);
	
	int cardIndex; // which card we're looking at in the deck. top of the deck!
	private boolean shuffling = true;
	private boolean fanCards = true;
	private float elapsedTime = 0f;
	private boolean isShuffled = false;
	
	public Deck(Texture cardSheet) {
		TextureRegion[][] tmp = TextureRegion.split(cardSheet, cardSheet.getWidth()/13, cardSheet.getHeight()/4);
		
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 13; j++){
				Sprite sprite = new Sprite(tmp[i][j], 2, 2, cardSheet.getWidth()/13-2, cardSheet.getHeight()/4-2);
				Card card = new Card(i, j, sprite);
				cards.add(card);
			}
		}
	}
	
	public void shuffle(){
		Collections.shuffle(cards);
		cardIndex = 0;
	}
	
	public void listCards(){
		for(Card c : cards){
			System.out.println(c.toString());
		}
	}
	
	public void resetAceValue(){
		for(Card c : cards){
			if(c.getRank() == "ACE"){
				c.setValue(11);
			}
		}
	}
	
	public void deal(){
		// run hit four times.
		// need to shuffle if less than 11 cards		
		playerHand.clear();
		dealerHand.clear();
		
		if (cardIndex >=42){
			shuffle();
		}
		
		hit(0); // Player = 0
		hit(0); 
		hit(1); // Dealer = 1
		hit(1); 
	}

	public void hit(int player) {
		// put the card out, based on player
		// update total
		int yPos;
		int xPos;
		Card card = cards.get(cardIndex);
		
		// put the card with the rest of the player's hand
		if (player == 0){
			yPos = 50;
			xPos = 500 + 20*(playerHand.size());
		} else {
			yPos = 300;
			xPos = 500 + 20*(dealerHand.size());
		}
		
		
		card.SetPosition(xPos, yPos); // put the card where it should be
		if (player == 0){
			playerHand.add(card);
		} else {
			dealerHand.add(card);
		}
		cardIndex++;
	}

	public void draw(SpriteBatch batch) {
		if(shuffling){
			animateShuffle(batch);
		} else {
			if(!isShuffled){
				shuffle();
				isShuffled = true;
				cardBack.setPosition(520, 300);
			}
			// call the card method in each card that's been dealt
			for(Card card : dealerHand){
				card.draw(batch);
			}
			if (holeCard.isCover() == true){
				holeCard.draw(batch);
			}
			for(Card card : playerHand){
				card.draw(batch);
			}
		}
	}
	
	public void animateShuffle(SpriteBatch batch){
		if(fanCards){
			for(int i = 0; i < cards.size(); i++){
				cards.get(i).fanCard(i, elapsedTime);
				cards.get(i).draw(batch);
			}
			elapsedTime += Gdx.graphics.getDeltaTime();
			if(elapsedTime > 5){
				fanCards = false;
				elapsedTime = 0;
			}
		} else {
			cardBack.setPosition(Gdx.graphics.getWidth()/2 - cardBack.getWidth()/2, Gdx.graphics.getHeight()/2 - cardBack.getHeight()/2);
			if(elapsedTime < 5)
				cardBack.draw(batch);
			for(Card card : cards){
				card.scatterCard();
				card.draw(batch);
			}
			if(elapsedTime > 7){
				shuffling = false;
				resetRotation();
				elapsedTime = 0;
			}
			elapsedTime += Gdx.graphics.getDeltaTime();
			
			if(elapsedTime >= 5)
				cardBack.draw(batch);
		}		
	}
	
	public void cancelShuffle(){
		shuffling = false;
		resetRotation();
		elapsedTime = 0f;
		for(Card card : cards){
			card.resetScatterCardValues();
		}
	}
	
	public void resetRotation(){
		for(Card card : cards){
			card.resetRot();
		}
	}
	
	public void resetShuffling(){
		shuffling = true;
		fanCards = true;
		resetRotation();
		elapsedTime = 0f;
		isShuffled = false;
		for(Card card : cards){
			card.resetScatterCardValues();
		}
	}
	
	public void stand(){

	}

}

