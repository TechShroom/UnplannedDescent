package com.techshroom.unplanned.util;

import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.techshroom.unplanned.modloader.StackTraceInfo;
import com.techshroom.unplanned.modloader.Strings;
import com.techshroom.unplanned.util.imported.IconLoader;

public final class LUtils {

	/**
	 * A dummy method to load this class. Does nothing.
	 */
	public static void init() {

	}

	public static String VERSION = "1.0.0";

	public static final String LIB_NAME = "UnplannedDescent".intern(),
			SHORT_LIB_NAME = "Unplanned".intern();
	public static final String LOWER_LIB_NAME = LIB_NAME.toLowerCase().intern(),
			LOWER_SHORT_LIB_NAME = SHORT_LIB_NAME.toLowerCase().intern();

	/**
	 * <p>
	 * These logging groups are used to filter certain information. The default
	 * logging combo is INFO + WARNING + ERROR.
	 * </p>
	 * 
	 * Logging groups from lowest to highest: INFO, WARNING, DEBUG, JUNK.<br>
	 * <br>
	 * 
	 * Recommended usages:
	 * <dl>
	 * <dt>INFO</dt>
	 * <dd>- STDOUT</dd>
	 * <dt>WARNING</dt>
	 * <dd>- warnings like non-fatal OpenGL errors</dd>
	 * <dt>ERROR</dt>
	 * <dd>- STDERR</dd>
	 * <dt>DEBUG</dt>
	 * <dd>- debug info for developing</dd>
	 * <dt>JUNK</dt>
	 * <dd>- for batch-dumping information</dd>
	 * </dl>
	 */
	public static enum LoggingGroup {
		/**
		 * Standard output for users; etc.
		 */
		INFO,
		/**
		 * Non-fatal errors or suggestions for performance
		 */
		WARNING,
		/**
		 * Fatal errors
		 */
		ERROR,
		/**
		 * Debug output for developing
		 */
		DEBUG,
		/**
		 * Dump group for unloading tons of data
		 */
		JUNK;

		public static final EnumSet<LoggingGroup> ALL = EnumSet
				.allOf(LoggingGroup.class);
	}

	private static Set<LoggingGroup> logGroups = EnumSet.of(LoggingGroup.INFO,
			LoggingGroup.WARNING, LoggingGroup.ERROR);

	private static final Logger bkupLog = Logger.getLogger(SHORT_LIB_NAME
			+ " backup log");
	private static final Logger log = Logger.getLogger(LIB_NAME);

	static {
		bkupLog.setLevel(Level.ALL);
		// log setup
		try {
			String basename = LIB_NAME + ".log";
			File oldLog = new File(basename);
			if (oldLog.exists()) {
				if (!oldLog.renameTo(new File(basename + ".old"))) {
					if (!oldLog.delete()) {
						bkupLog.warning("Couldn't delete old log '"
								+ oldLog.getAbsolutePath() + "'");
					}
				}
			}
			FileHandler fh = new FileHandler(basename);
			log.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
		} catch (Exception e) {
			bkupLog.throwing(LUtils.class.getName(), "<clinit>", e);
			System.exit(-1);
		}
	}

	/**
	 * What packages are accepted for EL
	 */
	private static final String[] ACCEPT = { "com.techshroom."
			+ LOWER_SHORT_LIB_NAME + ".*" };

	/**
	 * The default system streams, before overload.
	 */
	public static PrintStream sysout = System.out, syserr = System.err;
	public static String PLATFORM_NAME = "unknown";

	static {
		/*
		 * If you need to change LWJGLUtil's system properties, do it before
		 * this comment!
		 */
		PLATFORM_NAME = LWJGLUtil.getPlatformName();
		String osName = System.getProperty("os.name");
		if (osName.startsWith("SunOS")) {
			PLATFORM_NAME = "solaris";
		}
	}

	static {
		overrideStandardStreams();
	}

	public static final String libPrintPrefix = String.format("[%s-%s]",
			LIB_NAME, LUtils.VERSION);

	/**
	 * 
	 * @deprecated Specify your group with {@link #print(String, LoggingGroup)}.
	 */
	@Deprecated
	public static void print(String msg) {
		print(msg, LoggingGroup.INFO);
	}

	public static void print(String msg, LoggingGroup group) {
		if (!logGroups.contains(group)) {
			return;
		}
		try {
			checkAccessor(ACCEPT, StackTraceInfo.getInvokingClassName());
		} catch (Exception e) {
			throw new RuntimeException(new IllegalAccessException("Not "
					+ SHORT_LIB_NAME + " trusted class"));
		}
		System.err.println("[" + group + "] " + libPrintPrefix + " " + msg);
	}

	private static void injectNatives() {
		String natives = LUtils.getELTop() + File.separator + "res"
				+ File.separator + "libs" + File.separator + "natives"
				+ File.separator + PLATFORM_NAME;
		System.setProperty("org.lwjgl.librarypath", natives);
		try {
			addLibraryPath(natives);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.err.println("Natives injected.");
	}

	/**
	 * Adds the specified path to the java library path
	 *
	 * @param pathToAdd
	 *            the path to add
	 * @throws Exception
	 */
	public static void addLibraryPath(String pathToAdd) throws Exception {
		final Field usrPathsField = ClassLoader.class
				.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		// get array of paths
		final String[] paths = (String[]) usrPathsField.get(null);

		// check if the path to add is already present
		for (String path : paths) {
			if (path.equals(pathToAdd)) {
				return;
			}
		}

		// add the new path
		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = pathToAdd;
		usrPathsField.set(null, newPaths);
	}

	private static void overrideStandardStreams() {
		System.err.println("Replacing streams with methodized...");
		MethodizedSTDStream sysout = new MethodizedSTDStream(System.out);
		System.setOut(new PrintStream(sysout));
		MethodizedSTDStream syserr = new MethodizedSTDStream(System.err);
		System.setErr(new PrintStream(syserr));
		syserr.orig.println("Finished.");
	}

	/**
	 * The top level of the game/tool
	 */
	public static String TOP_LEVEL = null;
	static {
		try {
			// reuse KCore's data
			LUtils.TOP_LEVEL = new File("").getAbsolutePath()
					.replace(File.separatorChar, '/').replaceFirst("/$", "");
			LUtils.print("Using TOP_LEVEL " + TOP_LEVEL);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * The top level of emergency landing, used to load our shaders.
	 */
	private static String EL_TOP = null;
	static {
		String tempName = LUtils.class.getPackage().getName();
		int levels = Strings.count(tempName, '.') + 2;
		tempName = LUtils.class.getResource("LUtils.class").getFile()
		// .replace('/', File.separatorChar)// .substring(1)
				.replace("%20", " ");
		for (int i = 0; i < levels; i++) {
			tempName = tempName.substring(0, tempName.lastIndexOf("/"));
		}
		LUtils.print(tempName);
		if (tempName.endsWith("!")) {
			// jar files: natives are in TOP_LEVEL
			LUtils.print("Assumed JAR launch.");
			EL_TOP = TOP_LEVEL;
		} else {
			EL_TOP = ((tempName.startsWith("/") ? "" : "/") + tempName)
					.replace("/C:/", "C:/").replace("\\C:\\", "C:\\");
		}
		LUtils.print("Using EL_TOP " + EL_TOP);

		injectNatives();
	}

	public static final int debugLevel = Integer.parseInt(System.getProperty(
			LOWER_SHORT_LIB_NAME + ".debug.level", "0"));

	static {
		System.err.println(LOWER_SHORT_LIB_NAME + ".debug.level" + ": "
				+ debugLevel);
	}

	// the range between which the "close enough" guesser in getDisplayMode uses
	private static final int WIDTH_RANGE = 300, HEIGHT_RANGE = 300;

	/**
	 * Checks for the given OpenGL version (eg. 3.0.2)
	 * 
	 * @param vers
	 *            - the wanted version
	 * @return true if the actual version is the same as or newer than the
	 *         wanted version, false otherwise
	 */
	public static boolean isVersionAvaliable(String vers) {
		String cver = getGLVer();
		if (cver.indexOf(' ') > -1) {
			cver = cver.substring(0, cver.indexOf(' '));
		}
		LUtils.print("Comparing " + cver + " to " + vers);
		String[] cver_sep = cver.split("\\.", 3);
		String[] vers_sep = vers.split("\\.", 3);
		int[] cver_sepi = new int[3];
		int[] vers_sepi = new int[3];
		int min = LUtils.minAll(cver_sep.length, vers_sep.length, 3);
		for (int i = 0; i < min; i++) {
			cver_sepi[i] = Integer.parseInt(cver_sep[i]);
			vers_sepi[i] = Integer.parseInt(vers_sep[i]);
		}
		boolean ret = cver_sepi[0] >= vers_sepi[0]
				&& cver_sepi[1] >= vers_sepi[1] && cver_sepi[2] >= vers_sepi[2];
		LUtils.print("Returning " + ret);
		return ret;
	}

	/**
	 * Gets the smallest of all the given ints
	 * 
	 * @param ints
	 *            - the set of ints to use
	 * @return the smallest int from ints
	 */
	public static int minAll(int... ints) {
		int min = Integer.MAX_VALUE;
		for (int i : ints) {
			// System.out.println("Comparing " + i + " and " + min);
			min = Math.min(min, i);
		}
		// System.out.println("Result is " + min);
		return min;
	}

	/**
	 * Check to see if access is allowed from the given class
	 * 
	 * @param accepts
	 *            - the packages to allow access from
	 * @param className
	 *            - the name of the class, including package (eg.
	 *            java.lang.String)
	 * @throws Exception
	 *             if any exceptions occur, they will be thrown
	 */
	public static void checkAccessor(String[] accepts, String className)
			throws Exception {
		boolean oneDidntThrow = false;
		for (int i = 0; i < accepts.length; i++) {
			String s = accepts[i];
			try {
				LUtils.checkAccessor(s, className);
				oneDidntThrow = true;
			} catch (Exception e) {
				if (e instanceof IllegalArgumentException) {
					accepts[i] += " --(DEBUG: This threw a IAE)--";
				}
				continue;
			}
		}
		if (oneDidntThrow) {
			return;
		}
		throw new IllegalAccessException("Access denied to " + className
				+ " because it wasn't in the following list: "
				+ BetterArrays.dump0(accepts));
	}

	/**
	 * Check to see if access is allowed from the given class
	 * 
	 * Accepts stars in the package name, such as java.lang.&#42;
	 * 
	 * @param accept
	 *            - the package to allow access from
	 * @param className
	 *            - the name of the class, including package (eg.
	 *            java.lang.String)
	 * @throws Exception
	 *             if any exceptions occur, they will be thrown
	 */
	public static void checkAccessor(String accept, String className)
			throws Exception {
		int star = accept.indexOf('*'); // Star in package name
		if (star > -1 && accept.length() == 1) {
			// If any package is accepted, it's okay.
			return;
		}
		Class.forName(className); // make sure this is a REAL class
		if (star > -1) {
			// Any packages within the specified package
			if (accept.charAt(star - 1) != '.') {
				// Weird (invalid) package ex. com.package*.malformed
				throw new IllegalArgumentException("Package malformed");
			}
			String sub = accept.substring(0, star - 1);
			if (className.startsWith(sub)) {
				return;
			}
		} else {
			// Only this package
			if (className.startsWith(accept)
					&& !className.replace(accept + ".", "").contains(".")) {
				// replacing the package name and a dot leaves no dot means
				// no other packages
				return;
			}
		}
		throw new IllegalAccessException("Access denied to " + className
				+ " because it wasn't in " + accept);
	}

	/**
	 * Attempts to get a fullscreen compatible {@link DisplayMode} for the width
	 * and height given
	 * 
	 * @param width
	 * @param height
	 * @param fullscreen
	 * @return
	 */
	public static DisplayMode getDisplayMode(int width, int height,
			boolean fullscreen) {
		ArrayList<DisplayMode> possibleExtras = new ArrayList<DisplayMode>();
		try {
			for (DisplayMode m : Display.getAvailableDisplayModes()) {
				int w = m.getWidth();
				int h = m.getHeight();
				if (m.isFullscreenCapable() || !fullscreen) {
					if (w == width) {
						if (h == height) {
							return m;
						}
					}
				}
				if (m.isFullscreenCapable()
						&& ((w < (width + WIDTH_RANGE)) && (w > (width - WIDTH_RANGE)))
						&& ((h < (height + HEIGHT_RANGE)) && (h > (height - HEIGHT_RANGE)))) {
					possibleExtras.add(m);
				}
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		print("Using non-fullscreen compatible display mode, no default ones found.");
		if (fullscreen) {
			print("Fullscreen was requested. Here are some close matches that support fullscreen: "
					+ possibleExtras);
		}
		return new DisplayMode(width, height);
	}

	/**
	 * Returns a list of fullscreen capable dimensions
	 * 
	 * @return a list of fullscreen capable dimensions
	 */
	public static Dimension[] getFullscreenCompatDimensions() {
		try {
			ArrayList<Dimension> ret = new ArrayList<Dimension>();
			for (DisplayMode m : Display.getAvailableDisplayModes()) {
				if (m.isFullscreenCapable()) {
					ret.add(new Dimension(m.getWidth(), m.getHeight()));
				}
			}
			return ret.toArray(new Dimension[ret.size()]);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		return new Dimension[0];
	}

	/**
	 * Returns a list of all dimensions built into LWJGL
	 * 
	 * @return the list of all dimensions built into LWJGL
	 */
	public static Dimension[] getDimensions() {
		try {
			ArrayList<Dimension> ret = new ArrayList<Dimension>();
			for (DisplayMode m : Display.getAvailableDisplayModes()) {
				ret.add(new Dimension(m.getWidth(), m.getHeight()));
			}
			return ret.toArray(new Dimension[ret.size()]);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		return new Dimension[0];
	}

	/**
	 * Returns a user friendly version of the fullscreen compatible dimensions
	 * 
	 * @return a user friendly version of the fullscreen compatible dimensions
	 */
	public static String[] getFullscreenCompatDimensionsSimple() {
		return LUtils.getDimensionsSimple(LUtils
				.getFullscreenCompatDimensions());
	}

	/**
	 * Returns a list of Strings representing the Dimensions given in a user
	 * friendly form
	 * 
	 * @param compat
	 *            - the Dimensions to format
	 * @return a list of Strings representing the Dimensions in the form "W x H"
	 */
	public static String[] getDimensionsSimple(Dimension[] compat) {
		Dimension[] cmpt = compat;
		String[] s = new String[cmpt.length];
		for (int i = 0; i < cmpt.length; i++) {
			Dimension d = cmpt[i];
			s[i] = String.format("%s x %s", d.width, d.height);
		}
		return s;
	}

	/**
	 * Gets a fullscreen compatible dimension from the user
	 * 
	 * @return a fullscreen compatible dimension
	 */
	public static Dimension getDimensionFromUser() {
		return LUtils.getDimensionFromUser(LUtils
				.getFullscreenCompatDimensions());
	}

	/**
	 * Gets a dimension from the user, using the given list
	 * 
	 * @param availabeDimensions
	 *            - the dimensions to choose from
	 * @return
	 */
	public static Dimension getDimensionFromUser(Dimension[] availabeDimensions) {
		Dimension[] compat = availabeDimensions;
		String[] compat_s = LUtils.getDimensionsSimple(compat);
		JFrame toClose = null;
		String ret_s = (String) JOptionPane.showInputDialog(
				toClose = new JFrame(), "Avaliable sizes:",
				"Choose a window size", JOptionPane.DEFAULT_OPTION, null,
				compat_s, compat_s[0]);
		toClose.dispose();
		toClose = null;
		if (ret_s == null) {
			return null;
		}
		return compat[Arrays.asList(compat_s).indexOf(ret_s)];
	}

	/**
	 * Turns a {@link MidiDevice.Info} list into a list of user friendly strings
	 * 
	 * @param info
	 *            - the list of MidiDevice.Infos to use
	 * @return a list of Strings representing the given Infos
	 */
	public static List<String> getInfoAsString(Info[] info) {
		List<String> out = new ArrayList<String>();
		for (Info i : info) {
			out.add(i + "" + i.getClass().getName());
		}
		return out;
	}

	/**
	 * Gets a dimension from the args, or, failing that, the user
	 * 
	 * @param normalized
	 *            - 'normalized' argument list, (eg. ["-width", "800",
	 *            "-height", "600"])
	 * @return the dimension that was found or requested
	 */
	public static Dimension getDimensionFromUserAndArgs(String[] normalized) {
		return LUtils.getDimensionFromUserAndArgs(
				LUtils.getFullscreenCompatDimensions(), normalized);
	}

	/**
	 * Gets a dimension from the args, or, failing that, the user
	 * 
	 * @param dimensions
	 *            - the array of Dimensions to use
	 * @param normalized
	 *            - 'normalized' argument list, (eg. ["-width", "800",
	 *            "-height", "600"])
	 * @return the dimension that was found or requested
	 */
	public static Dimension getDimensionFromUserAndArgs(Dimension[] dimensions,
			String[] normalized) {
		if (normalized.length >= 4) {
			List<String> strs = Arrays.asList(normalized);
			if (strs.indexOf("-width") == -1 || strs.indexOf("-height") == -1) {
			} else {
				String w = strs.get(strs.indexOf("-width") + 1);
				String h = strs.get(strs.indexOf("-height") + 1);
				if (LUtils.isInt(w) && LUtils.isInt(h)) {
					return new Dimension(Integer.parseInt(w),
							Integer.parseInt(h));
				}
			}
		}
		Dimension get = LUtils.getDimensionFromUser(dimensions);
		if (get == null) {
			get = new Dimension(600, 600);
		}

		return get;
	}

	/**
	 * Check for integer
	 * 
	 * @param test
	 *            - the String to check for integer
	 * @return if the String represents an integer
	 */
	public static boolean isInt(String test) {
		try {
			Integer.parseInt(test);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Gets the current OpenGL version
	 * 
	 * @return {@link GL11#GL_VERSION}
	 */
	public static String getGLVer() {
		return glGetString(GL_VERSION);
	}

	/**
	 * Gets the first thing in the stack that is not the given class name
	 * 
	 * @param name
	 *            - a class name
	 * @return the class that is not the given class
	 */
	public static String getFirstEntryNotThis(String name) {
		String ret = "no class found";
		int level = StackTraceInfo.INVOKING_METHOD_ZERO;
		try {
			while (StackTraceInfo.getCurrentClassName(level).equals(name)) {
				level++;
			}
			ret = StackTraceInfo.getCurrentClassName(level);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Gets an input stream from a path
	 * 
	 * @param path
	 *            - the path, must be absolute
	 * @return the input stream, or null if not possible to get an input stream
	 * @throws IOException
	 *             if there are I/O errors
	 */
	public static InputStream getInputStream(String path) throws IOException {
		LUtils.print("[Retriving InputStream for '" + path + "']");
		// Normalize to UNIX style
		path = path.replace(File.separatorChar, '/');

		InputStream result = null;

		int isType = 0; // undefined=-1;fileis=0;zipis=1;jaris=1
		List<String> pathparts = Arrays.asList(path.split("/"));
		for (String part : pathparts) {
			if (part.endsWith(".zip") || part.endsWith("jar")
					&& !(pathparts.indexOf(part) == pathparts.size() - 1)) {
				if (isType == 1) {
					isType = 2;
					break;
				} else {
					isType = 1;
					break;
				}
			}
		}

		if (isType == 0) {
			LUtils.print("Using raw file input stream");
			result = new FileInputStream(path);
		} else if (isType == 1 || isType == 2) {
			LUtils.print("Using recursive zip/jar searcher style " + isType);
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			for (int i = 0; i < pathparts.size(); i++) {
				if (pathparts.get(i).endsWith(".zip")
						|| pathparts.get(i).endsWith(".jar")) {
					LUtils.print("Adding zip/jar " + pathparts.get(i) + " at "
							+ i);
					indexes.add(i);
				}
			}
			String pathToCurrFile = "";
			for (int i = 0; i <= indexes.get(0); i++) {
				String temp_ = pathparts.get(i);
				LUtils.print(String.format("Appending '%s' to '%s'", temp_,
						pathToCurrFile));
				pathToCurrFile += temp_ + "/";
			}
			String file = pathToCurrFile.substring(0,
					pathToCurrFile.length() - 1);
			String extra = path.replace(pathToCurrFile, "");
			LUtils.print("Attempting to load from " + file);
			final ZipFile zf = new ZipFile(file);
			ZipEntry ze = zf.getEntry(extra);
			final InputStream _ = zf.getInputStream(ze);
			result = new InputStream() {
				@Override
				public int read() throws IOException {
					return _.read();
				}

				@Override
				public void close() throws IOException {
					_.close();
					zf.close();
				}
			};
		}

		LUtils.print("[Complete]");
		return result;
	}

	/**
	 * Protected method to access EL's top level
	 */
	public static String getELTop() {
		try {
			checkAccessor(ACCEPT, StackTraceInfo.getInvokingClassName());
		} catch (Exception e) {
			throw new RuntimeException(new IllegalAccessException("Not "
					+ SHORT_LIB_NAME + " trusted class"));
		}
		return EL_TOP;
	}

	public static String[] getAccepts() {
		return ACCEPT.clone();
	}

	public static void setIcon(final InputStream is) {
		ByteBuffer[] icondata = IconLoader.load(is);
		Display.setIcon(icondata);
	}
}
