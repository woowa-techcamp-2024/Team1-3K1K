package camp.woowak.lab.infra.aop;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {
	private CustomSpringELParser() {
	}

	public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
		SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();

		for (int parmeterIdx = 0; parmeterIdx < parameterNames.length; parmeterIdx++) {
			context.setVariable(parameterNames[parmeterIdx], args[parmeterIdx]);
		}
		
		return spelExpressionParser.parseExpression(key).getValue(context, Object.class);
	}
}
