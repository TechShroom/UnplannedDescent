package com.techshroom.unplanned.util.stepbuilder;

/**
 * Note: ignore the fact this extends Step with a NEXT of FinalStep. It's the
 * final step, how else should it proceed?
 */
public interface FinalStep<RES> extends Step<RES, FinalStep<RES>> {
	RES build();
}
