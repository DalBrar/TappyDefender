package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	PlayerShipTest.class,
	EnemyShipTest.class,
	SpaceDustTest.class
})
public class GameObjectsTestSuite {}
