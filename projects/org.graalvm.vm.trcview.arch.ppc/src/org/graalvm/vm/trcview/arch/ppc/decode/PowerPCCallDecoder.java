package org.graalvm.vm.trcview.arch.ppc.decode;

import org.graalvm.vm.trcview.analysis.type.Function;
import org.graalvm.vm.trcview.analysis.type.Prototype;
import org.graalvm.vm.trcview.analysis.type.Type;
import org.graalvm.vm.trcview.arch.io.CpuState;
import org.graalvm.vm.trcview.arch.ppc.io.PowerPCCpuState;
import org.graalvm.vm.trcview.decode.CallDecoder;
import org.graalvm.vm.trcview.expression.EvaluationException;
import org.graalvm.vm.trcview.expression.ExpressionContext;
import org.graalvm.vm.trcview.net.TraceAnalyzer;

public class PowerPCCallDecoder extends CallDecoder {
	private static long getRegister(PowerPCCpuState state, int reg) {
		if(reg <= 7) {
			return state.getGPR(reg + 3);
		} else {
			return 0;
		}
	}

	public static String decode(Function function, PowerPCCpuState state, PowerPCCpuState nextState,
			TraceAnalyzer trc) {
		StringBuilder buf = new StringBuilder(function.getName());
		buf.append('(');
		Prototype prototype = function.getPrototype();
		for(int i = 0, arg = 0; i < prototype.args.size(); i++) {
			Type type = prototype.args.get(i);
			long val;
			if(type.getExpression() != null) {
				try {
					ExpressionContext ctx = new ExpressionContext(state, trc);
					val = type.getExpression().evaluate(ctx);
				} catch(EvaluationException e) {
					val = 0;
				}
			} else {
				val = getRegister(state, arg++);
			}
			if(i > 0) {
				buf.append(", ");
			}
			buf.append(str(type, val, state, trc));
		}
		buf.append(')');
		if(nextState != null) {
			long retval;
			if(prototype.expr != null) {
				try {
					ExpressionContext ctx = new ExpressionContext(nextState, trc);
					retval = prototype.expr.evaluate(ctx);
				} catch(EvaluationException e) {
					retval = 0;
				}
			} else {
				retval = nextState.getGPR(3);
			}
			String s = str(prototype.returnType, retval, nextState, trc);
			if(s.length() > 0) {
				buf.append(" = ");
				buf.append(s);
			}
		}
		return buf.toString();
	}

	@Override
	public String decode(Function function, CpuState state, CpuState nextState, TraceAnalyzer trc) {
		if(!(state instanceof PowerPCCpuState) ||
				(nextState != null && !(nextState instanceof PowerPCCpuState))) {
			return null;
		}
		return decode(function, (PowerPCCpuState) state, (PowerPCCpuState) nextState, trc);
	}
}
