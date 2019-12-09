package org.graalvm.vm.trcview.arch.ppc.io;

import java.io.IOException;

import org.graalvm.vm.trcview.arch.io.Event;
import org.graalvm.vm.trcview.arch.io.EventParser;
import org.graalvm.vm.util.io.WordInputStream;

public class PowerPCEventParser extends EventParser {
	@Override
	public <T extends Event> T parse(WordInputStream in, byte id, int tid) throws IOException {
		return null;
	}
}
