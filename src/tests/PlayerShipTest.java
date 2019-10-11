package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dstealth.tappydefender.gameobjects.PlayerShip;

public class PlayerShipTest {
	PlayerShip ps;

	@Before
	public void setUp() throws Exception {
		this.ps = new PlayerShip(0, 0);
	}

	@Test
	public void testPlayerShipSpeed() {
		assertEquals(1, this.ps.getSpeed());
	}
	
	@Test
	public void testPlayerShipStrength() {
		assertEquals(2, this.ps.getShieldStrength());
	}
}
