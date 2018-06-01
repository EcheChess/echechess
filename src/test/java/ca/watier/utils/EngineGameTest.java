package ca.watier.utils;

import ca.watier.echechess.engine.game.GameConstraints;
import ca.watier.echechess.common.tests.GameTest;

public abstract class EngineGameTest extends GameTest {
    protected static final GameConstraints CONSTRAINT_SERVICE = new GameConstraints();
}
