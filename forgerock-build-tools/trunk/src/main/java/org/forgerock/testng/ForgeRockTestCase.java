/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 *      Copyright 2009-2010 Sun Microsystems, Inc.
 *      Portions copyright 2011-2012 ForgeRock AS
 */

package org.forgerock.testng;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Set;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * This class defines a base test case that should be sub-classed by all unit
 * tests used by ForgeRock projects.
 * <p>
 * This class adds the ability to print error messages and automatically have
 * them include the class name.
 */
@Test
public abstract class ForgeRockTestCase {

    /*
     * This is all a HACK to reduce the amount of memory that's consumed.
     *
     * This could be a problem if a subclass references a @DataProvider in a
     * super-class that provides static parameters, i.e. the parameters are not
     * regenerated for each invocation of the DataProvider.
     */

    /**
     * A list of all parameters that were generated by a @DataProvider and
     * passed to a test method of this class. TestListener helps us keep this so
     * that once all of the tests are finished, we can clear it out in an @AfterClass
     * method. We can't just clear it out right away in the TestListener because
     * some methods share a @DataProvider.
     */
    private final IdentityHashMap<Object[], Object> successfulTestParams = new IdentityHashMap<Object[], Object>();

    /**
     * These are test parameters from a test that has failed. We need to keep
     * these around because the test report expects to find them when printing
     * out failures.
     */
    private final IdentityHashMap<Object[], Object> failedTestParams = new IdentityHashMap<Object[], Object>();

    /**
     * Null out all test parameters except the ones used in failed tests since
     * we might need these again.
     */
    @AfterClass(alwaysRun = true)
    public final void clearSuccessfulTestParams() {
        final Set<Object[]> paramsSet = successfulTestParams.keySet();
        if (paramsSet == null) { // Can this ever happen?
            return;
        }
        for (final Object[] params : paramsSet) {
            if (failedTestParams.containsKey(params)) {
                continue;
            }
            for (int i = 0; i < params.length; i++) {
                params[i] = null;
            }
        }
        successfulTestParams.clear();
        failedTestParams.clear();
    }

    /**
     * The member variables of a test class can prevent lots of memory from
     * being reclaimed, so we use reflection to null out all of the member
     * variables after the tests have run. Since all tests must inherit from
     * ForgeRockTestCase, TestNG guarantees that this method runs after all of
     * the subclass methods, so this isn't too dangerous.
     */
    @AfterClass(alwaysRun = true)
    public final void nullMemberVariablesAfterTest() {
        Class<?> cls = this.getClass();
        /*
         * Iterate through all of the fields in all subclasses of
         * ForgeRockTestCase, but not ForgeRockTestCase itself.
         */
        while (ForgeRockTestCase.class.isAssignableFrom(cls)
                && !ForgeRockTestCase.class.equals(cls)) {
            final Field fields[] = cls.getDeclaredFields();
            for (final Field field : fields) {
                final int modifiers = field.getModifiers();
                final Class<?> fieldClass = field.getType();

                /*
                 * If it's a non-static non-final non-primitive type, then null
                 * it out so that the garbage collector can reclaim it and
                 * everything it references.
                 */
                if (!fieldClass.isPrimitive() && !fieldClass.isEnum()
                        && !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                    field.setAccessible(true);
                    try {
                        field.set(this, null);
                    } catch (final IllegalAccessException e) {
                        /*
                         * We're only doing this to save memory, so it's no big
                         * deal if we can't set it.
                         */
                    }
                }
            }
            cls = cls.getSuperclass();
        }
    }

    /**
     * Adds testParams to the list of all failed test parameters, so that we
     * know to NOT null it out later.
     */
    final void addParamsFromFailedTest(final Object[] testParams) {
        if (testParams != null) {
            failedTestParams.put(testParams, testParams);
        }
    }

    /**
     * Adds testParams to the list of all test parameters, so it can be null'ed
     * out later if it's not part.
     */
    final void addParamsFromSuccessfulTests(final Object[] testParams) {
        if (testParams != null) {
            successfulTestParams.put(testParams, testParams);
        }
    }
}
