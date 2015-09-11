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
	
	public Deck(Texture cardSheet) {
		TextureRegion[][] tmp = TextureRegion.split(cardSheet, cardSheet.getWidth()/13, cardSheet.getHeight()/4);
		
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 13; j++){
				Sprite sprite = new Sprite(tmp[i][j], 2, 2, cardSheet.getWidth()/13-2, cardSheet.getHeight()/4-2);
				Card card = new Card(i, j, sprite);
				cards.add(card);
			}
		}
		Shuffle();
	}
	
	public void Shuffle(){
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
	
	
		
	public void Deal(){
		// run Hit four times.
		// need to shuffle if less than 11 cards		
		playerHand.clear();
		dealerHand.clear();
		
		if (cardIndex >=42){
			Shuffle();
		}
		Hit(0); // Player = 0
		Hit(0); 
		Hit(1); // Dealer = 1
		Hit(1); 
	}

	public void Hit(int player) {
		// put the card out, based on player
		// update total
		int yPos;
		int xPos;
		Card c = cards.get(cardIndex);
		
		// put the card with the rest of the player's hand
		if (player == 0){
			yPos = 50;
			xPos = 500 + 20*(playerHand.size());
		} else {
			yPos = 300;
			xPos = 500 + 20*(dealerHand.size());
		}
		
		
		c.SetPosition(xPos, yPos); // put the card where it should be
		if (player == 0){
			playerHand.add(c);
		} else {
			dealerHand.add(c);
		}
		cardIndex++;
	}

	public void draw(SpriteBatch batch) {
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
	
	public void Stand(){

	}
}

