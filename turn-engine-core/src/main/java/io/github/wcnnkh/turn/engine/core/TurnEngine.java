package io.github.wcnnkh.turn.engine.core;

import java.util.ArrayList;
import java.util.Iterator;
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
			calculation(unit.getAttributes(), buff);
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
	 * 计算(将buff作用在实例属性上)<br/>
	 * 处理任意战斗属性在此方法上扩展
	 * 
	 * @param attributes
	 * @param buff
	 */
	private void calculation(Attributes attributes, Buff buff) {
		Attributes target = buff.calculation(attributes);
		if (buff.isDebuff()) {
			// 伤害
			// 攻击-防御就是伤害
			long value = target.getAtt() - attributes.getDef();
			if (value <= 0) {
				value = 1;
			}
			attributes.setHp(attributes.getHp() - target.getHp());
			attributes.setHp(attributes.getHp() - value);
		} else {
			// 回复
			attributes.setHp(attributes.getHp() + target.getHp());
			attributes.setHp(attributes.getHp() + target.getAtt());
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
			throw new TurnEngineException("Should never get here");
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
		throw new TurnEngineException("Should never get here");
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
					if (unit.isDeath()) {
						continue;
					}

					unit.addBuff(new CombatBuff(buff, left));
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
