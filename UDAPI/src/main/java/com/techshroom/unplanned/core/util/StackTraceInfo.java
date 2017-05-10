/*
 * This file is part of UnplannedDescent, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshoom.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.techshroom.unplanned.core.util;

import java.lang.reflect.Method;

/* Utility class: Getting the name of the current executing method 
 * http://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method
 * 
 * Provides: 
 * 
 *      getCurrentClassName()
 *      getCurrentMethodName()
 *      getCurrentFileName()
 * 
 *      getInvokingClassName()
 *      getInvokingMethodName()
 *      getInvokingFileName()
 *
 * Nb. Using StackTrace's to get this info is expensive. There are more optimised ways to obtain
 * method names. See other stackoverflow posts eg. http://stackoverflow.com/questions/421280/in-java-how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection/2924426#2924426
 *
 * 29/09/2012 (lem) - added methods to return (1) fully qualified names and (2) invoking class/method names
 */

public class StackTraceInfo {

    /* (Lifted from virgo47's stackoverflow answer) */
    /**
     * Index of the StackTraceInfo methods in the stack
     */
    public static final int CLIENT_CODE_STACK_INDEX = 1;

    /**
     * Makes the offset zero for the calling method, by adding the required
     * offset
     */
    public static final int CURRENT_METHOD_ZERO = 0;

    /**
     * Makes the offset zero for the invoking method, by adding the required
     * offset
     */
    public static final int INVOKING_METHOD_ZERO = 1;

    /**
     * Makes the offset zero for the method that called the method that called
     * the current method, by adding the required offset
     */
    public static final int DUAL_INVOKING_METHOD_ZERO =
            INVOKING_METHOD_ZERO + INVOKING_METHOD_ZERO;
    private static Method m;

    static {
        try {
            m = Throwable.class.getDeclaredMethod("getStackTraceElement",
                    int.class);
            m.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentMethodName() {
        return StackTraceInfo.getCurrentMethodName(INVOKING_METHOD_ZERO);
    }

    public static String getCurrentMethodName(int offset) {
        Throwable here = new Throwable();
        StackTraceElement ste = null;
        try {
            ste = (StackTraceElement) m.invoke(here,
                    CLIENT_CODE_STACK_INDEX + offset);
        } catch (Exception e) {
            ste = here.getStackTrace()[CLIENT_CODE_STACK_INDEX + offset];
        }
        return ste.getMethodName();
    }

    public static String getCurrentClassName() {
        return StackTraceInfo.getCurrentClassName(INVOKING_METHOD_ZERO);
    }

    public static String getCurrentClassName(int offset) {
        Throwable here = new Throwable();
        StackTraceElement ste = null;
        try {
            ste = (StackTraceElement) m.invoke(here,
                    CLIENT_CODE_STACK_INDEX + offset);
        } catch (Exception e) {
            ste = here.getStackTrace()[CLIENT_CODE_STACK_INDEX + offset];
        }
        return ste.getClassName();
    }

    public static String getCurrentFileName() {
        return StackTraceInfo.getCurrentFileName(INVOKING_METHOD_ZERO);
    }

    public static String getCurrentFileName(int offset) {

        Throwable here = new Throwable();
        StackTraceElement ste = null;
        try {
            ste = (StackTraceElement) m.invoke(here,
                    CLIENT_CODE_STACK_INDEX + offset);
        } catch (Exception e) {
            ste = here.getStackTrace()[CLIENT_CODE_STACK_INDEX + offset];
        }
        String filename = ste.getFileName();
        int lineNumber = ste.getLineNumber();

        return filename + ":" + lineNumber;
    }

    public static String getInvokingMethodName() {
        return StackTraceInfo.getInvokingMethodName(DUAL_INVOKING_METHOD_ZERO);
    }

    public static String getInvokingMethodName(int offset) {
        return getCurrentMethodName(offset + INVOKING_METHOD_ZERO);
    }

    public static String getInvokingClassName() {
        return StackTraceInfo.getInvokingClassName(DUAL_INVOKING_METHOD_ZERO);
    }

    public static String getInvokingClassName(int offset) {
        return getCurrentClassName(offset + INVOKING_METHOD_ZERO);
    }

    public static String getInvokingFileName() {
        return StackTraceInfo.getInvokingFileName(DUAL_INVOKING_METHOD_ZERO);
    }

    public static String getInvokingFileName(int offset) {
        return getCurrentFileName(offset + INVOKING_METHOD_ZERO);
    }

    public static String getCurrentMethodNameFqn() {
        return StackTraceInfo.getCurrentMethodNameFqn(INVOKING_METHOD_ZERO);
    }

    public static String getCurrentMethodNameFqn(int offset) {
        String currentClassName =
                getCurrentClassName(offset + INVOKING_METHOD_ZERO);
        String currentMethodName =
                getCurrentMethodName(offset + INVOKING_METHOD_ZERO);

        return currentClassName + "." + currentMethodName;
    }

    public static String getCurrentFileNameFqn() {
        String CurrentMethodNameFqn =
                StackTraceInfo.getCurrentMethodNameFqn(INVOKING_METHOD_ZERO);
        String currentFileName =
                StackTraceInfo.getCurrentFileName(INVOKING_METHOD_ZERO);

        return CurrentMethodNameFqn + "(" + currentFileName + ")";
    }

    public static String getInvokingMethodNameFqn() {
        return StackTraceInfo
                .getInvokingMethodNameFqn(DUAL_INVOKING_METHOD_ZERO);
    }

    public static String getInvokingMethodNameFqn(int offset) {
        String invokingClassName =
                getInvokingClassName(offset + INVOKING_METHOD_ZERO);
        String invokingMethodName =
                getInvokingMethodName(offset + INVOKING_METHOD_ZERO);

        return invokingClassName + "." + invokingMethodName;
    }

    public static String getInvokingFileNameFqn() {
        String invokingMethodNameFqn = StackTraceInfo
                .getInvokingMethodNameFqn(DUAL_INVOKING_METHOD_ZERO);
        String invokingFileName =
                StackTraceInfo.getInvokingFileName(DUAL_INVOKING_METHOD_ZERO);

        return invokingMethodNameFqn + "(" + invokingFileName + ")";
    }
}
