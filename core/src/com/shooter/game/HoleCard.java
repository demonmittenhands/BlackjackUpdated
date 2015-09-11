package com.shooter.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HoleCard {

	private Sprite cardBack;
	boolean cover = false; //cover up the dealer's second card? 

	public HoleCard(Sprite cardBack) {
		this.cardBack = cardBack;
		this.cardBack.setPosition(520, 300); //will cover up the dealer's second card
	}

	public void draw(SpriteBatch batch) {
		cardBack.draw(batch);
		
	}
	
	public boolean isCover(){
		return cover;
	}
	
	public void setCover(boolean cover){
		this.cover = cover;
	}

}
