package com.shooter.game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Deck {
	
	List<Card> cards = new ArrayList<Card>();
	List<Card> phand = new ArrayList<Card>();
	List<Card> ahand = new ArrayList<Card>();
	
	int cardIndex; // which card we're looking at in the deck. top of the deck!
	
	public Deck(Texture cardSheet) {
		TextureRegion[][] tmp = TextureRegion.split(cardSheet, cardSheet.getWidth()/13, cardSheet.getHeight()/4);
		
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 13; j++){
				Sprite s = new Sprite(tmp[i][j], 2, 2, cardSheet.getWidth()/13-2, cardSheet.getHeight()/4-2);
				Card c = new Card(i, j, s);
				cards.add(c);
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
		
	public void Deal(){
		// run Hit three times.
		// need to shuffle if less than 11 cards		
		phand.clear();
		ahand.clear();
		
		if (cardIndex >=42){
			Shuffle();
		}
		Hit(0);
		Hit(0);
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
			xPos = 500 + 20*(phand.size());
		} else {
			yPos = 300;
			xPos = 500 + 20*(ahand.size());
		}
		
		
		c.SetPosition(xPos, yPos); // put the card where it should be
		if (player == 0){
			phand.add(c);
		} else {
			ahand.add(c);
		}
		cardIndex++;
	}

	public void draw(SpriteBatch batch) {
		// call the card method in each card that's been dealt
		for(Card c : ahand){
			c.draw(batch);
		}
		for(Card c : phand){
			c.draw(batch);
		}
		
	}
	
	public void Stand(){

	}


}

