package org.graalvm.vm.trcview.arch.ppc.io;

import org.graalvm.vm.trcview.arch.io.CpuState;
import org.graalvm.vm.trcview.arch.ppc.PowerPC;
import org.graalvm.vm.trcview.arch.ppc.disasm.Cr;
import org.graalvm.vm.util.HexFormatter;

public abstract class PowerPCCpuState extends CpuState {
	protected PowerPCCpuState(int tid) {
		super(PowerPC.ID, tid);
	}

	public abstract int getInstruction();

	public abstract int getLR();

	public abstract int getCR();

	public abstract int getCTR();

	public abstract int getXER();

	public abstract int getFPSCR();

	public abstract int getGPR(int reg);

	public abstract int getSRR0();

	public abstract int getSRR1();

	@Override
	public long get(String name) {
		switch(name) {
		case "r0":
			return Integer.toUnsignedLong(getGPR(0));
		case "r1":
			return Integer.toUnsignedLong(getGPR(1));
		case "r2":
			return Integer.toUnsignedLong(getGPR(2));
		case "r3":
			return Integer.toUnsignedLong(getGPR(3));
		case "r4":
			return Integer.toUnsignedLong(getGPR(4));
		case "r5":
			return Integer.toUnsignedLong(getGPR(5));
		case "r6":
			return Integer.toUnsignedLong(getGPR(6));
		case "r7":
			return Integer.toUnsignedLong(getGPR(7));
		case "r8":
			return Integer.toUnsignedLong(getGPR(8));
		case "r9":
			return Integer.toUnsignedLong(getGPR(9));
		case "r10":
			return Integer.toUnsignedLong(getGPR(10));
		case "r11":
			return Integer.toUnsignedLong(getGPR(11));
		case "r12":
			return Integer.toUnsignedLong(getGPR(12));
		case "r13":
			return Integer.toUnsignedLong(getGPR(13));
		case "r14":
			return Integer.toUnsignedLong(getGPR(14));
		case "r15":
			return Integer.toUnsignedLong(getGPR(15));
		case "r16":
			return Integer.toUnsignedLong(getGPR(16));
		case "r17":
			return Integer.toUnsignedLong(getGPR(17));
		case "r18":
			return Integer.toUnsignedLong(getGPR(18));
		case "r19":
			return Integer.toUnsignedLong(getGPR(19));
		case "r20":
			return Integer.toUnsignedLong(getGPR(20));
		case "r21":
			return Integer.toUnsignedLong(getGPR(21));
		case "r22":
			return Integer.toUnsignedLong(getGPR(22));
		case "r23":
			return Integer.toUnsignedLong(getGPR(23));
		case "r24":
			return Integer.toUnsignedLong(getGPR(24));
		case "r25":
			return Integer.toUnsignedLong(getGPR(25));
		case "r26":
			return Integer.toUnsignedLong(getGPR(26));
		case "r27":
			return Integer.toUnsignedLong(getGPR(27));
		case "r28":
			return Integer.toUnsignedLong(getGPR(28));
		case "r29":
			return Integer.toUnsignedLong(getGPR(29));
		case "r30":
			return Integer.toUnsignedLong(getGPR(30));
		case "r31":
			return Integer.toUnsignedLong(getGPR(31));
		case "insn":
			return Integer.toUnsignedLong(getInstruction());
		case "cr":
			return Integer.toUnsignedLong(getCR());
		case "xer":
			return Integer.toUnsignedLong(getXER());
		case "fpscr":
			return Integer.toUnsignedLong(getFPSCR());
		case "ctr":
			return Integer.toUnsignedLong(getCTR());
		case "lr":
			return Integer.toUnsignedLong(getLR());
		case "srr0":
			return Integer.toUnsignedLong(getSRR0());
		case "srr1":
			return Integer.toUnsignedLong(getSRR1());
		case "pc":
			return Integer.toUnsignedLong((int) getPC());
		case "sp":
			return Integer.toUnsignedLong(getGPR(1));
		default:
			throw new IllegalArgumentException("unknown register " + name);
		}
	}

	private static boolean test(int x, int i) {
		return (x & (1 << (3 - i))) != 0;
	}

	private String strcr(int i) {
		StringBuilder buf = new StringBuilder(3);
		Cr field = new Cr(i);
		int val = field.get(getCR());
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
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong((int) getPC()), 8));
		buf.append("   LR ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(getLR()), 8));
		buf.append("    CTR ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(getCTR()), 8));
		buf.append("   XER ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(getXER()), 8));
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
				buf.append(HexFormatter.tohex(Integer.toUnsignedLong(getGPR(r)), 8));
				if(j < 3) {
					buf.append(' ');
				}
			}
			buf.append('\n');
		}
		buf.append("CR ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(getCR()), 8));
		buf.append("  [");
		for(int i = 0; i < 8; i++) {
			buf.append(strcr(i));
		}
		buf.append("]\n\n");
		buf.append("SRR0 ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(getSRR0()), 8));
		buf.append(" SRR1 ");
		buf.append(HexFormatter.tohex(Integer.toUnsignedLong(getSRR1()), 8));
		buf.append('\n');
		return buf.toString();
	}
}
