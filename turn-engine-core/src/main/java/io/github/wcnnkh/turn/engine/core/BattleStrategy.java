package io.github.wcnnkh.turn.engine.core;

/**
 * 战斗引擎策略
 * 
 * @author wcnnkh
 *
 */
public interface BattleStrategy {
	/**
	 * 这个单位是否已死亡
	 * 
	 * @param unit
	 * @return
	 */
	boolean isDeath(Unit unit);

	/**
	 * 将buff作用在目标身上
	 * 
	 * @param buff
	 * @param target
	 */
	void calculation(Buff buff, Unit targetUnit);
}
