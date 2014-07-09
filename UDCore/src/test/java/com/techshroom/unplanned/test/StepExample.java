package com.techshroom.unplanned.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.techshroom.unplanned.test.StepExample.Build.Builder.FirstStep;
import com.techshroom.unplanned.util.stepbuilder.FinalStep;
import com.techshroom.unplanned.util.stepbuilder.OneArgStep;
import com.techshroom.unplanned.util.stepbuilder.StepBuilder;

/**
 * A class example for the <code>util.stepbuilder</code> package.
 * 
 * @author Kenzie Togami
 */
public class StepExample {

	private static final int A_VALUE = 0;
	private static final int B_VALUE = 1;
	private static final int C_VALUE = 2;
	private static final int D_VALUE = 3;
	private static final String MESSAGE_VALUE = "0123!";

	@Test
	public void test() {
		Build build = new Build.Builder().start().a(A_VALUE).b(B_VALUE)
				.c(C_VALUE).d(D_VALUE).message(MESSAGE_VALUE).build();
		assertEquals(build.getA(), A_VALUE);
		assertEquals(build.getB(), B_VALUE);
		assertEquals(build.getC(), C_VALUE);
		assertEquals(build.getD(), D_VALUE);
		assertEquals(build.getMessage(), MESSAGE_VALUE);
	}

	public static final class Build {
		private final int a, b, c, d;
		private final String message;

		private Build(int a, int b, int c, int d, String msg) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			message = msg;
		}

		public int getA() {
			return a;
		}

		public int getB() {
			return b;
		}

		public int getC() {
			return c;
		}

		public int getD() {
			return d;
		}

		public String getMessage() {
			return message;
		}

		/**
		 * The builder
		 * 
		 * @author Kenzie Togami
		 */
		public static final class Builder implements
				StepBuilder<Build, FirstStep> {
			private int a, b, c, d;
			private String message;

			/*
			 * Some steps implemented, notice that these could technically be
			 * private.
			 * 
			 * Also, note how the overridden method must be exposed but the
			 * better method that names the variable is also there. This is a
			 * side effect of using interfaces.
			 */

			public class FirstStep implements
					OneArgStep<Build, Integer, SecondStep> {
				private FirstStep() {
				}

				@Override
				public SecondStep step(Integer arg1) {
					Builder.this.a = arg1;
					return new SecondStep();
				}

				public SecondStep a(Integer a) {
					return step(a);
				}
			}

			public class SecondStep implements
					OneArgStep<Build, Integer, ThirdStep> {
				private SecondStep() {
				}

				@Override
				public ThirdStep step(Integer arg1) {
					Builder.this.b = arg1;
					return new ThirdStep();
				}

				public ThirdStep b(Integer b) {
					return step(b);
				}
			}

			public class ThirdStep implements
					OneArgStep<Build, Integer, FourthStep> {
				private ThirdStep() {
				}

				@Override
				public FourthStep step(Integer arg1) {
					Builder.this.c = arg1;
					return new FourthStep();
				}

				public FourthStep c(Integer c) {
					return step(c);
				}
			}

			public class FourthStep implements
					OneArgStep<Build, Integer, FifthStep> {
				private FourthStep() {
				}

				@Override
				public FifthStep step(Integer arg1) {
					Builder.this.d = arg1;
					return new FifthStep();
				}

				public FifthStep d(Integer d) {
					return step(d);
				}
			}

			public class FifthStep implements
					OneArgStep<Build, String, FinalStep<Build>> {
				private FifthStep() {
				}

				@Override
				public FinalStep<Build> step(String arg1) {
					Builder.this.message = arg1;
					return new FinalStep<Build>() {

						@Override
						public Build build() {
							Build build = new Build(a, b, c, d, message);

							// normally you would check things here

							return build;
						}
					};
				}

				public FinalStep<Build> message(String message) {
					return step(message);
				}
			}

			@Override
			public FirstStep start() {
				return new FirstStep();
			}
		}
	}
}
