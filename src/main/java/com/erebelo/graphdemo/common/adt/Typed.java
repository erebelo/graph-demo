/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.adt;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Type references to preserve (reify) generic parameters at runtime. This class
 * is immutable and thread-safe as it contains only static methods.
 */
public final class Typed {

    /**
     * Private constructor. Type contains only static methods.
     */
    private Typed() {
    }

    /**
     * Factory for a single type parameter.
     */
    public static <T> Typed1<T> of(final T data, final Class<T> clazz) {
        Objects.requireNonNull(clazz);
        return new Typed1<>(data) {
        };
    }

    /**
     * Factory for two type parameters.
     */
    public static <A, B> Typed2<A, B> of(final A a, final B b, final Class<A> classA, final Class<B> classB) {
        Objects.requireNonNull(classA);
        Objects.requireNonNull(classB);
        return new Typed2<>(a, b) {
        };
    }

    /**
     * Factory for three type parameters.
     */
    public static <A, B, C> Typed3<A, B, C> of(final A a, final B b, final C c, final Class<A> classA,
            final Class<B> classB, final Class<C> classC) {
        Objects.requireNonNull(classA);
        Objects.requireNonNull(classB);
        Objects.requireNonNull(classC);
        return new Typed3<>(a, b, c) {
        };
    }

    /**
     * Helper method to retrieve type information.
     */
    private static Type[] getTypes(final Class<?> clazz) {
        final var superClass = clazz.getGenericSuperclass();
        if (superClass instanceof final ParameterizedType parameterized) {
            return parameterized.getActualTypeArguments();
        }
        throw new IllegalStateException("Missing type parameters.");
    }

    /**
     * Abstract class for one type parameter. This class is immutable and
     * thread-safe.
     */
    @SuppressWarnings("AbstractClassWithoutAbstractMethods")
    public abstract static class Typed1<A> {

        private final A first;
        private final Type typeA;

        /**
         * Creates a new typed instance with the specified value.
         */
        protected Typed1(final A first) {

            this.first = first;
            final var types = getTypes(getClass());
            typeA = types[0];
        }

        /**
         * Returns the first value.
         */
        public final A getFirst() {
            return first;
        }

        /**
         * Returns the type of the first value.
         */
        public final Type getTypeA() {
            return typeA;
        }
    }

    /**
     * Abstract class for two type parameters. This class is immutable and
     * thread-safe.
     */
    @SuppressWarnings("AbstractClassWithoutAbstractMethods")
    public abstract static class Typed2<A, B> {

        private final A first;
        private final B second;
        private final Type typeA;
        private final Type typeB;

        protected Typed2(final A first, final B second) {

            this.first = first;
            this.second = second;
            final var types = getTypes(getClass());
            typeA = types[0];
            typeB = types[1];
        }

        public final A getFirst() {
            return first;
        }

        public final B getSecond() {
            return second;
        }

        public final Type getTypeA() {
            return typeA;
        }

        public final Type getTypeB() {
            return typeB;
        }
    }

    /**
     * Abstract class for three type parameters.
     */
    @SuppressWarnings("AbstractClassWithoutAbstractMethods")
    public abstract static class Typed3<A, B, C> {

        private final A first;
        private final B second;
        private final C third;
        private final Type typeA;
        private final Type typeB;
        private final Type typeC;

        protected Typed3(final A first, final B second, final C third) {
            this.first = first;
            this.second = second;
            this.third = third;
            final var types = getTypes(getClass());
            typeA = types[0];
            typeB = types[1];
            typeC = types[2];
        }

        public final A getFirst() {
            return first;
        }

        public final B getSecond() {
            return second;
        }

        public final C getThird() {
            return third;
        }

        public final Type getTypeA() {
            return typeA;
        }

        public final Type getTypeB() {
            return typeB;
        }

        public final Type getTypeC() {
            return typeC;
        }
    }
}
