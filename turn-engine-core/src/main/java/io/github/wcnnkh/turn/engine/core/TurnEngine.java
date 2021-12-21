package io.github.wcnnkh.turn.engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.ToString;

/**
 * 回合制战斗引擎
 * 
 * @author shuchaowen
 *
 */
@ToString
public class TurnEngine {
	private final Unit[] leftUnits;
	private final Unit[] rightUnits;
	private int rounds;

	public TurnEngine(List<Unit> leftUnits, List<Unit> rightUnits) {
		this.leftUnits = leftUnits == null ? new Unit[0]
				: leftUnits.stream().map((e) -> e.clone()).toArray(Unit[]::new);
		this.rightUnits = rightUnits == null ? new Unit[0]
				: rightUnits.stream().map((e) -> e.clone()).toArray(Unit[]::new);
	}

	/**
	 * 是否已结束
	 * 
	 * @return
	 */
	public boolean isEnd() {
		for (Unit unit : leftUnits) {
			if (!unit.isDeath()) {
				return false;
			}
		}

		for (Unit unit : rightUnits) {
			if (!unit.isDeath()) {
				return false;
			}
		}
		return true;
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
			throw new TurnEngineException("战斗已结束");
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
			unit.nextRound();
		}

		for (Unit unit : this.rightUnits) {
			unit.nextRound();
		}

		this.rounds++;
		return reports.stream().map((e) -> e.clone()).collect(Collectors.toList());
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
			throw new IllegalStateException("Should never get here");
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
		throw new IllegalStateException("Should never get here");
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
				e.setAnger(e.getAnger() + 1);
			}
		});

		List<Unit> attackers = new ArrayList<Unit>();
		for (Buff actionBuff : action.getBuffs()) {
			Buff buff = actionBuff.getBuff();
			Buff debuff = actionBuff.getDebuff();
			if (action.isAoe()) {
				if (buff.isActive()) {
					for (Unit unit : leftUnits) {
						unit.addBuff(buff);
						attackers.add(unit);
					}
				}

				if (debuff.isActive()) {
					for (Unit unit : rightUnits) {
						unit.addBuff(debuff);
						attackers.add(unit);
					}
				}
			} else {
				if (buff.isActive()) {
					for (Unit unit : leftUnits) {
						if (unit.isDeath()) {
							continue;
						}

						unit.addBuff(buff);
						attackers.add(unit);
					}
				}

				if (debuff.isActive()) {
					for (Unit unit : rightUnits) {
						if (unit.isDeath()) {
							continue;
						}

						unit.addBuff(debuff);
						attackers.add(unit);
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
