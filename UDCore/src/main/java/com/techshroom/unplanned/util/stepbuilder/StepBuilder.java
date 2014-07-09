package com.techshroom.unplanned.util.stepbuilder;

public interface StepBuilder<BUILDTYPE, NEXT extends Step<BUILDTYPE, ?>> {
	NEXT start();
}
