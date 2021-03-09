package com.mygdx.game.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.Assets;
import com.mygdx.game.components.PlayerComponent;

public class ScoreWidget extends Actor {
    private Label label;
    private Container container;
    private int score;


    public ScoreWidget() {
        label = new Label("", Assets.skin);
        score = 0;
        // in order to use a `Label` class with `Actions`, we will first need to add it to a
        // `Container` class (even if the `Label` class lets you add `Actions`, they won't work on
        // this version of LibGDX, at least.
        container = new Container(label);
        container.setTransform(true);
        container.fill();
    }

    @Override
    public void act(float delta) {
        container.act(delta);
        if (PlayerComponent.score > score){
            container.addAction(new SequenceAction(Actions.scaleBy(0.5f, 0.25f, 0.3f),
                    Actions.scaleBy(-0.5f, -0.25f, 0.3f)));
        }
//        label.act(delta);
//        label.setText("Score : " + PlayerComponent.score);
        score = PlayerComponent.score;
        label.setText("Score: " + PlayerComponent.score);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        label.draw(batch, parentAlpha);
        container.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
//        label.setPosition(x, y);
        container.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
//        label.setSize(width, height);
        container.setSize(width, height);
    }
}
