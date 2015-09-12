package com.shooter.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
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
	private float angle = 0f;
	private int directionX = 1;
	private int directionY = 1;
	private float boundX = 400;
	private float boundY = 100;
	
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
	
	public float getX(){
		return sprite.getX();
	}
	
	public float getY(){
		return sprite.getY();
	}
	
	public void scatterCards(){
		float deltatime = Gdx.graphics.getDeltaTime();
		Random rand = new Random();
		if(rand.nextInt(100) == 1)
			directionX *= -1;
		if(rand.nextInt(100) == 1)
			directionY *= -1;
		if (value%2 == 0)
			angle = -10f;
		else
			angle = 10f;
		sprite.rotate(angle * deltatime * 30f);
		if(angle < 0)
			angle += deltatime * 100;
		if(angle > 0)
			angle -= deltatime * 100;
		
		
		sprite.setPosition(getX() + 200*deltatime*directionX , getY() + 100*deltatime*directionY);
		
		if(sprite.getX() > Gdx.graphics.getWidth() - boundX - (sprite.getWidth()/2))
			sprite.setX(Gdx.graphics.getWidth() - boundX - (sprite.getWidth()/2));
		if(sprite.getX() < boundX - (sprite.getWidth()/2))
			sprite.setX(boundX - (sprite.getWidth()/2));
		if(sprite.getY() > Gdx.graphics.getHeight() - boundY)
			sprite.setY(Gdx.graphics.getHeight() - boundY);
		if(sprite.getY() < boundY)
			sprite.setY(boundY);
		
		boundX += deltatime * 60;
		boundY += deltatime * 20;

		
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
	
	public void resetRot(){
		sprite.setRotation(0);
	}
}
