package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 战斗单位
 * 
 * @author wcnnkh
 *
 */
@Schema(description = "战斗单位")
@Data
public class Unit implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String id;
	private Attributes attributes;
	@Schema(description = "这个战斗单位拥有的行为，可能是技能、普攻等")
	private List<Action> actions;
	@Schema(description = "身上剩余的buffs")
	private List<Buff> buffs;

	public void addBuff(Buff buff) {
		// 会频繁操作，使用使用linked
		if (buffs == null) {
			buffs = new LinkedList<>();
		}
		buffs.add(buff);
	}

	@Override
	public Unit clone() {
		Unit unit = new Unit();
		unit.id = this.id;
		unit.attributes = this.attributes.clone();
		unit.actions = actions == null ? null : actions.stream().map((e) -> e.clone()).collect(Collectors.toList());
		unit.buffs = buffs == null ? null
				: this.buffs.stream().map((e) -> e.clone()).collect(Collectors.toCollection(() -> new LinkedList<>()));
		return unit;
	}

	/**
	 * 是否已死亡
	 * 
	 * @return
	 */
	public boolean isDeath() {
		return attributes.getHp() <= 0;
	}

	/**
	 * 下一轮(将buff作用到属性上并清理buff)
	 */
	public void nextRound() {
		if (buffs == null) {
			return;
		}
		Iterator<Buff> iterator = buffs.iterator();
		while (iterator.hasNext()) {
			Buff buff = iterator.next();
			if (!buff.isActive()) {
				iterator.remove();
				continue;
			}

			attributes.merge(buff.getAttributes());
			if (buff.getRounds() > 0) {
				buff.setRounds(buff.getRounds() - 1);
			}

			if (!buff.isActive()) {
				iterator.remove();
			}
		}
	}
}
