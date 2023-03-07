package io.github.wcnnkh.turn.engine.core.test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.basc.framework.json.JsonUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StopWatch;
import io.github.wcnnkh.turn.engine.core.Action;
import io.github.wcnnkh.turn.engine.core.AttributeValueType;
import io.github.wcnnkh.turn.engine.core.BattleEngine;
import io.github.wcnnkh.turn.engine.core.BattleReport;
import io.github.wcnnkh.turn.engine.core.Buff;
import io.github.wcnnkh.turn.engine.core.SimpleBattleStrategy;
import io.github.wcnnkh.turn.engine.core.Unit;

public class BattleEngineTest {
	private static Logger logger = LoggerFactory.getLogger(BattleEngineTest.class);

	/**
	 * @param id
	 * @return
	 */
	private Unit getUnit(String id) {
		Unit unit = new Unit();
		unit.setId(id);

		unit.setAttributes(createAttributes("100", "20", "20"));

		Action action = new Action();
		action.setId("普攻");
		action.setWeight(1);
		action.setBuffs(Arrays
				.asList(new Buff("普攻伤害", createAttributes("0", "20", "0"), AttributeValueType.VALUE, "att", 1, true)));
		unit.setActions(Arrays.asList(action));
		return unit;
	}

	private Map<String, BigDecimal> createAttributes(String hp, String att, String def) {
		Map<String, BigDecimal> attributes = new HashMap<String, BigDecimal>();
		// 生命值
		attributes.put("hp", new BigDecimal(hp));
		// 攻击力
		attributes.put("att", new BigDecimal(att));
		// 防御力
		attributes.put("def", new BigDecimal(def));
		return attributes;
	}

	@Test
	public void test() {
		SimpleBattleStrategy strategy = new SimpleBattleStrategy("hp", Collections.singletonMap("att", "def"));
		BattleEngine turnEngine = new BattleEngine(Arrays.asList(getUnit("左1"), getUnit("左2")),
				Arrays.asList(getUnit("右1"), getUnit("右2")), 10, strategy);

		StopWatch stopWatch = new StopWatch("回合制战斗");
		long t = System.currentTimeMillis();
		while (!turnEngine.isEnd()) {
			stopWatch.start("第" + turnEngine.getRounds() + "轮战斗");
			List<BattleReport> reports = turnEngine.battle();
			stopWatch.stop();
			logger.info("第{}轮战斗，战报：{}", turnEngine.getRounds() - 1, JsonUtils.getSupport().toJsonString(reports));
		}
		logger.info("用时{}ms, 战斗结束：{}", (System.currentTimeMillis() - t), stopWatch);
	}

}
