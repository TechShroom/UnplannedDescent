package com.techshroom.unplanned.core.util;

import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Platform;

import com.techshroom.unplanned.core.util.Logging.LoggingGroup;

public final class LUtils {

    private LUtils() {
    }

    /**
     * A dummy method to load this class. Does nothing.
     */
    public static void init() {
        // no-op
    }

    public static String VERSION = "1.0.0";

    public static final String LIB_NAME = "UnplannedDescent".intern(),
            SHORT_LIB_NAME = "Unplanned".intern();
    public static final String LOWER_LIB_NAME = LIB_NAME.toLowerCase().intern(),
            LOWER_SHORT_LIB_NAME = SHORT_LIB_NAME.toLowerCase().intern();

    private static final Logger bkupLog =
            Logger.getLogger(SHORT_LIB_NAME + " backup log");
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
     * The default system streams, before overload.
     */
    public static PrintStream sysout = System.out, syserr = System.err;
    public static String PLATFORM_NAME = "unknown";

    static {
        PLATFORM_NAME = Platform.get().getName();
        String osName = System.getProperty("os.name");
        if (osName.startsWith("SunOS")) {
            PLATFORM_NAME = "solaris";
        }
        String val = System.getProperty(LIB_NAME + ".addStackTraceToStreams");
        if (Boolean.parseBoolean(val)) {
            overrideStandardStreams();
        }
    }

    private static void injectNatives() {
        String natives = String.join(File.separator, RESOURCE_ROOT, "res",
                "libs", "natives", PLATFORM_NAME);
        System.setProperty("org.lwjgl.librarypath", natives);
        try {
            addLibraryPath(natives);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Logging.log("Natives injected.", LoggingGroup.DEBUG);
    }

    /**
     * Adds the specified path to the java library path
     *
     * @param pathToAdd
     *            the path to add
     * @throws Exception
     */
    public static void addLibraryPath(String pathToAdd) throws Exception {
        final Field usrPathsField =
                ClassLoader.class.getDeclaredField("usr_paths");
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

    @SuppressWarnings("resource")
    private static void overrideStandardStreams() {
        System.err.println("Adding tracing to standard streams...");
        MethodizedSTDStream sysout = new MethodizedSTDStream(System.out);
        System.setOut(new PrintStream(sysout));
        MethodizedSTDStream syserr = new MethodizedSTDStream(System.err);
        System.setErr(new PrintStream(syserr));
        syserr.orig.println("Finished.");
    }

    public static final String ROOT;

    static {
        String tmp = "";
        try {
            // reuse KCore's data
            tmp = new File("").getAbsolutePath()
                    .replace(File.separatorChar, '/').replaceFirst("/$", "");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        ROOT = tmp;
        Logging.log("Fake root at " + ROOT, LoggingGroup.DEBUG);
    }

    private static final String RESOURCE_ROOT;

    static {
        String tempName = LUtils.class.getPackage().getName();
        int levels = Strings.count(tempName, '.') + 2;
        tempName = LUtils.class.getResource("LUtils.class").getFile()
                .replace("%20", " ");
        for (int i = 0; i < levels; i++) {
            tempName = tempName.substring(0, tempName.lastIndexOf("/"));
        }
        if (tempName.endsWith("!")) {
            // jar files: same as root
            tempName = ROOT;
        } else {
            // non-jar: inside bin|build/classes|etc.
            tempName = ((tempName.startsWith("/") ? "" : "/") + tempName)
                    .replace("/C:/", "C:/").replace("\\C:\\", "C:\\");
        }
        RESOURCE_ROOT = tempName;
        Logging.log("Fake root for resources at " + RESOURCE_ROOT,
                LoggingGroup.DEBUG);

        injectNatives();
    }

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
        String[] cver_sep = cver.split("\\.", 3);
        String[] vers_sep = vers.split("\\.", 3);
        int[] cver_sepi = new int[3];
        int[] vers_sepi = new int[3];
        int min = Maths.min(cver_sep.length, vers_sep.length, 3);
        for (int i = 0; i < min; i++) {
            cver_sepi[i] = Integer.parseInt(cver_sep[i]);
            vers_sepi[i] = Integer.parseInt(vers_sep[i]);
        }
        boolean ret = cver_sepi[0] >= vers_sepi[0]
                && cver_sepi[1] >= vers_sepi[1] && cver_sepi[2] >= vers_sepi[2];
        return ret;
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
                + Arrays.toString(accepts));
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
    @SuppressWarnings("resource")
    public static InputStream getInputStream(String path) throws IOException {
        // Normalize to UNIX style
        path = path.replace(File.separatorChar, '/');

        InputStream result = null;

        int streamType = 0; // file=0;zip=1;nested-zip=2
        List<String> pathparts = Arrays.asList(path.split("/"));
        for (String part : pathparts) {
            if (part.endsWith(".zip") || part.endsWith("jar")
                    && !(pathparts.indexOf(part) == pathparts.size() - 1)) {
                if (streamType < 2) {
                    streamType++;
                } else {
                    break;
                }
            }
        }

        if (streamType == 0) {
            result = new FileInputStream(path);
        } else if (streamType == 1 || streamType == 2) {
            ArrayList<Integer> indexes = new ArrayList<Integer>();
            for (int i = 0; i < pathparts.size(); i++) {
                if (pathparts.get(i).endsWith(".zip")
                        || pathparts.get(i).endsWith(".jar")) {
                    indexes.add(i);
                }
            }
            String pathToCurrFile = "";
            for (int i = 0; i <= indexes.get(0); i++) {
                String temp_ = pathparts.get(i);
                pathToCurrFile += temp_ + "/";
            }
            String file =
                    pathToCurrFile.substring(0, pathToCurrFile.length() - 1);
            String extra = path.replace(pathToCurrFile, "");
            final ZipFile zf = new ZipFile(file);
            ZipEntry ze = zf.getEntry(extra);
            final InputStream zfIn = zf.getInputStream(ze);
            result = new InputStream() {

                @Override
                public int read() throws IOException {
                    return zfIn.read();
                }

                @Override
                public void close() throws IOException {
                    zfIn.close();
                    zf.close();
                }
            };
        }

        return result;
    }

}
