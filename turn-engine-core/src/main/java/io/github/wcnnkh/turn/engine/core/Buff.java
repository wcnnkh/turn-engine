package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "对buff的抽象，buff/debuff")
@Data
public class Buff implements Serializable{
	private static final long serialVersionUID = 1L;
	@Schema(description = "持续多少个回合，0表示永久")
	private long rounds;
	@Schema(description = "buff名称")
	private String name;
	@Schema(description = "buff数值")
	private double value;
	
	//TODO 对值还有更多支持：，如百分比，表达式等
}
