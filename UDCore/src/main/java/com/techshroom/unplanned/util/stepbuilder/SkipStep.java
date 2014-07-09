package com.techshroom.unplanned.util.stepbuilder;

public interface SkipStep<BUILDTYPE, NEXT extends Step<BUILDTYPE, ?>> extends
		Step<BUILDTYPE, NEXT> {
	NEXT skip();
}
