package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "对buff的抽象，buff/debuff")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Buff implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String id;
	@Schema(description = "buff属性")
	private Attributes attributes;
	@Schema(description = "属性值类型")
	private AttributeValueType attributeValueType;
	@Schema(description = "持续多少个回合，-1表示永久")
	private long rounds;
	@Schema(description = "是否是debuff")
	private boolean debuff;

	public Buff(Buff buff) {
		this.id = buff.id;
		this.attributes = buff.attributes;
		this.attributeValueType = buff.attributeValueType;
		this.rounds = buff.rounds;
		this.debuff = buff.debuff;
	}

	@Override
	public Buff clone() {
		Buff buff = new Buff();
		buff.id = this.id;
		buff.rounds = this.rounds;
		buff.attributeValueType = this.attributeValueType;
		buff.attributes = this.attributes.clone();
		buff.debuff = this.debuff;
		return buff;
	}

	/**
	 * 是否有效
	 * 
	 * @return
	 */
	public boolean isActive() {
		return rounds != 0 && attributes.isActive();
	}

	public Attributes calculation(Attributes source) {
		if (attributeValueType == AttributeValueType.OWN_PERCENTAGE) {
			throw new TurnEngineException("必须是CombatBuff类型");
		}
		return attributes.calculation(source, attributeValueType);
	}
}
