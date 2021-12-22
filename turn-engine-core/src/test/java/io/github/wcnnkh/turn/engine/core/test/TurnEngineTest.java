package io.github.wcnnkh.turn.engine.core.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StopWatch;
import io.github.wcnnkh.turn.engine.core.Action;
import io.github.wcnnkh.turn.engine.core.AttributeValueType;
import io.github.wcnnkh.turn.engine.core.Attributes;
import io.github.wcnnkh.turn.engine.core.BattlefieldReport;
import io.github.wcnnkh.turn.engine.core.Buff;
import io.github.wcnnkh.turn.engine.core.TurnEngine;
import io.github.wcnnkh.turn.engine.core.Unit;

public class TurnEngineTest {
	private static Logger logger = LoggerFactory.getLogger(TurnEngineTest.class);

	private Unit getUnit(String id) {
		Unit unit = new Unit();
		unit.setId(id);

		unit.setAttributes(new Attributes(100, 20, 10));

		Action action = new Action();
		action.setId("普攻");
		action.setBuffs(Arrays.asList(new Buff("普攻伤害", new Attributes(0, 20, 0), AttributeValueType.VALUE, 1, true)));
		unit.setActions(Arrays.asList(action));
		return unit;
	}

	@Test
	public void test() {
		StopWatch stopWatch = new StopWatch("回合制战斗");
		TurnEngine turnEngine = new TurnEngine(Arrays.asList(getUnit("左1"), getUnit("左2")),
				Arrays.asList(getUnit("左1"), getUnit("左2")));
		while (!turnEngine.isEnd()) {
			stopWatch.start("第" + turnEngine.getRounds() + "轮战斗");
			List<BattlefieldReport> reports = turnEngine.battle();
			logger.info("第{}轮战斗，战报：{}", turnEngine.getRounds(), reports);
			stopWatch.stop();
		}
		logger.info("战斗结束：{}", stopWatch);
	}

}
