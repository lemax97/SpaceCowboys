package com.mygdx.game;

import com.mygdx.game.Screens.GameScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.Screens.MainMenuScreen;

public class Core extends ApplicationAdapter {

    public static final float VIRTUAL_WIDTH = 960;
    public static final float VIRTUAL_HEIGHT = 540;

    Screen screen;

    public void create() {
        super.create();
//        Gdx.input.setCatchBackKey(true);
        new Assets();
        new Settings().load();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
        Gdx.gl.glClearColor(0, 0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        screen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);
        screen.resize(width, height);
    }

    public void setScreen(Screen screen) {

        if (this.screen != null) {

            this.screen.hide();
            this.screen.dispose();
        }
        this.screen = screen;
        if (this.screen != null) {

            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    @Override
    public void dispose() {
        Assets.dispose();
        Settings.save();
    }
}
