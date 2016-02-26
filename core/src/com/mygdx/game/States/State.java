package com.mygdx.game.States;

/**
 * Created by Dell on 18-02-2016.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Brent on 6/25/2015.
 */
public abstract class State {
    protected GameStateManager gsm;
    protected OrthographicCamera cam;
    protected Vector3 mouse;

    protected State(GameStateManager gsm){
        this.gsm = gsm;


        this.cam = new OrthographicCamera(((float) Gdx.graphics.getWidth()/Gdx.graphics.getHeight()) *800, 1280);

    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
}