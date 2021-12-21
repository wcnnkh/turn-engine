package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "战报")
@Data
public class BattlefieldReport implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	@Schema(description = "第几轮")
	private int rounds;
	@Schema(description = "攻击者")
	private Unit leftUnit;
	@Schema(description = "行为")
	private Action action;
	@Schema(description = "被攻击者")
	private List<Unit> rightUnits;

	@Override
	public BattlefieldReport clone() {
		BattlefieldReport report = new BattlefieldReport();
		report.rounds = this.rounds;
		report.leftUnit = this.leftUnit.clone();
		report.action = this.action.clone();
		report.rightUnits = this.rightUnits.stream().map((e) -> e.clone()).collect(Collectors.toList());
		return report;
	}
}
