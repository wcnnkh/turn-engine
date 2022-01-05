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
	@Schema(description = "身上剩余的战斗")
	private List<Battle> battles;

	public void addBattle(Battle battle) {
		// 会频繁操作，使用使用linked
		if (this.battles == null) {
			this.battles = new LinkedList<>();
		}
		this.battles.add(battle);
	}
}
