package com.mygdx.game.old;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Core2 extends ApplicationAdapter {

    public static final float VIRTUAL_WIDTH = 960;
    public static final float VIRTUAL_HEIGHT = 540;

    Screen screen;

    public PerspectiveCamera cam;
    public Model model;
    public ModelInstance instance;
    public ModelBatch modelBatch;
    public Environment environment;



    public void create() {



        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        model = modelBuilder.createBox(5, 5, 5, mat,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    Vector3 position = new Vector3();
    private void movement(){

        instance.transform.getTranslation(position);
        if (Gdx.input.isKeyPressed(Input.Keys.W)){

            position.x += Gdx.graphics.getDeltaTime() * 10;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){

            position.z += Gdx.graphics.getDeltaTime() * 10;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){

            position.z -= Gdx.graphics.getDeltaTime() * 10;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){

            position.x -= Gdx.graphics.getDeltaTime() * 10;
        }
//        instance.transform.setTranslation(position);
    }

    float rotation;
    private void rotate(){

        rotation = (rotation + Gdx.graphics.getDeltaTime() * 100) % 360;
//        instance.transform.setFromEulerAngles(0, 0, rotation).trn(position.x, position.y, position.z);
//        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
//            instance.transform.rotate(Vector3.X, Gdx.graphics.getDeltaTime() * 100);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
//            instance.transform.rotate(Vector3.Y, Gdx.graphics.getDeltaTime() * 100);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
//            instance.transform.rotate(Vector3.Z, Gdx.graphics.getDeltaTime() * 100);
//        }
    }

    private void updateTransformation() {

        instance.transform.setFromEulerAngles(0, 0, rotation).trn(position.x, position.y,
                position.z).scale(scale, scale, scale);
    }

    boolean increment = true;
    float scale = 1;
    void scale(){

        if (increment) {

            scale += Gdx.graphics.getDeltaTime()/5;
            if (scale >= 1.5f)
                increment = false;
            }
            else {
                scale -= Gdx.graphics.getDeltaTime()/5;
                if (scale <= 0.5f)
                    increment = true;
            }
    }


    @Override
    public void render() {
        super.render();
        movement();
        rotate();
        scale();
        updateTransformation();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        modelBatch.end();
    }



    @Override
    public void dispose() {
        super.dispose();
        model.dispose();
    }
}
