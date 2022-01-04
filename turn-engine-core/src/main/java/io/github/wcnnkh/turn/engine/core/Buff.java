package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	@Schema(description = "buff属性数值")
	private Map<String, BigDecimal> attributes;
	@Schema(description = "属性值类型")
	private AttributeValueType attributeValueType;
	@Schema(description = "这个buff作用到的属性名称")
	private String attributeName;
	@Schema(description = "持续多少个回合，-1表示永久")
	private long rounds;
	@Schema(description = "是否是debuff")
	private boolean debuff;

	@Override
	public Buff clone() {
		Buff buff = new Buff();
		buff.id = this.id;
		buff.rounds = this.rounds;
		buff.attributeValueType = this.attributeValueType;
		buff.attributes = this.attributes == null ? null : new LinkedHashMap<String, BigDecimal>(this.attributes);
		buff.debuff = this.debuff;
		return buff;
	}

	/**
	 * 是否有效
	 * 
	 * @return
	 */
	public boolean isActive() {
		if (rounds == 0) {
			return false;
		}

		if (attributes == null || attributes.isEmpty()) {
			return false;
		}

		for (Entry<String, BigDecimal> entry : attributes.entrySet()) {
			if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
				return true;
			}
		}
		return false;
	}

	public Map<String, BigDecimal> calculationPercentage(Map<String, BigDecimal> sourceAttributes) {
		if (sourceAttributes == null || sourceAttributes.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, BigDecimal> attributes = new HashMap<String, BigDecimal>();
		for (Entry<String, BigDecimal> entry : this.attributes.entrySet()) {
			BigDecimal source = sourceAttributes.get(entry.getKey());
			if (source == null || source.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			attributes.put(entry.getKey(), source.multiply(entry.getValue()));
		}
		return attributes;
	}
}
