package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dstealth.tappydefender.EnemyShip;

public class EnemyShipTest {
	private static final int SCREEN_WIDTH = 400;
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 5 - MIN_SPEED + 1;
	EnemyShip es;

	@Before
	public void setUp() throws Exception {
		this.es = new EnemyShip(null, SCREEN_WIDTH, 350, 1);
	}

	@Test
	public void testEnemyShipX() {
		System.out.println(this.es.x);
		assertEquals(SCREEN_WIDTH, this.es.x);
	}
	
	@Test
	public void testEnemyShipSpeed() {
		assertEquals(true, this.es.speed >= MIN_SPEED && this.es.speed <= MAX_SPEED);
	}

}