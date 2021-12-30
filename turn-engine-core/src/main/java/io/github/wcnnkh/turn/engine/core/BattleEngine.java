package io.github.wcnnkh.turn.engine.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.util.Assert;
import lombok.ToString;

/**
 * 回合制战斗引擎
 * 
 * @author shuchaowen
 *
 */
@ToString
public class BattleEngine {
	private final Unit[] leftUnits;
	private final Unit[] rightUnits;
	private int rounds;
	private final BattleStrategy strategy;

	public BattleEngine(List<Unit> leftUnits, List<Unit> rightUnits, BattleStrategy strategy) {
		Assert.requiredArgument(strategy != null, "strategy");
		this.leftUnits = leftUnits == null ? new Unit[0]
				: leftUnits.stream().map((e) -> e.clone()).toArray(Unit[]::new);
		this.rightUnits = rightUnits == null ? new Unit[0]
				: rightUnits.stream().map((e) -> e.clone()).toArray(Unit[]::new);
		this.strategy = strategy;
	}

	/**
	 * 是否全部死亡
	 * 
	 * @param units
	 * @return
	 */
	private boolean isDeathAll(Unit[] units) {
		for (Unit unit : leftUnits) {
			// 只要有一个没死就没结束
			if (!strategy.isDeath(unit)) {
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
	public List<BattlefieldReport> battle() {
		if (isEnd()) {
			throw new EngineException("战斗已结束");
		}

		List<BattlefieldReport> reports = new ArrayList<BattlefieldReport>();
		// 左边打右边
		for (Unit left : this.leftUnits) {
			reports.add(battle(left, this.leftUnits, this.rightUnits));
			if (isEnd()) {
				break;
			}
		}

		// 右边打左边
		for (Unit right : this.rightUnits) {
			reports.add(battle(right, this.rightUnits, this.leftUnits));
			if (isEnd()) {
				break;
			}
		}

		for (Unit unit : this.leftUnits) {
			nextRound(unit);
		}

		for (Unit unit : this.rightUnits) {
			nextRound(unit);
		}
		this.rounds++;
		return reports.stream().map((e) -> e.clone()).collect(Collectors.toList());
	}

	/**
	 * 进行一轮的数值运算
	 * 
	 * @param unit
	 */
	private void nextRound(Unit unit) {
		if (unit.getBuffs() == null) {
			return;
		}
		Iterator<Buff> iterator = unit.getBuffs().iterator();
		while (iterator.hasNext()) {
			Buff buff = iterator.next();
			if (!buff.isActive()) {
				iterator.remove();
				continue;
			}

			// 计算
			strategy.calculation(buff, unit);
			// -1表示永久
			if (buff.getRounds() > 0) {
				buff.setRounds(buff.getRounds() - 1);
			}

			if (!buff.isActive()) {
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
	private Action getActions(Unit left, Unit[] leftUntis, Unit[] rightUnits) {
		List<Action> actions = left.getActions().stream().filter((e) -> e.getAnger() >= e.getMaxAnger())
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
	private BattlefieldReport battle(Unit left, Unit[] leftUnits, Unit[] rightUnits) {
		// 选择一个行为
		Action action = getActions(left, leftUnits, rightUnits);
		left.getActions().forEach((e) -> {
			if (e.getAnger() < e.getMaxAnger()) {
				e.setAnger(e.getAnger() + e.getAngerIncrease());
			}
		});

		List<Unit> attackers = new ArrayList<Unit>();
		for (Buff buff : action.getBuffs()) {
			if (buff.isActive()) {
				for (Unit unit : buff.isDebuff() ? rightUnits : leftUnits) {
					if (strategy.isDeath(unit)) {
						// 已经死了，无法施加buff
						continue;
					}

					Buff b = buff.clone();
					b.setProducer(left);
					unit.addBuff(b);
					attackers.add(unit);

					if (!action.isAoe()) {
						break;
					}
				}
			}
		}

		BattlefieldReport report = new BattlefieldReport();
		report.setLeftUnit(left);
		report.setRightUnits(attackers);
		report.setAction(action);
		report.setRounds(this.rounds);
		return report;
	}
}
