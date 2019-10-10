package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dstealth.tappydefender.SpaceDust;

public class SpaceDustTest {
	private static final int SCREEN_WIDTH = 400;
	private static final int SCREEN_HEIGHT = 350;
	SpaceDust sd;

	@Before
	public void setUp() throws Exception {
		this.sd = new SpaceDust(SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Test
	public void testNothingToTest() {
		assertTrue(true);
	}
}
