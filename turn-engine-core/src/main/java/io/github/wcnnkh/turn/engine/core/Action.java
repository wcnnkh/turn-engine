package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "对一个行为的抽象，可能是一个普攻或是技能等")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	@Schema(description = "行为id")
	private String id;
	@Schema(description = "这个行为作用的buff")
	private List<Buff> buffs;
	@Schema(description = "当前怒气")
	private int anger;
	@Schema(description = "怒气增长")
	private int angerIncrease;
	@Schema(description = "最大怒气，当达到最大怒气时说明可以使用")
	private int maxAnger;
	@Schema(description = "权重, 自动释放权重")
	private int weight;
	@Schema(description = "是否是群体行为")
	private boolean aoe;

	@Override
	public Action clone() {
		Action action = new Action();
		action.id = this.id;
		action.buffs = this.buffs == null ? null
				: this.buffs.stream().map((e) -> e.clone()).collect(Collectors.toList());
		action.anger = this.anger;
		action.maxAnger = this.maxAnger;
		action.angerIncrease = this.angerIncrease;
		action.weight = this.weight;
		action.aoe = this.aoe;
		return action;
	}
}
