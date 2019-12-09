package org.graalvm.vm.trcview.arch.ppc.io;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.trcview.arch.io.CpuState;
import org.graalvm.vm.trcview.arch.io.InstructionType;
import org.graalvm.vm.trcview.arch.io.StepEvent;
import org.graalvm.vm.trcview.arch.io.StepFormat;
import org.graalvm.vm.trcview.arch.ppc.PowerPC;
import org.graalvm.vm.trcview.arch.ppc.disasm.PowerPCDisassembler;
import org.graalvm.vm.util.io.Endianess;
import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;

public class PowerPCStepEvent extends StepEvent {
	private final PowerPCCpuState state;

	protected PowerPCStepEvent(WordInputStream in, int tid) throws IOException {
		super(PowerPC.ID, tid);
		state = new PowerPCCpuState(in, tid);
	}

	@Override
	public byte[] getMachinecode() {
		byte[] opcd = new byte[4];
		Endianess.set32bitBE(opcd, state.getInstruction());
		return opcd;
	}

	@Override
	public String getDisassembly() {
		String[] code = getDisassemblyComponents();
		if(code.length == 1) {
			return code[0];
		} else {
			return code[0] + "\t" + Stream.of(code).skip(1).collect(Collectors.joining(",\t"));
		}
	}

	@Override
	public String[] getDisassemblyComponents() {
		return PowerPCDisassembler.disassemble((int) state.getPC(), state.getInstruction());
	}

	@Override
	public String getMnemonic() {
		return getDisassemblyComponents()[0];
	}

	@Override
	public long getPC() {
		return state.getPC();
	}

	@Override
	public InstructionType getType() {
		return PowerPCDisassembler.getType(state, state.getInstruction());
	}

	@Override
	public long getStep() {
		return state.getStep();
	}

	@Override
	public CpuState getState() {
		return state;
	}

	@Override
	public StepFormat getFormat() {
		return PowerPC.FORMAT;
	}

	@Override
	protected void writeRecord(WordOutputStream out) throws IOException {
		// TODO Auto-generated method stub
	}
}
