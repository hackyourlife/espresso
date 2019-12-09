package org.graalvm.vm.trcview.arch.ppc.io;

import java.io.IOException;

import org.graalvm.vm.trcview.arch.io.CpuState;
import org.graalvm.vm.trcview.arch.ppc.PowerPC;
import org.graalvm.vm.trcview.arch.ppc.disasm.Cr;
import org.graalvm.vm.util.HexFormatter;
import org.graalvm.vm.util.io.WordInputStream;
import org.graalvm.vm.util.io.WordOutputStream;

public class PowerPCCpuState extends CpuState {
	private final int insn;
	private final int[] gpr;
	private final int lr;
	private final int ctr;
	private final int pc;
	private final int cr;
	private final int xer;
	private final int fpscr;
	private final long step;

	public PowerPCCpuState(WordInputStream in, int tid) throws IOException {
		super(PowerPC.ID, tid);
		insn = in.read32bit();
		gpr = new int[32];
		for(int i = 0; i < 32; i++) {
			gpr[i] = in.read32bit();
		}
		for(int i = 0; i < 32; i++) {
			in.read64bit();
		}
		lr = in.read32bit();
		ctr = in.read32bit();
		pc = in.read32bit();
		cr = in.read32bit();
		xer = in.read32bit();
		fpscr = in.read32bit();
		step = in.read64bit();
	}

	@Override
	public long getStep() {
		return step;
	}

	@Override
	public long getPC() {
		return Integer.toUnsignedLong(pc);
	}

	@Override
	public long get(String name) {
		switch(name) {
		case "r0":
			return gpr[0];
		case "r1":
			return gpr[1];
		case "r2":
			return gpr[2];
		case "r3":
			return gpr[3];
		case "r4":
			return gpr[4];
		case "r5":
			return gpr[5];
		case "r6":
			return gpr[6];
		case "r7":
			return gpr[7];
		case "r8":
			return gpr[8];
		case "r9":
			return gpr[9];
		case "r10":
			return gpr[10];
		case "r11":
			return gpr[11];
		case "r12":
			return gpr[12];
		case "r13":
			return gpr[13];
		case "r14":
			return gpr[14];
		case "r15":
			return gpr[15];
		case "r16":
			return gpr[16];
		case "r17":
			return gpr[17];
		case "r18":
			return gpr[18];
		case "r19":
			return gpr[19];
		case "r20":
			return gpr[20];
		case "r21":
			return gpr[21];
		case "r22":
			return gpr[22];
		case "r23":
			return gpr[23];
		case "r24":
			return gpr[24];
		case "r25":
			return gpr[25];
		case "r26":
			return gpr[26];
		case "r27":
			return gpr[27];
		case "r28":
			return gpr[28];
		case "r29":
			return gpr[29];
		case "r30":
			return gpr[30];
		case "r31":
			return gpr[31];
		case "insn":
			return Integer.toUnsignedLong(insn);
		case "cr":
			return Integer.toUnsignedLong(cr);
		case "xer":
			return Integer.toUnsignedLong(xer);
		case "fpscr":
			return Integer.toUnsignedLong(fpscr);
		case "ctr":
			return Integer.toUnsignedLong(ctr);
		case "lr":
			return Integer.toUnsignedLong(lr);
		case "pc":
			return Integer.toUnsignedLong(pc);
		case "sp":
			return gpr[1];
		default:
			throw new IllegalArgumentException("unknown register " + name);
		}
	}

	public int getInstruction() {
		return insn;
	}

	public int getCR() {
		return cr;
	}

	public int getCTR() {
		return ctr;
	}

	public int getGPR(int reg) {
		return gpr[reg];
	}

	@Override
	protected void writeRecord(WordOutputStream out) throws IOException {
		// TODO Auto-generated method stub
	}

	private static boolean test(int x, int i) {
		return (x & (1 << (3 - i))) != 0;
	}

	private String strcr(int i) {
		StringBuilder buf = new StringBuilder(3);
		Cr field = new Cr(i);
		int val = field.get(cr);
		if(test(val, Cr.EQ)) {
			buf.append('E');
		}
		if(test(val, Cr.SO)) {
			buf.append('O');
		}
		if(test(val, Cr.GT)) {
			buf.append('G');
		}
		if(test(val, Cr.LT)) {
			buf.append('L');
		}
		if(buf.length() == 0) {
			return " - ";
		} else if(buf.length() == 1) {
			return " " + buf + " ";
		} else if(buf.length() == 2) {
			return " " + buf;
		} else {
			return buf.toString();
		}
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("CIP ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(pc), 8));
		buf.append("   LR ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(lr), 8));
		buf.append("    CTR ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(ctr), 8));
		buf.append("   XER ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(xer), 8));
		buf.append('\n');
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 4; j++) {
				int r = i * 4 + j;
				buf.append("GPR");
				if(r < 10) {
					buf.append('0');
					buf.append((char) (r + '0'));
				} else {
					buf.append(r);
				}
				buf.append('=');
				buf.append(HexFormatter.tohex(Integer.toUnsignedLong(gpr[r]), 8));
				if(j < 3) {
					buf.append(' ');
				}
			}
			buf.append('\n');
		}
		buf.append("CR ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(cr), 8));
		buf.append("  [");
		for(int i = 0; i < 8; i++) {
			buf.append(strcr(i));
		}
		buf.append("]\n");
		return buf.toString();
	}
}
