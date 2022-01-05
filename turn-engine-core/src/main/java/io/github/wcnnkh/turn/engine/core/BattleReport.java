package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleReport implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Battle battle;
	private Map<String, BigDecimal> changeAttributes;

	@Override
	public BattleReport clone() {
		return new BattleReport(this.battle == null ? null : battle.clone(),
				this.changeAttributes == null ? null : new LinkedHashMap<String, BigDecimal>(changeAttributes));
	}

	/**
	 * 是否是有效的战报
	 * 
	 * @return
	 */
	public boolean isActive() {
		if (changeAttributes == null || changeAttributes.isEmpty()) {
			return false;
		}

		// 是否应该判断属性是否有值
		return true;
	}
}
