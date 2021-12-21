package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "属性")
@Data
public class Attributes implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	@Schema(description = "生命值")
	private long hp;
	@Schema(description = "攻击力")
	private long att;
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

	public Attributes getGainAttributes() {
		Attributes attributes = new Attributes();
		attributes.hp = this.hp > 0 ? this.hp : 0;
		attributes.att = this.att > 0 ? this.att : 0;
		attributes.def = this.def > 0 ? this.def : 0;
		return attributes;
	}

	public Attributes getImpairmentAttribute() {
		Attributes attributes = new Attributes();
		attributes.hp = this.hp < 0 ? this.hp : 0;
		attributes.att = this.att < 0 ? this.att : 0;
		attributes.def = this.def < 0 ? this.def : 0;
		return attributes;
	}

	/**
	 * 合并属性
	 * 
	 * @param attributes
	 */
	public void merge(Attributes attributes) {
		this.hp += attributes.hp;
		this.att += attributes.att;
		this.def += attributes.def;
	}
}
