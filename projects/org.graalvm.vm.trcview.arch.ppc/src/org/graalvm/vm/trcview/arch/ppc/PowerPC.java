package org.graalvm.vm.trcview.arch.ppc;

import java.io.InputStream;

import org.graalvm.vm.posix.elf.Elf;
import org.graalvm.vm.trcview.arch.Architecture;
import org.graalvm.vm.trcview.arch.io.ArchTraceReader;
import org.graalvm.vm.trcview.arch.io.EventParser;
import org.graalvm.vm.trcview.arch.io.StepFormat;
import org.graalvm.vm.trcview.arch.ppc.decode.PowerPCCallDecoder;
import org.graalvm.vm.trcview.arch.ppc.decode.PowerPCSyscallDecoder;
import org.graalvm.vm.trcview.arch.ppc.io.PowerPCEventParser;
import org.graalvm.vm.trcview.arch.ppc.io.PowerPCTraceReader;
import org.graalvm.vm.trcview.decode.CallDecoder;
import org.graalvm.vm.trcview.decode.SyscallDecoder;

public class PowerPC extends Architecture {
	public static final short ID = Elf.EM_PPC;
	public static final StepFormat FORMAT = new StepFormat(StepFormat.NUMBERFMT_HEX, 8, 8, 1, true);

	private static final SyscallDecoder syscallDecoder = new PowerPCSyscallDecoder();
	private static final CallDecoder callDecoder = new PowerPCCallDecoder();
	private static final EventParser eventParser = new PowerPCEventParser();

	@Override
	public short getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "ppc32";
	}

	@Override
	public String getDescription() {
		return "Nintendo Wii U (Espresso microprocessor)";
	}

	@Override
	public ArchTraceReader getTraceReader(InputStream in) {
		return new PowerPCTraceReader(in);
	}

	@Override
	public EventParser getEventParser() {
		return eventParser;
	}

	@Override
	public SyscallDecoder getSyscallDecoder() {
		return syscallDecoder;
	}

	@Override
	public CallDecoder getCallDecoder() {
		return callDecoder;
	}

	@Override
	public int getTabSize() {
		return 10;
	}

	@Override
	public StepFormat getFormat() {
		return FORMAT;
	}

	@Override
	public boolean isSystemLevel() {
		return true;
	}

	@Override
	public boolean isStackedTraps() {
		return false;
	}
}
