package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "对buff的抽象，buff/debuff")
@Data
public class Buff implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String id;
	@Schema(description = "buff属性")
	private Attributes attributes;
	@Schema(description = "持续多少个回合，-1表示永久")
	private long rounds;

	// TODO 对值还有更多支持：，如百分比，表达式等

	@Override
	public Buff clone() {
		Buff buff = new Buff();
		buff.id = this.id;
		buff.rounds = this.rounds;
		buff.attributes = this.attributes.clone();
		return buff;
	}

	public Buff getBuff() {
		Buff buff = new Buff();
		buff.id = this.id;
		buff.rounds = rounds;
		buff.attributes = this.attributes.getGainAttributes();
		return buff;
	}

	public Buff getDebuff() {
		Buff buff = new Buff();
		buff.id = this.id;
		buff.rounds = this.rounds;
		buff.attributes = this.attributes.getImpairmentAttribute();
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

}
