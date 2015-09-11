package com.shooter.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Card {
	public enum Rank {ACE, DEUCE, THREE, FOUR, FIVE,
		SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN,
		KING
	}

	public enum Suit { CLUB, SPADE, HEART, DIAMOND } 
	
	private Sprite sprite;
	private Rank rank;
	private Suit suit;
	private int value;
	
	public Card(int suit, int rank, Sprite s) {
		this.sprite = s;
		this.rank = Rank.values()[rank];
		this.suit = Suit.values()[suit];
		if (rank == 0) //based on the location in the enum, 'ACE' is in the zeroth index
			this.value = 11;
		else if (rank < 9 && rank != 0)
			this.value = rank + 1;
		else
			this.value = 10;
	}
	
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);	
	}
	
	public void SetPosition(int xPos, int yPos) {
		sprite.setPosition(xPos, yPos);
	}
	
	public int getValue(){
		return value;
	}
	
	public void setValue(int value){
		this.value = value;
	}
	
	public String getRank(){
		return rank.toString();
	}
}
