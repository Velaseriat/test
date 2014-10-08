package com.mygdx.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;


public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	 Texture img;
	    TiledMap tiledMap;
	    PerspectiveCamera camera;
	    TiledMapRenderer tiledMapRenderer;
		private Vector3 target, tileDragTarget;

	    
	    @Override
	    public void create () {
	    	camera = new PerspectiveCamera(65, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	        camera.update();
	        camera.position.set(0f, -100f, 150f);
	        camera.lookAt(0f, 0f, 0f);
	        camera.near = 0.1f; 
	        camera.far = 4000.0f;
	        target = camera.position.cpy();
	        tileDragTarget = target.cpy();
	        tiledMap = new TmxMapLoader().load("level.tmx");
	        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
	        Gdx.input.setInputProcessor(this);
	    }

	    @Override
	    public void render () {
	        Gdx.gl.glClearColor(0, 0, 0, 1);
	        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	        camera.update();
	        tiledMapRenderer.setView(camera.combined, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	        tiledMapRenderer.render();
	        //camera.position.lerp(target, 4f*Gdx.graphics.getDeltaTime());
	        //camera.position.set(target);
	        
	    }

	    @Override
	    public boolean keyDown(int keycode) {
	        return false;
	    }

	    @Override
	    public boolean keyUp(int keycode) {
	        if(keycode == Input.Keys.LEFT)
	           // camera.translate(-32,0);
	        	camera.position.add(-32f, 0f, 0f);
	        if(keycode == Input.Keys.RIGHT)
	            //camera.translate(32,0);
	        	camera.position.add(32f, 0f, 0f);
	        if(keycode == Input.Keys.UP)
	            //camera.translate(0,-32);
	        	camera.position.add(0f, 32f, 0f);
	        if(keycode == Input.Keys.DOWN)
	           // camera.translate(0,32);
	        	System.out.println("Camera: " + camera.position);
	        if(keycode == Input.Keys.NUM_1)
	            tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
	        if(keycode == Input.Keys.NUM_2)
	            tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());
	        return false;
	    }

	    @Override
	    public boolean keyTyped(char character) {

	        return false;
	    }

	    @Override
	    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	    	//On touchdown, it picks a tileDragTarget as a starting reference point.
	    	
	    	//The next three lines converts screenCoords to worldMap coords
	    	Ray ray = camera.getPickRay(screenX, screenY); //origin and direction
			float scale = -camera.position.z/ray.direction.z; //scale it
	    	tileDragTarget = ray.direction.scl(scale).cpy().add(camera.position); //make it so it touches the worldMap, and add the original camera position since it was pushed x:0 y:-100 z:250
	    	
	    	System.out.println("Touching down" + tileDragTarget); //starting dragTileTarget print
	    	if (((TiledMapTileLayer)tiledMap.getLayers().get(0)).getCell((int)tileDragTarget.x/32, (int) tileDragTarget.y/32).getTile().getProperties().get("blocked") != null)
	    		System.out.println("BLOCKED TILE"); //just says that it's a blocked tile. 
	        return false;
	    }

	    @Override
	    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	    	//This is to lerp to targets on the map
	    	
	    	/*Ray ray = camera.getPickRay(screenX, screenY);
	    	float scale = -camera.position.z/ray.direction.z;
	    	Vector3 temp = ray.direction.scl(scale);
	    	temp.add(camera.position);
	    	if (((TiledMapTileLayer)tiledMap.getLayers().get(0)).getCell((int)temp.x/32, (int) temp.y/32).getTile().getProperties().get("blocked") != null)
	    		System.out.println("BLOCKED TILE");
	    	temp.z = camera.position.z;
	    	temp.y = temp.y - 100f;
	    	target = temp.cpy();*/
	        return false;
	    }

		@Override
	    public boolean touchDragged(int screenX, int screenY, int pointer) {
			Ray ray = camera.getPickRay(screenX, screenY);
			float scale = -camera.position.z/ray.direction.z;
	    	Vector3 temp3 = ray.direction.scl(scale);
	    	temp3.add(camera.position);
	    	Vector3 diff = new Vector3(-tileDragTarget.x + temp3.x, -tileDragTarget.y + temp3.y, 0f);
	    	camera.position.set(camera.position.cpy().sub(diff));
			System.out.println(diff);
	        return false;
	    }

	    @Override
	    public boolean mouseMoved(int screenX, int screenY) {
	        return false;
	    }

	    @Override
	    public boolean scrolled(int amount) {
	    	target = target.cpy().add(0f, 0f, amount*15f);
	        return false;
	    }

}
