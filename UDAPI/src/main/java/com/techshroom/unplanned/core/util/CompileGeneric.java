package com.techshroom.unplanned.core.util;

/**
 * A class for forcing generics on methods.
 */
public class CompileGeneric<T> {

    private static final CompileGeneric<?> NON_CLASS_INSTANCE =
            new CompileGeneric<>();

    public static final class ClassCompileGeneric<T> extends CompileGeneric<T> {

        private final Class<?> rawClass;

        private ClassCompileGeneric(Class<?> rawClass) {
            this.rawClass = rawClass;
        }

        public Class<?> getRawClass() {
            return this.rawClass;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((this.rawClass == null) ? 0 : this.rawClass.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ClassCompileGeneric<?> other = (ClassCompileGeneric<?>) obj;
            if (this.rawClass == null) {
                if (other.rawClass != null)
                    return false;
            } else if (!this.rawClass.equals(other.rawClass))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return super.toString() + "[rawClass=" + this.rawClass.getName()
                    + "]";
        }

    }

    public static <T> ClassCompileGeneric<T> specify(Class<?> rawClass) {
        return new ClassCompileGeneric<>(rawClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> CompileGeneric<T> specify() {
        return (CompileGeneric<T>) NON_CLASS_INSTANCE;
    }

    private CompileGeneric() {
    }

    @Override
    public String toString() {
        return getClass().getName().replaceFirst("(?:.+\\.)*(.+?)$", "$1");
    }

}
