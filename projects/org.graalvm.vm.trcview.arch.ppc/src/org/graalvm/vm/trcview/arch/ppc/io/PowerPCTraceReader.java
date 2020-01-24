package org.graalvm.vm.trcview.arch.ppc.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

import org.graalvm.vm.posix.api.mem.Mman;
import org.graalvm.vm.trcview.arch.io.ArchTraceReader;
import org.graalvm.vm.trcview.arch.io.Event;
import org.graalvm.vm.trcview.arch.io.InstructionType;
import org.graalvm.vm.trcview.arch.io.MemoryDumpEvent;
import org.graalvm.vm.trcview.arch.io.MemoryEvent;
import org.graalvm.vm.trcview.arch.io.MmapEvent;
import org.graalvm.vm.trcview.arch.ppc.disasm.InstructionFormat;
import org.graalvm.vm.trcview.arch.ppc.disasm.Opcode;
import org.graalvm.vm.trcview.net.protocol.IO;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.io.BEInputStream;
import org.graalvm.vm.util.io.WordInputStream;

public class PowerPCTraceReader extends ArchTraceReader {
	public static final int MAGIC_STEP = 0x53544550;
	public static final int MAGIC_TRAP = 0x54524150;
	public static final int MAGIC_MEMR = 0x4D454D52;
	public static final int MAGIC_MEMW = 0x4D454D57;
	public static final int MAGIC_DUMP = 0x44554D50;

	private final WordInputStream in;
	private PowerPCStepEvent lastStep;
	private int init = 0;

	// used to detect mtsrr0/rfi/mtsrr0/rfi sequences
	private static final InstructionFormat insn = new InstructionFormat();
	private Deque<Integer> trapstack = new ArrayDeque<>();

	private int fullstate = 0;

	public PowerPCTraceReader(InputStream in) {
		this(new BEInputStream(in));
	}

	public PowerPCTraceReader(WordInputStream in) {
		this.in = in;
		lastStep = null;
	}

	private static MmapEvent mmap(int start, int end, String name) {
		return new MmapEvent(0, Integer.toUnsignedLong(start), Integer.toUnsignedLong(end - start + 1),
				Mman.PROT_READ | Mman.PROT_WRITE | Mman.PROT_EXEC,
				Mman.MAP_FIXED | Mman.MAP_PRIVATE | Mman.MAP_ANONYMOUS, -1, 0, name,
				Integer.toUnsignedLong(start), null);
	}

	private void checkTrap(PowerPCStepEvent step) {
		int opcd = step.getState().getInstruction();
		switch(insn.OPCD.get(opcd)) {
		case Opcode.CR_OPS:
			switch(insn.XO_1.get(opcd)) {
			case Opcode.XO_RFI:
				if(trapstack.isEmpty()) {
					PowerPCCpuState state = step.getState();
					if(state.getSRR0() == state.getLR()) {
						step.type = InstructionType.RET;
					} else {
						step.type = InstructionType.JMP_INDIRECT;
					}
				} else if(step.getState().getSRR0() == trapstack.peek()) {
					trapstack.pop();
				} else {
					step.type = InstructionType.JMP_INDIRECT;
				}
				return;
			}
			break;
		}
	}

	@Override
	public Event read() throws IOException {
		switch(init) {
		case 0:
			init++;
			return mmap(0x80000000, 0x817FFFFF, "MEM1 (Cached)");
		case 1:
			init++;
			return mmap(0xC0000000, 0xC17FFFFF, "MEM1 (Uncached)");
		case 2:
			init++;
			return mmap(0x90000000, 0x93FFFFFF, "MEM2 (Cached)");
		case 3:
			init++;
			return mmap(0xD0000000, 0xD3FFFFFF, "MEM2 (Uncached)");
		case 4:
			init++;
			return mmap(0xCD000000, 0xCD008000, "Hollywood Registers");
		}

		int magic;
		try {
			magic = in.read32bit();
		} catch(EOFException e) {
			return null;
		}
		switch(magic) {
		case MAGIC_STEP:
			lastStep = new PowerPCStepEvent(in, 0, lastStep, (fullstate % 500) == 0);
			fullstate++;
			fullstate %= 500;
			checkTrap(lastStep);
			return lastStep;
		case MAGIC_TRAP:
			PowerPCExceptionEvent trap = new PowerPCExceptionEvent(in, 0, lastStep);
			trapstack.push(trap.getSRR0());
			return trap;
		case MAGIC_MEMR: {
			long address = Integer.toUnsignedLong(in.read32bit());
			long value = Integer.toUnsignedLong(in.read32bit());
			byte size = (byte) in.read8bit();
			return new MemoryEvent(true, 0, address, size, false, value);
		}
		case MAGIC_MEMW: {
			long address = Integer.toUnsignedLong(in.read32bit());
			long value = Integer.toUnsignedLong(in.read32bit());
			byte size = (byte) in.read8bit();
			return new MemoryEvent(true, 0, address, size, true, value);
		}
		case MAGIC_DUMP: {
			long address = Integer.toUnsignedLong(in.read32bit());
			byte[] data = IO.readArray(in);
			return new MemoryDumpEvent(0, address, data);
		}
		default:
			throw new IOException("unknown record: " + HexFormatter.tohex(magic, 8) +
					" [position " + tell() + "]");
		}
	}

	@Override
	public long tell() {
		return in.tell();
	}
}
