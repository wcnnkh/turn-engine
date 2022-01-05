package io.github.wcnnkh.turn.engine.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.util.Assert;
import lombok.ToString;

/**
 * 回合制战斗引擎
 * 
 * @author wcnnkh
 *
 */
@ToString
public class BattleEngine {
	private final BattleUnit[] leftUnits;
	private final BattleUnit[] rightUnits;
	private int rounds;
	private final BattleStrategy strategy;
	private int order;
	/**
	 * 战斗的最大轮次
	 */
	private final int maxRounds;

	public BattleEngine(List<Unit> leftUnits, List<Unit> rightUnits, int maxRounds, BattleStrategy strategy) {
		Assert.requiredArgument(strategy != null, "strategy");
		this.leftUnits = leftUnits == null ? new BattleUnit[0]
				: leftUnits.stream().map((e) -> new BattleUnit(e, null)).toArray(BattleUnit[]::new);
		this.rightUnits = rightUnits == null ? new BattleUnit[0]
				: rightUnits.stream().map((e) -> new BattleUnit(e, null)).toArray(BattleUnit[]::new);
		this.maxRounds = maxRounds;
		this.strategy = strategy;
	}

	/**
	 * 是否全部死亡
	 * 
	 * @param units
	 * @return
	 */
	private boolean isDeathAll(BattleUnit[] units) {
		for (BattleUnit unit : leftUnits) {
			// 只要有一个没死就没结束
			if (!strategy.isDeath(unit.getUnit())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取战斗结果
	 * 
	 * @return 1表示右边赢了(左边死完了)， -1表示左边赢了(右边死完了), 0表示还未死完
	 */
	public int getResult() {
		if (isDeathAll(leftUnits)) {
			// 左边全死了
			return 1;
		}

		if (isDeathAll(rightUnits)) {
			// 右边全死了
			return -1;
		}

		// 还没死完
		return 0;
	}

	/**
	 * 是否已结束
	 * 
	 * @return
	 */
	public boolean isEnd() {
		if (this.rounds > maxRounds) {
			return true;
		}

		return getResult() != 0;
	}

	public int getRounds() {
		return rounds;
	}

	/**
	 * 进行一轮战斗
	 * 
	 * @return
	 */
	public List<BattleReport> battle() {
		if (isEnd()) {
			throw new EngineException("战斗已结束");
		}

		// 左边打右边
		order = 0;
		for (BattleUnit left : this.leftUnits) {
			order++;
			battle(left, this.leftUnits, this.rightUnits);
			if (isEnd()) {
				break;
			}
		}

		// 右边打左边
		for (BattleUnit right : this.rightUnits) {
			order++;
			battle(right, this.rightUnits, this.leftUnits);
			if (isEnd()) {
				break;
			}
		}

		List<BattleReport> reports = new ArrayList<BattleReport>();
		for (BattleUnit unit : this.leftUnits) {
			nextRound(reports, unit);
		}

		for (BattleUnit unit : this.rightUnits) {
			nextRound(reports, unit);
		}
		this.rounds++;
		return reports;
	}

	/**
	 * 进行一轮的数值运算
	 * 
	 * @param unit
	 */
	private void nextRound(Collection<BattleReport> reports, BattleUnit unit) {
		if (unit.getBattles() == null) {
			return;
		}
		Iterator<Battle> iterator = unit.getBattles().iterator();
		while (iterator.hasNext()) {
			Battle battle = iterator.next();
			if (!battle.isActive()) {
				iterator.remove();
				continue;
			}

			// 计算
			BattleReport report = strategy.calculation(battle);
			if (report != null && report.isActive()) {
				reports.add(report.clone());
			}

			// -1表示永久
			if (battle.getBuff().getRounds() > 0) {
				battle.getBuff().setRounds(battle.getBuff().getRounds() - 1);
			}

			if (!battle.isActive()) {
				iterator.remove();
			}
		}
	}

	/**
	 * 选择一个行为
	 * 
	 * @param left
	 * @param leftUntis
	 * @param rightUnits
	 * @return
	 */
	private Action getActions(BattleUnit left, BattleUnit[] leftUntis, BattleUnit[] rightUnits) {
		List<Action> actions = left.getUnit().getActions().stream().filter((e) -> e.getAnger() >= e.getMaxAnger())
				.collect(Collectors.toList());
		if (actions.isEmpty()) {
			// 没有可用的技能
			throw new EngineException("Should never get here");
		}

		// 只有一个行为，直接使用
		if (actions.size() == 1) {
			Action action = actions.get(0);
			action.setAnger(0);
			return action;
		}

		long totalWeight = 0;
		for (Action action : actions) {
			// 可以使用
			totalWeight += action.getWeight();
		}

		// 是否应该使用科学计算？
		long randomWeight = (long) (Math.random() * (totalWeight - 1));
		long weight = 0;
		for (Action action : actions) {
			weight += action.getWeight();
			// 如果随机的权重在这个范围内
			if (randomWeight <= weight) {
				action.setAnger(0);
				return action;
			}
		}
		// 不可能到这里，除非没有技能
		throw new EngineException("Should never get here");
	}

	/**
	 * 进行一次战斗
	 * 
	 * @param left       进行战斗的人
	 * @param leftUntis  友军
	 * @param rightUnits 敌军
	 * @return
	 */
	private void battle(BattleUnit left, BattleUnit[] leftUnits, BattleUnit[] rightUnits) {
		// 选择一个行为
		Action action = getActions(left, leftUnits, rightUnits);
		left.getUnit().getActions().forEach((e) -> {
			if (e.getAnger() < e.getMaxAnger()) {
				e.setAnger(e.getAnger() + e.getAngerIncrease());
			}
		});

		for (Buff buff : action.getBuffs()) {
			if (buff.isActive()) {
				for (BattleUnit unit : buff.isDebuff() ? rightUnits : leftUnits) {
					if (strategy.isDeath(unit.getUnit())) {
						// 已经死了，无法施加buff
						continue;
					}

					Battle battle = new Battle(this.rounds, this.order, left.getUnit(), action, unit.getUnit(), buff.clone());
					unit.addBattle(battle);
					if (!action.isAoe()) {
						break;
					}
				}
			}
		}
	}
}
