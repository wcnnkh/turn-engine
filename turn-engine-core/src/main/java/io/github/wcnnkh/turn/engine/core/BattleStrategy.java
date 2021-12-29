package io.github.wcnnkh.turn.engine.core;

/**
 * 战斗引擎策略
 * 
 * @author wcnnkh
 *
 */
public interface BattleStrategy {
	boolean isDeath(Unit unit);

	void calculation(Unit source, Unit target, Buff buff);
}
