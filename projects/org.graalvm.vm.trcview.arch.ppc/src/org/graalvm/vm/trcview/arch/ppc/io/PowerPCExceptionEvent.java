package org.graalvm.vm.trcview.arch.ppc.io;

import java.io.IOException;

import org.graalvm.vm.trcview.arch.io.InterruptEvent;
import org.graalvm.vm.trcview.arch.io.StepEvent;
import org.graalvm.vm.trcview.arch.ppc.PowerPC;
import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;

public class PowerPCExceptionEvent extends InterruptEvent {
	private final int type;
	private final PowerPCStepEvent step;

	protected PowerPCExceptionEvent(WordInputStream in, int tid, PowerPCStepEvent step) throws IOException {
		super(PowerPC.ID, tid);
		this.step = step;
		type = in.read32bit();
	}

	@Override
	public StepEvent getStep() {
		return step;
	}

	@Override
	protected void writeRecord(WordOutputStream out) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "<exception " + type + ">";
	}
}
