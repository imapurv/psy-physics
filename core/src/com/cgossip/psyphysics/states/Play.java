package com.cgossip.psyphysics.states;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cgossip.psyphysics.handlers.GameStateManager;
import com.cgossip.psyphysics.view.Button;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

public class Play extends GameState implements InputProcessor,ApplicationListener {

	private World world;
	private Box2DDebugRenderer b2dr;
	SpriteBatch sb;
	Stack<Body> undo;
	ArrayList<Body> all;
	ArrayList<String> warning;
	ArrayList<Texture> tall;
	ArrayList<Sprite> spriteall;
	private OrthographicCamera b2dCam;
	private OrthographicCamera camera;
	int width,height;
	int hogaya=1;
	int sscore=0;
	private FileHandle vertexShader, fragmentShader;
	public synchronized boolean dowork()throws Exception{

		int a=GameStateManager.getCURLEVEL()+1;
		if (a==12){
			gsm.setState(GameStateManager.SELECTLEVEL);
			return false;
		}
		FileWriter file = new FileWriter(String.valueOf(Gdx.files.internal("json/lvlstatus.json")));
		org.json.JSONObject objs = new org.json.JSONObject();
		try {
			objs.put("Current",a);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		file.write(objs.toString());
		file.flush();
		file.close();
		GameStateManager.setTOTAlLEVEL(a);
		return true;
	}
	private Body createPhysicBodies(Array<Vector2> input, World world) {
		System.out.println("Size :" + input.size);
		if (input.size <= 2) {

			return null;
		}

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		//System.out.println();
		Body body = world.createBody(bodyDef);

		//Detecting collision
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				if ((contact.getFixtureA().getBody() == bodycircle &&
						contact.getFixtureB().getBody() == bstar)
						||
						(contact.getFixtureA().getBody() == bstar &&
								contact.getFixtureB().getBody() == bodycircle)) {
					System.out.println("Here collision");
					bstar.applyForceToCenter(new Vector2(0, 20), true);
					pass = 1;
				}

			}

			@Override
			public void endContact(Contact contact) {
				if ((contact.getFixtureA().getBody() == bodycircle &&
						contact.getFixtureB().getBody() == bstar)
						||
						(contact.getFixtureA().getBody() == bstar &&
								contact.getFixtureB().getBody() == bodycircle)) {
					Timer.schedule(new Timer.Task() {
						@Override
						public void run() {
							// Do your work
							if(hogaya==1) {
								hogaya = 0;
								FileWriter file = null;
								if(GameStateManager.getCURLEVEL()==GameStateManager.getTOTAlLEVEL()){
									try {

										if(!dowork()){
											return;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								//GameStateManager.setCURLEVEL(GameStateManager.getCURLEVEL() + 1);
								//gsm.setState(GameStateManager.PLAY);
								gsm.setState(GameStateManager.LEVELCLEAR);
							}
						}
					}, 3);
				}
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});



		for (int i = 0; i < input.size - 1; i++) {
			Vector2 point = input.get(i);
			Vector2 dir = input.get(i + 1).cpy().sub(point);

			try {
				float distance = dir.len();


				//System.out.println("---->" + distance);
				if (distance == 0.00)
					continue;
				if (distance / 2.f < 0.00)
					continue;
				if (Math.abs(distance - 1.1920929E-7) < 0.001)
					continue;
				// if(distance<1.1)
				//    continue;

				float angle = dir.angle() * MathUtils.degreesToRadians;

				PolygonShape shape = new PolygonShape();

				shape.setAsBox(distance / 2, 3.5f / com.cgossip.psyphysics.handlers.B2DVars.PPM, dir.cpy()
						.scl(0.5f).add(point), angle);
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = shape;
				float den = (float) circum/3000;
				System.out.println("Circumference "+circum);
				System.out.println("Density "+den);
				if (den > 1.0f) den = 1;
				fixtureDef.density = den;
				fixtureDef.friction = 0.2f;
				fixtureDef.restitution = 0.2f; // Make it bounce a little bit
				fixtureDef.filter.categoryBits = com.cgossip.psyphysics.handlers.B2DVars.DYNAMIC;
				fixtureDef.filter.maskBits = com.cgossip.psyphysics.handlers.B2DVars.BALL| com.cgossip.psyphysics.handlers.B2DVars.STATIC| com.cgossip.psyphysics.handlers.B2DVars.DYNAMIC;

				body.createFixture(fixtureDef);
				shape.dispose();
			} catch (Exception E) {
			}

		}
		System.out.println("Created");
		undo.push(body);
		all.add(body);
		sscore++;
		return body;
	}
	void ColorPush(){
		col.add(getVal(39, 64, 131));
		col.add(getVal(196,46,84));
		col.add(getVal(239,122,42));
		col.add(getVal(128,155,140));
		col.add(getVal(219,170,191));col.add(getVal(97,60,163));
		col.add(getVal(245,225,89));
		col.add(getVal(216,168,164));
		col.add(getVal(20,167,61));
		col.add(getVal(182,237,59));
		col.add(getVal(0,0,158));
		col.add(getVal(47,148,219));
		col.add(getVal(255,215,76));




		warning=new ArrayList<String>();
		warning.add("can't you read numbers ?");
		warning.add("do me a favour and see downleft");
		warning.add("waacha wrong with you ?");
		warning.add("can't do it. you are OUTa line");
		warning.add("time to undo");

	}
	Color getVal(int r,int g,int b){
		//return new Color().
		return new Color(r/255f,g/255f,b/255f,.5f);

	}
	Body bodycircle;
	int pass=0;
	int dirty = 0;
	Texture img,tstar;
	Texture ball;
	Body bstar;

	public Button buttons[],dbuttons[],buttonc;
	private TextureRegion soundon,soundoff,back,menu,resoff,reson,menuoff,menuon,tex,backoff,menup;
	private TextureAtlas textatlas,dtatlas;
	private BitmapFont font,lfont;;
	private Skin textSkin,dtskin;
	private Texture dialogback;
	private TextureRegion close;
	private ShaderProgram shader;
	public static int score;
	public Play(GameStateManager gsm) {

		super(gsm);

		textatlas = new TextureAtlas("dataa/tools.atlas");
		textSkin= new Skin();
		textSkin.addRegions(textatlas);
		font = new BitmapFont(Gdx.files.internal("dataa/limit.fnt"),false); //** font **//
		font.setColor(0, 0, 0, 1);
		lfont = new BitmapFont(Gdx.files.internal("newui/war.fnt"),false);
		//lfont.setColor(255, 0, 0, 1);
		soundon=new TextureRegion(new Texture(Gdx.files.internal("newui/volumeon.png")));
		soundoff=new TextureRegion(new Texture(Gdx.files.internal("newui/mute.png")));
		back = new TextureRegion(new Texture(Gdx.files.internal("newui/undo.png")));
		backoff= new TextureRegion(new Texture(Gdx.files.internal("newui/undopressed.png")));
		menu=new TextureRegion(new Texture(Gdx.files.internal("newui/menu.png")));
		menup=new TextureRegion(new Texture(Gdx.files.internal("newui/menupressed.png")));

		vertexShader = Gdx.files.internal("vertex.glsl");
		fragmentShader = Gdx.files.internal("fragment.glsl");
		shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled()) {
			Gdx.app.log("Shader", shader.getLog());
			Gdx.app.exit();
		}
		sb = new SpriteBatch();
		sb.setShader(shader);
		shader.begin();
		shader.setUniformi("u_texture", 0);
		shader.setUniformi("u_mask", 1);
		shader.end();

		/* Pixmap blending can result result in some funky looking lines when
		 * drawing. You may need to disable it. */
		Pixmap.setBlending(Pixmap.Blending.None);


		buttons = new Button[5];
		buttons[0] = new Button(soundon);
		buttons[0].setPos(18, 400);
		buttons[1] = new Button(back);
		buttons[1].setPos(660, 400);
		buttons[2] = new Button(menu);
		buttons[2].setPos(735, 400);


		dtatlas = new TextureAtlas("dataa/playdialog.pack");
		dtskin = new Skin();
		dtskin.addRegions(dtatlas);
		resoff = new TextureRegion(new Texture(Gdx.files.internal("newui/reload.png")));
		reson = new TextureRegion(new Texture(Gdx.files.internal("newui/reloadpressed.png")));
		menuoff = new TextureRegion(new Texture(Gdx.files.internal("newui/gomenu.png")));
		menuon = new TextureRegion(new Texture(Gdx.files.internal("newui/gomenupressed.png")));
		tex = new TextureRegion(new Texture(Gdx.files.internal("newui/playdialog.png")));
		close = new TextureRegion(new Texture(Gdx.files.internal("newui/cross.png")));

		dbuttons = new Button[3];
		dbuttons[0] = new Button(resoff);
		dbuttons[0].setPos(280,210);
		dbuttons[1] = new Button(menuoff);
		dbuttons[1].setPos(450,210);
		dbuttons[2] = new Button(close);
		dbuttons[2].setPos(565,285);

		buttonc = new Button(close);
		buttonc.setPos(735,300);

		end=new Texture(Gdx.files.internal("dataa/levelcom.png"));
		tstar=  new Texture(Gdx.files.internal("dataa/star.png"));
		img = new Texture(Gdx.files.internal("newui/backgroundplay.png"));
		ball =new Texture(Gdx.files.internal("dataa/ball.png"));
		world = new World(new Vector2(0, -9.81f), true);
		b2dr = new Box2DDebugRenderer();
		ar = new Array<Vector2>();
		undo = new Stack<Body>();
		width=800;
		spriteall=new ArrayList<Sprite>();
		height=480;
		col=new ArrayList<Color>();
		ColorPush();
		try {
			readJson();
		} catch (Exception e) {
			System.out.println("Sadly Not Worked"+e.getMessage());
			e.printStackTrace();
		}
		//width=Gdx.graphics.getWidth();
		//height=Gdx.graphics.getHeight();

		// create platform
		BodyDef bdef = new BodyDef();
		bdef.position.set(120 / com.cgossip.psyphysics.handlers.B2DVars.PPM, 120 / com.cgossip.psyphysics.handlers.B2DVars.PPM);
		bdef.type = BodyType.StaticBody;
		//Body body = world.createBody(bdef);


		//font = new BitmapFont();
		all = new ArrayList<Body>();
		tall = new ArrayList<Texture>();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(50 / com.cgossip.psyphysics.handlers.B2DVars.PPM, 120 / com.cgossip.psyphysics.handlers.B2DVars.PPM);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		//body.createFixture(fdef);

		//second
		bdef.position.set(680 / com.cgossip.psyphysics.handlers.B2DVars.PPM, 120 / com.cgossip.psyphysics.handlers.B2DVars.PPM);
		bdef.type = BodyType.StaticBody;
		//Body bodys = world.createBody(bdef);
		pixmap = new Pixmap(width,height, Pixmap.Format.RGBA8888);


		PolygonShape shapes = new PolygonShape();
		shapes.setAsBox(50 / com.cgossip.psyphysics.handlers.B2DVars.PPM, 120 / com.cgossip.psyphysics.handlers.B2DVars.PPM);
		FixtureDef fdefs = new FixtureDef();
		fdefs.shape = shapes;

		//bodys.createFixture(fdefs);


		//circle
		BodyDef bodyDef = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
// Set our body's starting position in the world
		bodyDef.position.set(ballx, bally);
		bodycircle = world.createBody(bodyDef);
		Sprite bls=new Sprite(ball);
		bls.setSize(55,55);
		bodycircle.setUserData(bls);
// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(25f / com.cgossip.psyphysics.handlers.B2DVars.PPM);

// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.05f;
		fixtureDef.friction = 0.1f;
		fixtureDef.restitution = 0.0f; // Make it bounce a little bit
		fixtureDef.filter.categoryBits = com.cgossip.psyphysics.handlers.B2DVars.BALL;
		fixtureDef.filter.maskBits = com.cgossip.psyphysics.handlers.B2DVars.STATIC| com.cgossip.psyphysics.handlers.B2DVars.DYNAMIC| com.cgossip.psyphysics.handlers.B2DVars.STAR;
// Create our fixture and attach it to the body
		Fixture fixture = bodycircle.createFixture(fixtureDef);

// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();

		BodyDef star = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		star.type = BodyType.DynamicBody;
// Set our body's starting position in the world
		star.position.set(starx, stary);
		bstar= world.createBody(star);

		PolygonShape sshape = new PolygonShape();
		sshape.setAsBox(25 / com.cgossip.psyphysics.handlers.B2DVars.PPM, 25 / com.cgossip.psyphysics.handlers.B2DVars.PPM);
		FixtureDef sfdef = new FixtureDef();
		sfdef.density = 0.1f;
		sfdef.shape = sshape;
		sfdef.filter.categoryBits = com.cgossip.psyphysics.handlers.B2DVars.STAR;
		sfdef.filter.maskBits = com.cgossip.psyphysics.handlers.B2DVars.BALL| com.cgossip.psyphysics.handlers.B2DVars.STATIC;
		bstar.createFixture(sfdef);
		Sprite tls=new Sprite(tstar);
		tls.setSize(55, 55);
		bstar.setUserData(tls);

		// create falling box
      /*
      bdef.position.set(160 / PPM, 200 / PPM);
      bdef.type = BodyType.DynamicBody;
      body = world.createBody(bdef);
      
      shape.setAsBox(20 / PPM, 20 / PPM);
      fdef.shape = shape;
      body.createFixture(fdef);
      */

		// set up box2d cam
		//b2dCam = new PerspectiveCamera();
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, com.cgossip.psyphysics.main.Game.V_WIDTH / com.cgossip.psyphysics.handlers.B2DVars.PPM, com.cgossip.psyphysics.main.Game.V_HEIGHT / com.cgossip.psyphysics.handlers.B2DVars.PPM);
		//viewport = new FitViewport(Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM, b2dCam);

		Gdx.input.setInputProcessor(this);



	}
	float ballx,bally;
	float starx,stary;
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {
			// if the hit fixture's body is the ground body
			// we ignore it

			//System.out.println("Here");
			hitBody = fixture.getBody();

			// if the hit point is inside the fixture of the body
			// we report it
			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				//System.out.println("Here1");
				hitBody = fixture.getBody();
				if (hitBody.equals(bodycircle))
					hitBody.applyForceToCenter(new Vector2(-5, 0), true);
				return false;
			} else
				return true;
		}
	};

	public void handleInput() {
	}

	private Viewport viewport;

	public void update(float dt) {
		world.step(dt, 6, 2);
	}

	Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
	Texture pixmaptex;
	Sprite sss;
	public static Sprite starsp,ballsp;
	Sprite runtimesp,staticsp;
	int dialog=0;

	public void render() {


		// clear screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



		//Update level according to the position of ball
		if (bodycircle.getPosition().y < -50){
			System.out.println("Update the level");
			//GameStateManager.setCURLEVEL(GameStateManager.getCURLEVEL()+1);
			gsm.setState(GameStateManager.PLAY);
		}



		for (int i = 0; i < all.size(); i++) {
			if (all.get(i).getPosition().y < -10) {
				world.destroyBody(all.get(i));
				all.remove(i);
				spriteall.get(i).getTexture().dispose();
				spriteall.remove(i);

				// tall.get(i).dispose();
				System.out.println("Removed " + i);
			}
		}

		b2dr.render(world, b2dCam.combined);
		sb.begin();

		sb.draw(img, 0, 0);
		cam.update();
		sb.setProjectionMatrix(cam.combined);
		try {
			for (int i = 0; i < spriteall.size(); i++) {
				sss = spriteall.get(i);
				sss.setOrigin(all.get(i).getLocalCenter().x, all.get(i).getLocalCenter().y);
				//sss.setOrigin(sss.getWidth()/2,sss.getHeight()/2);
				//s.setPosition(all.get(i).getWorldCenter().x*PPM, all.get(i).getWorldCenter().y*PPM);
				sss.setPosition(all.get(i).getPosition().x * com.cgossip.psyphysics.handlers.B2DVars.PPM , all.get(i).getPosition().y * com.cgossip.psyphysics.handlers.B2DVars.PPM);
				sss.setRotation((float) all.get(i).getAngle() * MathUtils.radiansToDegrees);
				sss.draw(sb);

/*
         sb.draw(s, s.getX(), s.getY(),s.getOriginX(),
               s.getOriginY(),
               s.getWidth(),s.getHeight(),s.getScaleX(),s.
                     getScaleY(),s.getRotation());
                     */

			}
		}
		catch (Exception e){}
		//Sprite ts = new Sprite(statictexture);
		staticsp.draw(sb);
		//ts.draw(sb);
		ballsp = (Sprite) bodycircle.getUserData();
		//Vector2 bottlePos = bodycircle.getPosition();
		ballsp.setOrigin(ballsp.getWidth() / 2, ballsp.getHeight() / 2);
		//sss.setOrigin(bodycircle.getWorldCenter().x * PPM, bodycircle.getWorldCenter().y * PPM);
		//sss.setOrigin(sss.getWidth()/2,sss.getHeight()/2);
		ballsp.setPosition(bodycircle.getPosition().x * com.cgossip.psyphysics.handlers.B2DVars.PPM - ballsp.getWidth() / 2, bodycircle.getPosition().y * com.cgossip.psyphysics.handlers.B2DVars.PPM - ballsp.getHeight() / 2);
		//sss.setBounds(0,0,100,100);
		//ballsp.setRotation((float) bodycircle.getAngle() * MathUtils.radiansToDegrees);
		ballsp.draw(sb);

		starsp = (Sprite) bstar.getUserData();
		//Vector2 bottlePos = bodycircle.getPosition();
		starsp.setOrigin(starsp.getWidth() / 2, starsp.getHeight() / 2);
		//sss.setOrigin(bodycircle.getWorldCenter().x * PPM, bodycircle.getWorldCenter().y * PPM);
		//sss.setOrigin(sss.getWidth()/2,sss.getHeight()/2);
		starsp.setPosition(bstar.getPosition().x * com.cgossip.psyphysics.handlers.B2DVars.PPM - starsp.getWidth() / 2, bstar.getPosition().y * com.cgossip.psyphysics.handlers.B2DVars.PPM - starsp.getHeight() / 2);
		//sss.setBounds(0,0,100,100);
		starsp.setRotation((float)bstar.getAngle() * MathUtils.radiansToDegrees);
		starsp.draw(sb);

		//pixmaptex= ;

		buttons[0].draw(sb);
		buttons[1].draw(sb);
		buttons[2].draw(sb);

		//Create dialog box
		if (dialog == 1 ){

			//sb.draw(dialogback,0,0);
			sb.draw(tex, 200, 180);
			dbuttons[0].draw(sb);
			dbuttons[1].draw(sb);
			dbuttons[2].draw(sb);

		}


		if (dirty == 1) {
			//Pixmap p=pixmap;
			//System.out.println("Hereee "+p.getHeight()+p.getFormat());

			//pixmaptex= new Texture(p);
			runtime.draw(pixmap, 0, 0);
			//pixmap.dispose();

			sss = new Sprite(runtime);
			sss.draw(sb);

			//pixmaptex.dispose();
			//p.dispose();

		}
		if(pass==1){
			//sb.draw(end, 0, 0);

		}

		//pixmap.dispose();
		//sb.draw(pixmaptex, 0, 0);
		//System.out.println("Did");

		//pixmaptex.dispose();
		score = 10 - all.size();
		GameStateManager.setScore(sscore);
		font.draw(sb,String.valueOf(score), 10, 50);
		//System.out.println(war);
		if(war>0){

			lfont.draw(sb, warning.get(0), 335 - warning.get(0).length() * 10, 330);
			//System.out.println(Gdx.graphics.getDeltaTime()+" "+war);
			war-=Gdx.graphics.getDeltaTime()*.0001;
			if(war<=0)
				war=0;
		}
		sb.end();
		// draw box2d world


	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	Texture runtime;

	@Override
	public void rendersb(SpriteBatch sb) {
		font.draw(sb, "jk,bk,", 100, 100);
	}

	public void dispose() {
		for (int i = 0; i < spriteall.size(); i++) {
			spriteall.get(i).getTexture().dispose();
			//tall.get(i).dispose();
		}
		statictexture.dispose();
		try {
			runtime.dispose();
		}catch (Exception e){}
		soundon.getTexture().dispose();
		soundoff.getTexture().dispose();
		back.getTexture().dispose();
		menu.getTexture().dispose();
		resoff.getTexture().dispose();
		reson.getTexture().dispose();
		menuoff.getTexture().dispose();
		menuon.getTexture().dispose();
		tex.getTexture().dispose();
		backoff.getTexture().dispose();
		menup.getTexture().dispose();
		ball.dispose();
		tstar.dispose();
		dtatlas.dispose();
		textatlas.dispose();
		textSkin.dispose();
		dtskin.dispose();
		font.dispose();
		lfont.dispose();
		close.getTexture().dispose();
		img.dispose();

	}
	Texture end;
	@Override
	public void create() {

		cam = new OrthographicCamera();
		viewport = new FillViewport(com.cgossip.psyphysics.main.Game.V_WIDTH, com.cgossip.psyphysics.main.Game.V_HEIGHT,cam);
		viewport.apply();

	}

	@Override
	public void resize(int w, int h) {
//    viewport.update(w,h);
		//cam.position.set()
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	public void updatePoints(Array<Vector2> ar, World world) {

		Array<Vector2> arr = new Array<Vector2>();
		Vector2 q = null, r = null;

		// System.out.println("Array : -");
		for (int i = 0; i < ar.size; i++)
			System.out.println(ar.get(i).x + "|" + ar.get(i).y);

		// System.out.println("New array : -");
		for (int i = 0; i < ar.size - 1; i++) {
			Vector2 p1 = ar.get(i);
			Vector2 p2 = ar.get(i + 1);
			float x, y;
			x = (3 * p1.x) / 4 + (1 * p2.x) / 4;
			y = (3 * p1.y) / 4 + (1 * p2.y) / 4;
			q = new Vector2(x, y);
			x = (1 * p1.x) / 4 + (3 * p2.x) / 4;
			y = (1 * p1.y) / 4 + (3 * p2.y) / 4;
			r = new Vector2(x, y);
			// System.out.println(q.x + "|" + q.y + " " + r.x + "|" + r.y);
			arr.add(q);
			arr.add(r);
		}
		arr.add(ar.get(ar.size - 1));
		createPhysicBodies(arr, world);
	}
	int flag=1;
	@Override
	public boolean keyTyped(char character) {
		if (all.size() > 0)
			flag = 0;
		if (character == 'w'&&flag==0) { //it's the 'D' key
			world.destroyBody(all.get(all.size() - 1));
			all.remove(all.size() - 1);
			//tall.remove(tall.size() - 1);
			if(all.size()<=0)
				flag=1;
			spriteall.remove(spriteall.size()-1);
		}
		return true;
	}

	Body hitBody = null;
	Vector2 last;
	int bflag=0;
	int war=0;
	int res=0;
	int me=0;
	int unf=0,mf=0;
	int closef=0;
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		res = 0;
		me = 0;
		bflag = 0;
		Vector3 touchPos = new Vector3();
		b2dCam.unproject(touchPos.set(screenX, screenY, 0));
		touchPos.x = touchPos.x * com.cgossip.psyphysics.handlers.B2DVars.PPM;
		touchPos.y = touchPos.y * com.cgossip.psyphysics.handlers.B2DVars.PPM;
		System.out.println("Vol Button " + buttons[0].getPosX() + " " + buttons[0].getPosY());
		System.out.println("Undo Button "+buttons[1].getPosX()+" "+buttons[1].getPosY());
		System.out.println("Menu Button "+buttons[2].getPosX()+" "+buttons[2].getPosY());
		//System.out.println(touchPos.x + " " + touchPos.y);
		//  System.out.println(buttons[0].isPressed(touchPos));
		System.out.println("Touch "+touchPos.x + " " + touchPos.y);
		if(buttons[0].isPressed(touchPos)){
			bflag = 1;
			System.out.println("Vol pressed");
			if (vol == true) {
				vol = false;

				com.cgossip.psyphysics.main.Game.rainMusic.pause();
				buttons[0] = new Button(soundoff);
				buttons[0].setPos(18, 400);
			}
			else {
				vol = true;
				com.cgossip.psyphysics.main.Game.rainMusic.play();
				buttons[0] = new Button(soundon);
				buttons[0].setPos(18, 400);
			}
			return true;
		}
		if(buttons[1].isPressed(touchPos)){
			buttons[1] = new Button(backoff);
			buttons[1].setPos(660, 400);
			bflag = 1;
			unf=1;

			return true;
		}
		/*
		buttons = new Button[5];
		buttons[0] = new Button(soundon);
		buttons[0].setPos(18, 400);
		buttons[1] = new Button(back);
		buttons[1].setPos(660, 400);
		buttons[2] = new Button(menu);
		buttons[2].setPos(735, 400);

		 */
		if(buttons[2].isPressed(touchPos)){
			bflag = 1;
			dialog = 1;
			mf=1;
			buttons[2] = new Button(menup);
			buttons[2].setPos(735, 400);
			System.out.println("Play Dialog pressed");
			//gsm.setState(GameStateManager.SELECTLEVEL);
			return true;
		}
		if(dbuttons[0].isPressed(touchPos) && dialog==1){
			System.out.println("Restart pressed");
			dbuttons[0] = new Button(reson);
			dbuttons[0].setPos(280,210);
			res=1;
			//gsm.setState(GameStateManager.PLAY);
			return true;
		}
		if(dbuttons[1].isPressed(touchPos) && dialog==1){
			System.out.println("Menu pressed");
			dbuttons[1] = new Button(menuon);
			dbuttons[1].setPos(450,210);
			me=1;
			//gsm.setState(GameStateManager.MENU);
			return true;
		}
		if (dbuttons[2].isPressed(touchPos) && dialog == 1){
			System.out.println("Close");
			closef = 1;
			return true;
		}

		Vector3 touch = new Vector3();

		b2dCam.unproject(touch.set(screenX, screenY, 0));
		circum = 0;
		testPoint.set(x, y, 0);
		b2dCam.unproject(testPoint);




		last = new Vector2(touch.x * com.cgossip.psyphysics.handlers.B2DVars.PPM, touch.y * com.cgossip.psyphysics.handlers.B2DVars.PPM);
		touch.x = touch.x * com.cgossip.psyphysics.handlers.B2DVars.PPM;
		touch.y = touch.y * com.cgossip.psyphysics.handlers.B2DVars.PPM;
		//body.applyForce(0.1f, 0.1f, screenX, screenY, true);
		//makenewPIx();
		pixmap =new Pixmap(width,height, Pixmap.Format.RGBA8888);
		runtime = new Texture(pixmap);
		//pixmap.setColor(new Color(0, 0, 0, 0));
		pixmap.fill();
		Collections.shuffle(col);
		pixmap.setColor(col.get(0));
		//pixmap.fillCircle(32, 32, 32);
		//pixmap.fillCircle( (int)touch.x, (int)touch.y, 1 );
		ar.clear();

		ar.add(new Vector2(touch.x / com.cgossip.psyphysics.handlers.B2DVars.PPM, (touch.y) / com.cgossip.psyphysics.handlers.B2DVars.PPM));
		dirty = 1;
		//Texture pixmaptex = new Texture(pi);
		//body.applyTorque(0.4f,true);
		//.out.println("touch Down");
		return true;
	}
	boolean vol = true;
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(unf==1){
			buttons[1] = new Button(back);
			buttons[1].setPos(660, 400);
			System.out.println("Undo pressed");
			if (all.size() > 0)
				flag = 0;
			if (flag==0) { //it's the 'D' key
				world.destroyBody(all.get(all.size() - 1));
				all.remove(all.size() - 1);
				//tall.remove(tall.size() - 1);
				if(all.size()<=0)
					flag=1;
				spriteall.remove(spriteall.size() - 1);
			}

			unf=0;
			return true;
		}
		if(mf==1){
			buttons[2] = new Button(menu);
			buttons[2].setPos(735, 400);
			mf=0;
			return true;
		}
		if (bflag == 1)
			return false;
		if (res == 1) {
			System.out.println("Restart pressed");
			gsm.setState(GameStateManager.PLAY);
			return true;
		}
		if (me == 1){
			System.out.println("Menu pressed");
			gsm.setState(GameStateManager.MENU);
			return true;
		}
		if (closef == 1){
			 closef=0;
			dialog=0;
			return true;
		}
		Vector3 touchPos = new Vector3();
		System.out.println("In touch up");
		b2dCam.unproject(touchPos.set(screenX, screenY, 0));
		touchPos.x = touchPos.x * com.cgossip.psyphysics.handlers.B2DVars.PPM;
		touchPos.y = touchPos.y * com.cgossip.psyphysics.handlers.B2DVars.PPM;
		System.out.println("Touch "+touchPos.x + " " + touchPos.y);

		if (dialog == 0) {
			Vector3 touch = new Vector3();
			dirty = 0;
			b2dCam.unproject(touch.set(screenX, screenY, 0));
			touch.x = touch.x * com.cgossip.psyphysics.handlers.B2DVars.PPM;
			touch.y = touch.y * com.cgossip.psyphysics.handlers.B2DVars.PPM;
			x = (int) touch.x;
			y = (int) (touch.y);
			ar.add(new Vector2(x / com.cgossip.psyphysics.handlers.B2DVars.PPM, y / com.cgossip.psyphysics.handlers.B2DVars.PPM));
			//updatePoints(ar, world);
			Body bbs = null;
			if (10 - all.size() - 1 >= 0) {
				bbs = createPhysicBodies(ar, world);
			} else {
				Collections.shuffle(warning);
				war = 100;
				return true;
			}
			try {
				drawLerped(new Vector2((int) last.x, height - (int) last.y), new Vector2(x, height - y));
			}
			catch (Exception e){}
			//pixmap.scaled(1000, 600);
			// pixmap.scale
			try {
				if (bbs != null) {
					pixmaptex = new Texture(pixmap);
					//tall.add(pixmaptex);
					spriteall.add(new Sprite(pixmaptex));
					//pixmaptex.dispose();
					//tall.clear();v
				}
			}
			catch (Exception e){}
			try {
				pixmap.dispose();
			}
			catch (Exception e){}
			//pixmaptex.dispose();
			//createPhysicBodies(ar,world);
			//createbody(ar, world);
			//pixmap.dispose();
			count = 0;
			//pre=b;
			//System.out.println("touch Up");


		}
		return true;
	}

	void createbody(Array<Vector2> input, World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(input.get(0).x /
						com.cgossip.psyphysics.handlers.B2DVars.PPM,
				(input.get(0).y / com.cgossip.psyphysics.handlers.B2DVars.PPM));
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		Body body = world.createBody(bodyDef);
		// System.out.println(input.size);
		if (input.size < 2)
			return;
		if (input.size <= 3) {
			EdgeShape es = new EdgeShape();
			es.set(input.get(0), input.get(1));
			body.createFixture(es, 1.0f);
			return;
		}
		PolygonShape shape = new PolygonShape();
		Vector2 tmp[] = new Vector2[input.size - 1];
		for (int i = 0; i < input.size - 1; i++) {
			tmp[i] = input.get(i);
		}
		shape.set(tmp);
		body.createFixture(shape, 1.0f);
		shape.dispose();
	}

	int x = 0, y = 0;
	Array<Vector2> ar;
	int count = 0;
	int circum=0;
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (dialog == 1)
			return false;
		try {
			Vector3 touch = new Vector3();
			b2dCam.unproject(touch.set(screenX, screenY, 0));
			//dirty=0;
			touch.x = touch.x * com.cgossip.psyphysics.handlers.B2DVars.PPM;
			touch.y = touch.y * com.cgossip.psyphysics.handlers.B2DVars.PPM;
			// System.out.println(touch.x + " " + touch.y);
			// System.out.println("Ready ::" + touch.x + " " + touch.y);

			// System.out.println("UReady ::" + touch.x + " " + touch.y);
			//if (Math.sqrt(Math.pow(( last.x - touch.x), 2) + Math.pow((((int) last.y) -  touch.y), 2)) > 2)
			drawLerped(new Vector2((int) last.x, height - (int) last.y), new Vector2(touch.x, height - touch.y));
			circum += new Vector2((int) last.x, height - (int) last.y).dst2(new Vector2(touch.x, height - touch.y));
			last = new Vector2(touch.x, touch.y);


			//System.out.println(Gdx.graphics.getWidth()+" "+Gdx.graphics.getHeight());
			//y=Gdx.graphics.getHeight()-screenY;
			if (Math.sqrt(Math.pow((touch.x - x), 2) + Math.pow((touch.y - y), 2)) > 20) {
				x = (int) touch.x;
				y = (int) (touch.y);
				count++;

				//if(count<8)
				ar.add(new Vector2(x / com.cgossip.psyphysics.handlers.B2DVars.PPM, y / com.cgossip.psyphysics.handlers.B2DVars.PPM));
				// System.out.println("Last" + last.x + " " + last.y);
				//pixmap.drawLine((int) last.x, Gdx.graphics.getHeight() - (int) last.y, x, Gdx.graphics.getHeight() - y);
				//drawLerped(new Vector2((int) last.x, Gdx.graphics.getHeight() - (int) last.y), new Vector2(touch.x, Gdx.graphics.getHeight() - touch.y));
				///pixmap.fillCircle(x, y,5 );
				// System.out.println("touch Drr" + x + " " + y);

			}
			//pi.drawCircle(x % 100, x%100, 3);
			//pi.fillCircle(x % 100, y%100,5);
		}
		catch (Exception e){}
		return true;
	}

	int brushSize = 2;

	private void drawDot(Vector2 spot) {
		pixmap.fillCircle((int) spot.x, (int) spot.y, brushSize);
	}

	public void draw(Vector2 spot) {
		drawDot(spot);

	}

	public void drawLerped(Vector2 from, Vector2 to) {
		float dist = to.dst(from);
         /* Calc an alpha step to put one dot roughly every 1/8 of the brush
          * radius. 1/8 is arbitrary, but the results are fairly nice. */
		float alphaStep = brushSize / (20f * dist);

		for (float a = 0; a < 1f; a += alphaStep) {
			Vector2 lerped = from.lerp(to, a);
			drawDot(lerped);
		}

		drawDot(to);

	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	private Body createPhysicBodiesStatic(Array<Vector2> input, World world) {
		System.out.println("Size :" + input.size);
		if (input.size <= 2)
			return null;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		Body body = world.createBody(bodyDef);
		for (int i = 0; i < input.size - 1; i++) {
			Vector2 point = input.get(i);
			Vector2 dir = input.get(i + 1).cpy().sub(point);

			try {
				float distance = dir.len();


				System.out.println("---->" + distance);
				if (distance == 0.00)
					continue;
				if (distance / 2.f < 0.00)
					continue;
				if (Math.abs(distance - 1.1920929E-7) < 0.001)
					continue;
				// if(distance<1.1)
				//    continue;

				float angle = dir.angle() * MathUtils.degreesToRadians;

				PolygonShape shape = new PolygonShape();

				shape.setAsBox(distance / 2, 2 / com.cgossip.psyphysics.handlers.B2DVars.PPM, dir.cpy()
						.scl(0.5f).add(point), angle);
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = shape;
				fixtureDef.density = 0.4f;
				fixtureDef.friction = 0.2f;
				fixtureDef.restitution = 0.2f; // Make it bounce a little bit
				fixtureDef.filter.categoryBits = com.cgossip.psyphysics.handlers.B2DVars.STATIC;
				fixtureDef.filter.maskBits = com.cgossip.psyphysics.handlers.B2DVars.BALL| com.cgossip.psyphysics.handlers.B2DVars.DYNAMIC| com.cgossip.psyphysics.handlers.B2DVars.STAR;
				body.createFixture(fixtureDef);
				shape.dispose();
			} catch (Exception E) {
			}

		}
		undo.push(body);
		//all.add(body);
		return body;
	}

	public ArrayList<Vector2> tttext;
	public ArrayList<Color> col;
	public Texture statictexture;

	void readJson() throws Exception {
		pixmap = new Pixmap(width,height, Pixmap.Format.RGBA8888);

		pixmap.setColor(new Color(0, 0, 0, 0));
		pixmap.fill();
		pixmap.setColor(getVal(157,124,79));
		JSONParser parser = new JSONParser();
		System.out.println("Oneeee");

		//Check level to be displayed

/*
      FileHandle readF = Gdx.files.internal("json/curlevel.txt");
      String lno;
      lno = readF.readString();
      int ln = Integer.parseInt(lno);
*/
		int ln = GameStateManager.getCURLEVEL();
		String selLev = "json/level"+ln+".json";

		System.out.println("I got it");
		FileHandle fileHandle = Gdx.files.internal(selLev);
		String s = new String(fileHandle.readString());
		Object obj = parser.parse(s);



		System.out.println("Twoooo");
		tttext = new ArrayList<Vector2>();

		org.json.simple.JSONObject jsonObject = (JSONObject) obj;


		JSONObject ball=(JSONObject)jsonObject.get("Ball");
		JSONObject star=(JSONObject)jsonObject.get("Star");
		System.out.println(ball.get("x")+" "+ball.get("y"));
		Double d =(Double)ball.get("x");
		ballx=d.floatValue();
		d =(Double)ball.get("y");
		bally=d.floatValue();
		d =(Double)star.get("x");
		starx=d.floatValue();
		d =(Double)star.get("y");
		stary=d.floatValue();
		JSONArray texture = (JSONArray) jsonObject.get("Texture");
		Iterator iterator = texture.iterator();
		while (iterator.hasNext()) {
			String tp = (String) iterator.next();
			// System.out.println(tp.substring(1,tp.indexOf(','))+" "+tp.substring(tp.indexOf(','),tp.indexOf(')')));

			float x = Float.parseFloat(tp.substring(1, tp.indexOf(',')));
			float y = Float.parseFloat(tp.substring(tp.indexOf(',') + 1, tp.indexOf(')')));
			tttext.add(new Vector2(x, y));

		}
		System.out.println("Tecture " + Arrays.toString(tttext.toArray()));
		for (int i = 0; i < tttext.size(); i=i+2) {
			drawLerped(tttext.get(i), tttext.get(i + 1));
		}
		statictexture = new Texture(pixmap);
		staticsp=new Sprite(statictexture);
		System.out.println("Three");
		pixmap.dispose();
		texture = (JSONArray) jsonObject.get("Points");
		JSONObject jb = (JSONObject) texture.get(0);


		int i = 0;
		//System.out.println("Here");
		while (jb.get("" + i + "") != null) {
			String tmp = (String) jb.get("" + i + "");
			int j = tmp.indexOf("(");
			Array<Vector2> tmps = new Array<Vector2>();
			try {
				while (j != -1) {
					String tp = tmp.substring(j, tmp.indexOf(' ', j) - 1);
					j = tmp.indexOf(' ', j);
					//System.out.println(tp);
					System.out.println(tp.substring(1,tp.indexOf(','))+" "+tp.substring(tp.indexOf(','),tp.indexOf(')')));

					float x = Float.parseFloat(tp.substring(1, tp.indexOf(',')));
					float y = Float.parseFloat(tp.substring(tp.indexOf(',') + 1, tp.indexOf(')')));
					tmps.add(new Vector2(x, y));
					//System.out.println(j);

					j = tmp.indexOf('(', j);
					// System.out.println(j);

				}
			} catch (Exception e) {
				String tp = tmp.substring(j, tmp.indexOf(']', j));
				j = tmp.indexOf(']', j);
				// System.out.println(tp);
				System.out.println(tp.substring(1,tp.indexOf(','))+" "+tp.substring(tp.indexOf(','),tp.indexOf(')')));

				float x = Float.parseFloat(tp.substring(1, tp.indexOf(',')));
				float y = Float.parseFloat(tp.substring(tp.indexOf(',') + 1, tp.indexOf(')')));
				tmps.add(new Vector2(x, y));
				//System.out.println(j);

				//j = tmp.indexOf('(', j);
				//System.out.println(j);
			}

			createPhysicBodiesStatic(tmps, world);
			System.out.println("List " + Arrays.toString(tmps.toArray()));
			// System.out.println(jb.get("" + i + ""));
			i++;
		}


	}
}
