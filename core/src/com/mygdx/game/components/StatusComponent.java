package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class StatusComponent implements Component {
    public boolean alive;
    public float aliveStateTime;

    public StatusComponent(){
        alive = true;
    }

    public void update(float delta) {
        //We'll now update the status to gather the amount of time dead, until we make it disappear
        // and spawn another enemy.
        if (!alive) aliveStateTime += delta;
    }
}
