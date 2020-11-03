package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class StatusComponent implements Component {
    public boolean alive;
    public StatusComponent(){
        alive = true;
    }
}
