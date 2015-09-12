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
	private float boundX = 200;
	private float boundY = 50;
	
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
	
	public void scatterCard(){
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
		
		
		sprite.setPosition(getX() + (200*deltatime*directionX) , getY() + (100*deltatime*directionY));
		
		float centerX = (Gdx.graphics.getWidth()/2) - (sprite.getWidth()/2);
		float centerY = (Gdx.graphics.getHeight()/2) - (sprite.getHeight()/2);
		
		if(sprite.getX() > Gdx.graphics.getWidth() - boundX - sprite.getWidth())
			sprite.setX(Gdx.graphics.getWidth() - boundX - sprite.getWidth());
		if(sprite.getX() < boundX)
			sprite.setX(boundX);
		if(sprite.getY() > Gdx.graphics.getHeight() - boundY - sprite.getHeight())
			sprite.setY(Gdx.graphics.getHeight() - boundY - sprite.getHeight());
		if(sprite.getY() < boundY)
			sprite.setY(boundY);
		
		if(boundX < centerX)
			boundX += deltatime * ((Gdx.graphics.getWidth()-400)/14);
		else
			resetRot();
		if (boundY < centerY)
			boundY += deltatime * ((Gdx.graphics.getHeight()-100)/14);
		else
			resetRot();

		
	}
	
	public void fanCard(int index, float elapsedTime){
		float radius = 600f;
		double movement = elapsedTime * 20f;
		double angle = -26 + movement;
		if (angle > index - 26)
			angle = index - 26;
		sprite.setRotation((float) angle * -1);
		angle = angle * Math.PI / 180;
		float centerX = Gdx.graphics.getWidth()/2;
		float centerY = (Gdx.graphics.getHeight()/2) + 100;
		float xPos = (float) (centerX + (radius * Math.sin(angle)));
		float yPos = (float) (centerY - (radius * (1-Math.cos(angle))));
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
	
	public void resetRot(){
		sprite.setRotation(0);
	}
	
	public void resetScatterCardValues(){
		angle = 0f;
		directionX = 1;
		directionY = 1;
		boundX = 200;
		boundY = 50;
	}
}
