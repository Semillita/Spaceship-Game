package core;

import org.joml.Vector2f;
import org.joml.Vector3f;

import io.semillita.hugame.core.ApplicationListener;
import io.semillita.hugame.core.HuGame;
import io.semillita.hugame.graphics.Batch;
import io.semillita.hugame.graphics.Camera;
import io.semillita.hugame.graphics.Model;
import io.semillita.hugame.graphics.ModelBuilder;
import io.semillita.hugame.graphics.OrthographicCamera;
import io.semillita.hugame.graphics.Texture;
import io.semillita.hugame.graphics.Textures;
import io.semillita.hugame.input.Key;
import io.semillita.hugame.util.Transform;
import io.semillita.hugame.window.WindowConfiguration;

public class Application extends ApplicationListener {

	public static void main(String[] args) {
		var app = new Application();

		HuGame.start(new Application(), new WindowConfiguration().width(960).height(540).title("Hugo").x(500).y(300).decorated(true));
	}
	
	private Texture cookieTexture;
	
	private Model groundModel;
	private Model cubeModel;
	
	private Transform groundTransform;
	private Transform cubeTransform;
	
	private Batch batch;
	private Camera camera2D;
	
	private Vector3f position;
	private float dir = 0;
	
	@Override
	public void onCreate() {
		cookieTexture = Textures.get("/cookies.jpg");
		
		ModelBuilder builder = new ModelBuilder();
		
		builder.triangle(new Vector3f(-1, 0, -1), new Vector3f(1, 0, -1), new Vector3f(1, 0, 1), 0, 0, 1, 0, 1, 1, 0);
		builder.triangle(new Vector3f(1, 0, 1), new Vector3f(-1, 0, 1), new Vector3f(-1, 0, -1), 1, 1, 0, 1, 0, 0, 0);
		builder.texture(cookieTexture);
		groundModel = builder.generate();
		
		builder.cube(cookieTexture, cookieTexture, cookieTexture, cookieTexture, cookieTexture, cookieTexture);
		cubeModel = builder.generate();
		
		groundTransform = new Transform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(10, 1, 10));
		cubeTransform = new Transform(new Vector3f(0, 15, 0), new Vector3f(0, 0, 0), new Vector3f(5, 5, 20));
		
		batch = new Batch();
		camera2D = new OrthographicCamera(new Vector2f(0, 0));
		
		position = new Vector3f(0, 10, 0);
	}
	
	@Override
	public void onRender() {
		move();
		
		cubeTransform.position = position;
		cubeTransform.update();
		
		final var renderer = HuGame.getRenderer();
		final var camera = renderer.getCamera();
		camera.setPosition(new Vector3f(position.x, position.y + 5, position.z));
		
		renderer.draw(groundModel, groundTransform);
		renderer.draw(cubeModel, cubeTransform);
		renderer.renderModels();
		
		batch.begin();
		batch.useCamera(camera2D);
		batch.drawQuad(cookieTexture, 100, 100, 100, 100);
		batch.end();
	}
	
	@Override
	public boolean onClose() {
		return true;
	}
	
	private void move() {
		final var input = HuGame.getInput();
		
		if (input.isKeyPressed(Key.D)) {
			dir -= 1;
		}
		
		if (input.isKeyPressed(Key.A)) {
			dir += 1;
		}
		
		cubeTransform.rotation.y = dir;
		
		final float speed = 1;
		final float dirRad = (float) Math.toRadians(dir - 90);
		
		float movementX = (float) Math.cos(dirRad) * speed;
		float movementZ = (float) Math.sin(dirRad) * speed;
		System.out.println(movementX + ", " + movementZ);
		
		if (input.isKeyPressed(Key.W)) {
			position.x -= movementX;
			position.z += movementZ;
		}
		
		if (input.isKeyPressed(Key.S)) {
			position.x += movementX;
			position.z -= movementZ;
		}
	}
	
}
