package com.cgossip.psyphysics.states;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cgossip.psyphysics.actors.MyActor;
import com.cgossip.psyphysics.handlers.GameStateManager;
import com.cgossip.psyphysics.view.Button;

/**
 * Created by Dell on 24-03-2016.
 */
public class MainMenu extends GameState implements InputProcessor,ApplicationListener {
    /*
            imageProvider = game.getImageProvider();
            imageProvider.load();
            backgroundImage = imageProvider.getBackgroundSpring();
            TextureRegion buttonBg = imageProvider.getButton();
            buttons = new Button [3];
            buttons[0] = new Button(buttonBg, imageProvider.getStart());
            buttons[1] = new Button(buttonBg, imageProvider.getKids());
            buttons[2] = new Button(buttonBg, imageProvider.getScores());
            helpButton = new Button(imageProvider.getHelp());

            soundButtons = new Button[2];
            soundButtons[0] = new Button(imageProvider.getSoundImage(false));
            soundButtons[1] = new Button(imageProvider.getSoundImage(true));

            camera = new OrthographicCamera();
            camera.setToOrtho(false, imageProvider.getScreenWidth(), imageProvider.getScreenHeight());
            batch = new SpriteBatch();

            logo = imageProvider.getLogo();
            logoX = (imageProvider.getScreenWidth() - logo.getRegionWidth())/2;
            logoY = (imageProvider.getScreenHeight() - logo.getRegionHeight() - 10)-50;

            int buttonMargin = 15;
            int buttonsHeight = 3*buttonMargin;
            for(int i=0; i<buttons.length; i++) {
                buttonsHeight += buttons[i].getRegionHeight();
            }

            for(int i=buttons.length-1;i>=0;i--) {
                int x, y;
                x = (imageProvider.getScreenWidth() - buttons[i].getRegionWidth())/2;
                if (i == buttons.length - 1) {
                y = ((imageProvider.getScreenHeight() - buttonsHeight) / 2) - 10;
                }
                else {
                    y = ((int) buttons[i+1].getPosY()) +
                        buttons[i+1].getRegionHeight() + buttonMargin;
                }
                buttons[i].setPos(x, y);
            }

            float x = imageProvider.getScreenWidth() - helpButton.getRegionWidth() - 10;
            float y = 10;
            helpButton.setPos(x, y);

            soundButtons[0].setPos(10, 10);
            soundButtons[1].setPos(10, 10);

            Gdx.input.setInputProcessor(this);
     */
    private Stage stage; //** stage holds the Button **//
    private BitmapFont font;
    private TextureAtlas buttonsAtlas; //** image of buttons **//
    private Skin buttonSkin,textSkin,dialogskin; //** images are used as skins of the button **//
    private TextButton button;
    TextureRegion logo,play,credit,exit;
    Table root;
    float stateTime;
    TextureRegion                   currentFrame;           // #7
    TextureRegion[]                 drawFrames;
    Texture background,wood,dialogback;
    Animation drawAnimation;
    private TextureAtlas textatlas,dialogatlas;
    private Viewport viewport;
    private OrthographicCamera camera;
    public Button buttons[];
    TextureRegion texte;
    public MainMenu(final GameStateManager gsm) {
        super(gsm);
        background =new Texture(Gdx.files.internal("dataa/newbackground.png"));
        dialogback=new Texture(Gdx.files.internal("dataa/dialogback.png"));
        dialogatlas = new TextureAtlas("dataa/dialoga.atlas");
        buttonsAtlas = new TextureAtlas("dataa/button.pack"); //**button atlas image **//
        textatlas = new TextureAtlas("dataa/text.atlas");
        wood=new Texture(Gdx.files.internal("dataa/wood.png"));
        texte=new TextureRegion(dialogatlas.findRegion("exit"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, com.cgossip.psyphysics.main.Game.V_WIDTH , com.cgossip.psyphysics.main.Game.V_HEIGHT );

        root = new Table();
        root.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        dialogskin=new Skin();
        dialogskin.addRegions(dialogatlas);

        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonsAtlas); //** skins for on and off **//

        textSkin= new Skin();
        textSkin.addRegions(textatlas);
        font = new BitmapFont(Gdx.files.internal("data/arial-15.fnt"),false); //** font **//

        stage = new Stage();        //** window is stage **//
        stage.clear();
        Gdx.input.setInputProcessor(stage); //** stage is responsive **//

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(); //** Button properties **//
        style.up = buttonSkin.getDrawable("buttonOff");
        style.down = buttonSkin.getDrawable("buttonOn");

       // TextButton.TextButtonStyle styleexit = new TextButton.TextButtonStyle(); //** Button properties **//
      //  style.up = buttonSkin.getDrawable("buttonOff");
       // style.down = buttonSkin.getDrawable("buttonOn");

        style.font = font;

        button = new TextButton("START", style);
        //** Button text and style **//
        button.setHeight(Gdx.graphics.getHeight() / 3); //** Button Height **//
        button.setWidth(Gdx.graphics.getWidth() / 4); //** Button Width **//

        button.setPosition(Gdx.graphics.getWidth() / 2 - button.getWidth() / 2, Gdx.graphics.getHeight());

        button.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Pressed"); //** Usually used to start Game, etc. **//


                // TODO Auto-generated method stub


                return true;

            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("my app", "Rggggggeleased");

                ///and level
                gsm.setState(gsm.PLAY);

                dispose();

            }
        });



        MoveToAction moveAction = new MoveToAction();//Add dynamic movement effects to button
        moveAction.setPosition(10, 10);
        moveAction.setDuration(.5f);
       // button.addAction(moveAction);
        //stage.addActor(root);
       // root.debug();



        //Image imaget = new Image(textSkin.getDrawable("backgroundtext"));
        logo=new TextureRegion(textatlas.findRegion("backgroundtext"));
        play=new TextureRegion(textatlas.findRegion("play"));
        credit=new TextureRegion(textatlas.findRegion("credit"));
        exit=new TextureRegion(textatlas.findRegion("exit"));
        buttons = new Button [3];
        buttons[0] = new Button(play);

        buttons[0].setPos(525,200);
        buttons[1] = new Button(credit);
        buttons[1].setPos(525,140);
        drawFrames = new TextureRegion[25];
        buttons[2] = new Button(exit);


        buttons[2].setPos(525,80);
        for(int i=1;i<=25;i++){
            drawFrames[i-1]=new TextureRegion(new Texture(Gdx.files.internal("anim/"+i+".png")));
        }
        drawAnimation = new Animation(0.15f, drawFrames);
        // TextureRegionDrawable trd=new TextureRegionDrawable(new TextureRegion(new Texture(textSkin.get("backgroundtext"))));

        // Stack stack = new Stack();

        // stack.add(imaget);
// creating the group
        /*
        WidgetGroup group = new WidgetGroup();


        group.addActor(image);
        group.addActor(imaget);
        root.setBounds(0, 0, Gdx.graphics.getWidth(), 50);
        root.debug().left().top().add(group);
        */
        MoveToAction moveActions = new MoveToAction();
        moveActions.setPosition(10, 350);
        moveActions.setDuration(.2f);

        //image.setScaling(Scaling.fill);
        //root.add(imaget).height(200).fill(400,100);
        MyActor my=new MyActor();

        my.addAction(moveActions);
       // stage.addActor(my);
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
    }



    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();           // #15
        currentFrame = drawAnimation.getKeyFrame(stateTime, true);  // #16
        stage.act();
        sb.begin();

        cam.update();
        sb.setProjectionMatrix(cam.combined);

        sb.draw(background, 0, 0);
        sb.draw(wood, 400, 0);
        buttons[0].draw(sb);
        buttons[1].draw(sb);
        buttons[2].draw(sb);
        sb.draw(currentFrame, 50, 50, 500,200);
        sb.draw(logo, 10,350);
        if(backf==1){
            sb.draw(dialogback, 0,0);
            sb.draw(texte,450,110);
           // texte.
        }
        //stage.draw();
        sb.end();

        sb.begin();
        stage.draw();
        sb.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void rendersb(SpriteBatch sb) {

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        background.dispose();
        wood.dispose();

    }

    @Override
    public void create() {
        cam = new OrthographicCamera();
        viewport = new FitViewport(com.cgossip.psyphysics.main.Game.V_WIDTH, com.cgossip.psyphysics.main.Game.V_HEIGHT,cam);
        viewport.apply();
    }

    @Override
    public void resize(int w, int h) {
        // viewport.update(w,h);
    }
    int backf=0;
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            backf=backf^1;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if(character=='c'){
            backf=backf^1;
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        /*
        for(int i=0;i<buttons.length;i++) {
            if (buttons[i].isPressed(touchPos)) {
                if (i == 0) {
                    gsm.setState(GameStateManager.PLAY);
                }

                else if (i == 1) {
                    game.startGameKidsMode();
                    game.gotoGameScreen(null);
                }
                else if (i == 2) {
                    game.showHighscores();
                }
                break;

            }
        }
*/
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 touchPos = new Vector3();
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);
        System.out.println(touchPos.x + " " + touchPos.y);
        //  System.out.println(buttons[0].isPressed(touchPos));
        System.out.println(touchPos.x + " " + touchPos.y);
        if(buttons[0].isPressed(touchPos)){
            System.out.println("Hwre");
            gsm.setState(GameStateManager.SELECTLEVEL);

        }
        if(buttons[1].isPressed(touchPos)){
            System.out.println("Hwre");
            gsm.setState(GameStateManager.CREDITS);

        }
        if(buttons[2].isPressed(touchPos)){
            System.out.println("Hwre");
            Gdx.app.exit();

        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}