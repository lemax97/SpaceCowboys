package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.mygdx.game.Assets;

public class DieParticleComponent implements Component {
    public ParticleEffect originalEffect;
    public boolean used = false;

    public DieParticleComponent(ParticleSystem particleSystem) {
        ParticleEffectLoader.ParticleEffectLoadParameter loadParameter =
                new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        if (!Assets.assetManager.isLoaded("data/dieparticle.pfx")){
            Assets.assetManager.load("data/dieparticle.pfx", ParticleEffect.class, loadParameter);
            Assets.assetManager.finishLoading();
        }
        originalEffect = Assets.assetManager.get("data/dieparticle.pfx");
    }
}
