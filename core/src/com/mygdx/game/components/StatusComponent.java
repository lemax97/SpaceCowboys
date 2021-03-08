package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.managers.EnemyAnimations;

public class StatusComponent implements Component {
    private AnimationComponent animationComponent;
    public boolean alive, running, attacking;
    public float aliveStateTime;

    public StatusComponent(AnimationComponent animationComponent){
        this.animationComponent = animationComponent;
        alive = true;
        running = true;
    }

    public void update(float delta) {
        //We'll now update the status to gather the amount of time dead, until we make it disappear
        // and spawn another enemy.
        if (!alive) aliveStateTime += delta;
    }

    public void setAlive(boolean alive){
        this.alive = alive;
        playDeathAnim2();
    }

    private void playDeathAnim2() {
        animationComponent.animate(EnemyAnimations.id, EnemyAnimations.offsetDeath2, EnemyAnimations.durationDeath2, 1, 3);
    }


}
