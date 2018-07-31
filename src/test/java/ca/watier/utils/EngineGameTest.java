package ca.watier.utils;

import ca.watier.echechess.common.tests.GameTest;
import ca.watier.echechess.engine.factories.GameConstraintFactory;
import ca.watier.echechess.engine.interfaces.GameConstraint;

public abstract class EngineGameTest extends GameTest {
    protected static final GameConstraint CONSTRAINT_SERVICE = GameConstraintFactory.getDefaultGameConstraint();
}
