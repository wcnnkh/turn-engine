package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.basc.framework.util.CollectionFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 战斗单位
 * 
 * @author wcnnkh
 *
 */
@Schema(description = "战斗单位")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Unit implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String id;
	@Schema(description = "属性")
	private Map<String, BigDecimal> attributes;
	@Schema(description = "这个战斗单位拥有的行为，可能是技能、普攻等")
	private List<Action> actions;
	@Schema(description = "身上剩余的buffs")
	private List<Buff> buffs;

	public void addBuff(Buff buff) {
		// 会频繁操作，使用使用linked
		if (buffs == null) {
			buffs = new LinkedList<>();
		}
		buffs.add(buff);
	}

	@Override
	public Unit clone() {
		Unit unit = new Unit();
		unit.id = this.id;
		unit.attributes = this.attributes == null ? null : CollectionFactory.clone(this.attributes);
		unit.actions = actions == null ? null : actions.stream().map((e) -> e.clone()).collect(Collectors.toList());
		unit.buffs = buffs == null ? null
				: this.buffs.stream().map((e) -> e.clone()).collect(Collectors.toCollection(() -> new LinkedList<>()));
		return unit;
	}
}
