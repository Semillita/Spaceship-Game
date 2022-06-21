package core;

import java.util.ArrayList;
import java.util.List;

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
import net.NetUnit;

public class Application extends ApplicationListener {

	private static final float ROTATION_SPEED = 2;
	
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
	
	private Vector3f playerPosition;
	private Vector3f playerDirection;
	private Vector3f playerUp;
	
	private NetUnit netUnit;
	private List<List<Vector3f>> otherPlayers;
	
	int i = 0;
	
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
		
		playerPosition = new Vector3f(20, 10, 70);
		playerDirection = new Vector3f(0, 0, -1);
		playerUp = new Vector3f(0, 1, 0);
		
		netUnit = new NetUnit();
		netUnit.listenForUpdates((players) -> this.otherPlayers = players);
		otherPlayers = new ArrayList<>();
	}
	
	@Override
	public void onRender() {
		i++;
		if (i % 1 == 0) {
			netUnit.upload(playerPosition, playerDirection);
		}
		
		move();
		
		cubeTransform.position = playerPosition;
		cubeTransform.update();
		
		final var renderer = HuGame.getRenderer();
		renderer.getCamera().setPosition(new Vector3f(40, 80, 40));
		
		renderer.draw(groundModel, groundTransform);
		renderer.draw(cubeModel, cubeTransform);
		
		// Render other players
		for (var p : otherPlayers) {
			var yRotation = getAngleAroundY(p.get(1));
			renderer.draw(cubeModel, new Transform(p.get(0), new Vector3f(0, yRotation, 0), new Vector3f(5, 5, 20)));
		}
		
		renderer.renderModels();
		
		batch.begin();
		batch.useCamera(camera2D);
		batch.drawQuad(cookieTexture, 100, 100, 100, 100);
		batch.end();
	}
	
	@Override
	public boolean onClose() {
		netUnit.close();
		return true;
	}
	
	private void move() {
		final var input = HuGame.getInput();
		final var camera = HuGame.getRenderer().getCamera();
		
		if (input.isKeyPressed(Key.D)) {
			playerDirection.rotateAxis((float) Math.toRadians(-ROTATION_SPEED), 0, 1, 0);
		}
		
		if (input.isKeyPressed(Key.A)) {
			playerDirection.rotateAxis((float) Math.toRadians(ROTATION_SPEED), 0, 1, 0);
		}
		
//		var angle = (float) Math.toDegrees(playerDirection.angle(new Vector3f(0, 0, -1)));
//		cubeTransform.rotation.y = (playerDirection.x < 0) ? angle : 360 - angle;
		cubeTransform.rotation.y = getAngleAroundY(playerDirection);
		
		if (input.isKeyPressed(Key.W)) {	
			playerPosition.add(playerDirection.normalize().mul(1));
		}
		
		if (input.isKeyPressed(Key.S)) {
			playerPosition.sub(playerDirection.normalize().mul(1));
		}
		
		camera.setPosition(new Vector3f(playerPosition.x, playerPosition.y + 5, playerPosition.z));
		camera.lookInDirection(playerDirection);
	}
	
	private float getAngleAroundY(Vector3f direction) {
		var smallestAngle = (float) Math.toDegrees(direction.angle(new Vector3f(0, 0, -1)));
		return (direction.x < 0) ? smallestAngle : 360 - smallestAngle;
	}
	
}
