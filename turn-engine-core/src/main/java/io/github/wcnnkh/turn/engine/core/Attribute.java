package io.github.wcnnkh.turn.engine.core;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Attribute implements Serializable {
	private static final long serialVersionUID = 1L;
	@Schema(description = "属性名称")
	private String name;
	@Schema(description = "属性值")
	private double value;
}
