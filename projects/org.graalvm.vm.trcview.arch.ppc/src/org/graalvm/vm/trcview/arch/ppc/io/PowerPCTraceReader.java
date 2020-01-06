package org.graalvm.vm.trcview.arch.ppc.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.graalvm.vm.trcview.arch.io.ArchTraceReader;
import org.graalvm.vm.trcview.arch.io.Event;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.io.BEInputStream;
import org.graalvm.vm.util.io.WordInputStream;

public class PowerPCTraceReader extends ArchTraceReader {
	public static final int MAGIC_STEP = 0x53544550;
	public static final int MAGIC_TRAP = 0x54524150;

	private final WordInputStream in;
	private PowerPCStepEvent lastStep;

	public PowerPCTraceReader(InputStream in) {
		this(new BEInputStream(in));
	}

	public PowerPCTraceReader(WordInputStream in) {
		this.in = in;
		lastStep = null;
	}

	@Override
	public Event read() throws IOException {
		int magic;
		try {
			magic = in.read32bit();
		} catch(EOFException e) {
			return null;
		}
		switch(magic) {
		case MAGIC_STEP:
			lastStep = new PowerPCStepEvent(in, 0);
			return lastStep;
		case MAGIC_TRAP:
			return new PowerPCExceptionEvent(in, 0, lastStep);
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
