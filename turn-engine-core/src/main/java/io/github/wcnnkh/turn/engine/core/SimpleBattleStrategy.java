package io.github.wcnnkh.turn.engine.core;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.logger.Logger;

/**
 * 简单的战斗策略
 * 
 * @author wcnnkh
 *
 */
public class SimpleBattleStrategy implements BattleStrategy {
	private static Logger logger = LoggerFactory.getLogger(SimpleBattleStrategy.class);

	/**
	 * 生命属性名称
	 */
	private final String hpAttributeName;
	/**
	 * 攻击和防御的映射
	 */
	private final Map<String, String> attackToDefenseAttributeNameMap;

	public SimpleBattleStrategy(String hpAttributeName, Map<String, String> attackToDefenseAttributeNameMap) {
		Assert.requiredArgument(StringUtils.hasText(hpAttributeName), "hpAttributeName");
		Assert.requiredArgument(!CollectionUtils.isEmpty(attackToDefenseAttributeNameMap),
				"attackToDefenseAttributeNameMap");
		this.hpAttributeName = hpAttributeName;
		this.attackToDefenseAttributeNameMap = attackToDefenseAttributeNameMap;
	}

	@Override
	public boolean isDeath(Unit unit) {
		Map<String, BigDecimal> attributes = unit.getAttributes();
		if (attributes == null || attributes.isEmpty()) {
			return true;
		}

		BigDecimal value = attributes.get(hpAttributeName);
		if (value != null && value.compareTo(BigDecimal.ZERO) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * buff计算
	 */
	@Override
	public void calculation(Buff buff, Unit targetUnit) {
		Map<String, BigDecimal> buffAttributes = buff.calculation(targetUnit);
		if (logger.isDebugEnabled()) {
			logger.debug("Consumer[{}], Buff[{}] calculation: {}", targetUnit, buff, buffAttributes);
		}

		if (buffAttributes == null || buffAttributes.isEmpty()) {
			// 没的属性变更
			return;
		}

		BigDecimal total = BigDecimal.ZERO;
		for (Entry<String, BigDecimal> entry : buffAttributes.entrySet()) {
			total = total.add(entry.getValue());
		}

		if (total.compareTo(BigDecimal.ZERO) == 0) {
			// 没有属性变更
			return;
		}

		attributeCalculation(buff, targetUnit, total);
	}

	/**
	 * 属性计算
	 * 
	 * @param buff
	 * @param targetUnit
	 * @param attributeValue
	 */
	protected void attributeCalculation(Buff buff, Unit targetUnit, BigDecimal attributeValue) {
		Map<String, BigDecimal> consumerAttributes = targetUnit.getAttributes();
		if (consumerAttributes == null) {
			// 战斗对象没有属性，可能是已经死了或无用的对象
			return;
		}

		if (buff.isDebuff()) {
			// 如果是伤害
			BigDecimal hp = consumerAttributes.get(hpAttributeName);
			if (hp == null) {
				return;
			}

			String defenseAttributeName = attackToDefenseAttributeNameMap.get(buff.getAttributeName());
			if (defenseAttributeName == null) {
				// 找不到伤害对应的防御属性直接扣除
				hp = hp.subtract(attributeValue);
			} else {
				BigDecimal defense = consumerAttributes.get(defenseAttributeName);
				if (defense == null) {
					defense = BigDecimal.ZERO;
				}

				// 简单的进行攻击和防御的换算
				hp = hp.subtract(attributeValue.subtract(defense).abs().max(BigDecimal.ONE));
			}
			consumerAttributes.put(hpAttributeName, hp);
		} else {
			// 如果是增益直接修改对应属性
			BigDecimal value = consumerAttributes.get(buff.getAttributeName());
			if (value == null) {
				value = BigDecimal.ZERO;
			}

			value = value.add(attributeValue);
			consumerAttributes.put(buff.getAttributeName(), value);
		}
	}
}
