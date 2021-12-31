package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Battle implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Unit producer;
	private Action action;
	private Unit consumer;
	private Buff buff;
	private int rounds;

	@Override
	public Battle clone() {
		return new Battle(this.producer == null ? null : this.producer.clone(),
				this.action == null ? null : this.action.clone(), this.consumer == null ? null : this.consumer.clone(),
				this.buff == null ? null : this.buff.clone(), this.rounds);
	}

	public boolean isActive() {
		return buff != null && buff.isActive();
	}
}
