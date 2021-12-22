package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "属性")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attributes implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	/**
	 * 生命值
	 */
	@Schema(description = "生命值")
	private long hp;
	/**
	 * 攻击力
	 */
	@Schema(description = "攻击力")
	private long att;
	/**
	 * 防御力
	 */
	@Schema(description = "防御力")
	private long def;

	/**
	 * 是否有效
	 * 
	 * @return
	 */
	public boolean isActive() {
		return hp != 0 && att != 0 && def != 0;
	}

	@Override
	public Attributes clone() {
		Attributes attributes = new Attributes();
		attributes.hp = this.hp;
		attributes.att = this.att;
		attributes.def = this.def;
		return attributes;
	}

	public Attributes calculation(Attributes source, AttributeValueType valueType) {
		Attributes target = new Attributes();
		switch (valueType) {
		case VALUE:
			target.hp = source.hp;
			target.att = source.att;
			target.def = source.def;
			break;
		case OWN_PERCENTAGE:
		case OTHER_PERCENTAGE:
			// 万分比
			target.hp = source.hp * this.hp / 10000;
			target.att = source.att * this.att / 10000;
			target.def = source.def * this.def / 10000;
			break;
		default:
			break;
		}
		return target;
	}
}
