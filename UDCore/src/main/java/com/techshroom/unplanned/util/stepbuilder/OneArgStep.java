package com.techshroom.unplanned.util.stepbuilder;

public interface OneArgStep<BUILDTYPE, ARG1, NEXT extends Step<BUILDTYPE, ?>>
		extends Step<BUILDTYPE, NEXT> {
	NEXT step(ARG1 arg1);
}
