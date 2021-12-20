package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "对一个行为的抽象，可能是一个普攻或是技能等")
@Data
public class Action implements Serializable{
	private static final long serialVersionUID = 1L;
	@Schema(description = "行为名称")
	private String name;
	@Schema(description = "这个行为作用的buff")
	private List<Buff> buffs;
}
