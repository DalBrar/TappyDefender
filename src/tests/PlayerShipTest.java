package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dstealth.tappydefender.PlayerShip;

public class PlayerShipTest {
	PlayerShip ps;

	@Before
	public void setUp() throws Exception {
		this.ps = new PlayerShip(null, 0, 0);
	}

	@Test
	public void testPlayerShipX() {
		assertEquals(25, this.ps.x);
	}
	
	@Test
	public void testPlayerShipY() {
		assertEquals(50, this.ps.y);
	}
	
	@Test
	public void testPlayerShipSpeed() {
		assertEquals(1, this.ps.speed);
	}
	
	@Test
	public void testPlayerShipStrength() {
		assertEquals(2, this.ps.strength);
	}
	
	@Test
	public void testPlayerShipBoosting() {
		assertEquals(false, this.ps.boosting);
	}

}
