package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	@Override
	public Unit clone() {
		Unit unit = new Unit();
		unit.id = this.id;
		unit.attributes = this.attributes == null ? null : new LinkedHashMap<String, BigDecimal>(this.attributes);
		unit.actions = actions == null ? null : actions.stream().map((e) -> e.clone()).collect(Collectors.toList());
		return unit;
	}
}
