package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Assets;
import com.mygdx.game.Core;

public class MainMenuScreen implements Screen {

    Core game;
    Stage stage;
    Image backgroundImage, titleImage;
    TextButton playButton, leaderboardButton, quitButton;

    public MainMenuScreen(Core game) {
        this.game = game;
        stage = new Stage(new FitViewport(Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT));
        setWidgets();
        configureWidgets();
        setListeners();
        Gdx.input.setInputProcessor(stage);
    }

    private void setWidgets() {
        backgroundImage     = new Image(new Texture(Gdx.files.internal("data/backgroundMN.png")));
        titleImage          = new Image(new Texture(Gdx.files.internal("data/title.png")));
        playButton          = new TextButton("Play", Assets.skin);
        leaderboardButton   = new TextButton("Leaderboards", Assets.skin);
        quitButton          = new TextButton("Quit", Assets.skin);
    }

    private void configureWidgets() {
        backgroundImage.setSize(Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        titleImage.setSize(620, 200);
        titleImage.setPosition(Core.VIRTUAL_WIDTH / 2 - titleImage.getWidth() / 2,
                Core.VIRTUAL_HEIGHT / 2);
        playButton.setSize(128, 64);
        playButton.setPosition(Core.VIRTUAL_WIDTH / 2 - playButton.getWidth() / 2,
                Core.VIRTUAL_HEIGHT / 2 - 100);
        leaderboardButton.setSize(128, 64);
        leaderboardButton.setPosition(Core.VIRTUAL_WIDTH / 2 - leaderboardButton.getWidth() / 2,
                Core.VIRTUAL_HEIGHT / 2 - 170);
        quitButton.setSize(128, 64);
        quitButton.setPosition(Core.VIRTUAL_WIDTH / 2 - quitButton.getWidth() / 2,
                Core.VIRTUAL_HEIGHT / 2 - 240);
        stage.addActor(backgroundImage);
        stage.addActor(titleImage);
        stage.addActor(playButton);
        stage.addActor(leaderboardButton);
        stage.addActor(quitButton);
    }

    private void setListeners() {
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        leaderboardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LeaderboardsScreen(game));
            }
        });

        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }



    @Override
    public void render(float delta) {
        /** Updates **/
        stage.act(delta);
        /** Draw **/
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
