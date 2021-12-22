package io.github.wcnnkh.turn.engine.core;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "属性值类型")
public enum AttributeValueType {
	/**
	 * 值
	 */
	@Schema(description = "值")
	VALUE,
	/**
	 * 已方百分比
	 */
	@Schema(description = "生产者百分比")
	OWN_PERCENTAGE,

	/**
	 * 作用方百分比
	 */
	@Schema(description = "消费者百分比")
	OTHER_PERCENTAGE
}
