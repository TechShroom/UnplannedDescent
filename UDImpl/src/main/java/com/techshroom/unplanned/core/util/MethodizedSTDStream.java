package com.techshroom.unplanned.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;

public class MethodizedSTDStream extends ByteArrayOutputStream {
	private static final Field ps_autoFlush;
	static {
		try {
			ps_autoFlush = PrintStream.class.getDeclaredField("autoFlush");
			ps_autoFlush.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("No autoFlush in PrintStream");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	PrintStream orig = null;
	String data = "";
	boolean lastNewline = true, autoFlush = false;

	public MethodizedSTDStream(PrintStream out) {
		this.orig = out;
		try {
			this.autoFlush = ps_autoFlush.getBoolean(out);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) {
		try {
			String str = new String(Arrays.copyOfRange(b, off, len));
			str = replaceAllButLast(str, "\\r?\\n", "$0" + getMethod());
			if (this.lastNewline) {
				this.data += getMethod() + str;
			} else {
				this.data += str;
			}
			this.lastNewline = str.endsWith("\n");
			if (this.autoFlush) {
				flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String replaceAllButLast(String str, String check, String replace) {
		String out = "";
		String sub = str.replaceAll(check + "$", "");
		out = str.replace(sub, sub.replaceAll(check, replace));
		return out;
	}

	private String getMethod() {

		StackTraceElement[] ste = new Throwable().getStackTrace();

		int i = StackTraceInfo.DUAL_INVOKING_METHOD_ZERO;
		StackTraceElement s = null;
		for (; i < ste.length; i++) {
			s = ste[i];
			// skip LUtils.print() because we want the method that called that
			// one.
			if (!s.getClassName().matches("^(java|sun)(.+?)")
					&& !(s.getClassName().equals(LUtils.class.getName()) && s
							.getMethodName().equals("print"))) {
				break;
			}
		}
		if (s == null) {
			// there is no stack!
			throw new IllegalStateException("No stack!");
		}
		String[] classsplit = s.getClassName().split("\\.");
		return "[" + classsplit[classsplit.length - 1] + "."
				+ s.getMethodName() + "(" + s.getFileName() + ":"
				+ s.getLineNumber() + ")@" + Thread.currentThread().getName()
				+ "] ";
	}

	@Override
	public void flush() throws IOException {
		this.orig.write(this.data.getBytes());
		this.orig.flush();
		this.data = "";
	}

}
