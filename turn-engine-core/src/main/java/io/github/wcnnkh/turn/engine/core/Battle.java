package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Battle implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	@Schema(description = "战斗发生的轮次")
	private int rounds;
	@Schema(description = "战斗发生的顺序, rounds和order相同时视为同一次战斗")
	private int order;
	private Unit producer;
	private Action action;
	private Unit consumer;
	private Buff buff;

	@Override
	public Battle clone() {
		return new Battle(this.rounds, this.order, this.producer == null ? null : this.producer.clone(),
				this.action == null ? null : this.action.clone(), this.consumer == null ? null : this.consumer.clone(),
				this.buff == null ? null : this.buff.clone());
	}

	public boolean isActive() {
		return buff != null && buff.isActive();
	}

	/**
	 * 计算buff的实际属性
	 * 
	 * @param consumer
	 * @return
	 */
	public Map<String, BigDecimal> calculation() {
		if (this.buff.getAttributes() == null || this.buff.getAttributes().isEmpty()) {
			return Collections.emptyMap();
		}

		switch (this.buff.getAttributeValueType()) {
		case VALUE:
			return new HashMap<String, BigDecimal>(this.buff.getAttributes());
		case CONSUMER_PERCENTAGE:
			return this.buff.calculationPercentage(this.consumer.getAttributes());
		case PRODUCER_PERCENTAGE:
			if (producer == null) {
				// 属性值类型人
				throw new EngineException("Attribute value type is OWN_PERCENTAGE for producer cannot be empty");
			}
			return this.buff.calculationPercentage(producer.getAttributes());
		default:
			break;
		}
		throw new EngineException("Unsupported attribute value type: " + this.buff.getAttributeValueType());
	}
}
