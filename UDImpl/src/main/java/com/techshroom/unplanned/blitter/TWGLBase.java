package com.techshroom.unplanned.blitter;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Optional;

/**
 * Base class for a TryWithGL implementation. This takes care of most of the
 * implementation details, subclasses only need to produce a Runnable for the
 * start and end function or implement {@link #callStartFunction()} and
 * {@link #callEndFunction()} themselves.
 * 
 * @author Kenzie Togami
 */
@SuppressWarnings("javadoc")
class TWGLBase implements TryWithGL {

    protected final Optional<Runnable> endFuncRef, startFuncRef;
    protected final String startFuncName;
    protected final String endFuncName;

    TWGLBase(String startFunc, String endFunc) {
        this(startFunc, endFunc, null, null);
    }

    TWGLBase(String startFunc, String endFunc, Runnable startFuncRef,
            Runnable endFuncRef) {
        this.startFuncName = checkNotNull(startFunc, "startFunc");
        this.endFuncName = checkNotNull(endFunc, "endFunc");
        this.startFuncRef = Optional.ofNullable(startFuncRef);
        this.endFuncRef = Optional.ofNullable(endFuncRef);
        callStartFunction();
    }

    @Override
    public void close() throws IOException {
        callEndFunction();
    }

    protected void callStartFunction() {
        this.startFuncRef
                .orElseThrow(() -> new UnsupportedOperationException(
                        "subclass did not provide the start function to run"
                                + " and should have overriden callStartFunction"))
                .run();
    }

    protected void callEndFunction() {
        this.endFuncRef
                .orElseThrow(() -> new UnsupportedOperationException(
                        "subclass did not provide the end function to run"
                                + " and should have overriden callEndFunction"))
                .run();
    }

    @Override
    public String getStartFunctionName() {
        return this.startFuncName;
    }

    @Override
    public String getEndFunctionName() {
        return this.endFuncName;
    }

}
