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
	 * 计算战斗
	 * 
	 * @param battle
	 * @param targetUnit
	 * @return
	 */
	BattleReport calculation(Battle battle, BattleUnit targetUnit);
}
