package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleUnit implements Serializable {
	private static final long serialVersionUID = 1L;
	private Unit unit;
	@Schema(description = "身上剩余的buffs")
	private List<Battle> battles;

	public void addBuff(Battle buff) {
		// 会频繁操作，使用使用linked
		if (battles == null) {
			battles = new LinkedList<>();
		}
		battles.add(buff);
	}
}
