package io.github.wcnnkh.turn.engine.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CombatBuff extends Buff {
	private static final long serialVersionUID = 1L;
	@Schema(description = "buff的生产者")
	private Unit unit;

	public CombatBuff() {
	}

	public CombatBuff(Buff buff, Unit unit) {
		super(buff);
		this.unit = unit;
	}

	@Override
	public CombatBuff clone() {
		return new CombatBuff(super.clone(), unit.clone());
	}

	@Override
	public Attributes calculation(Attributes source) {
		if (getAttributeValueType() == AttributeValueType.OWN_PERCENTAGE) {
			return getAttributes().calculation(unit.getAttributes(), getAttributeValueType());
		}
		return super.calculation(source);
	}
}
