/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.jps.build;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("jps-plugin/testData/incremental/classHierarchyChanged")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class ExperimentalIncrementalClassHierarchyChangedTestGenerated extends AbstractExperimentalIncrementalClassHierarchyChangedTest {
    @TestMetadata("addPrimaryConstructorArgument")
    public void testAddPrimaryConstructorArgument() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/addPrimaryConstructorArgument/");
        doTest(fileName);
    }

    @TestMetadata("addSecondaryConstructor")
    public void testAddSecondaryConstructor() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/addSecondaryConstructor/");
        doTest(fileName);
    }

    public void testAllFilesPresentInClassHierarchyChanged() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("jps-plugin/testData/incremental/classHierarchyChanged"), Pattern.compile("^([^\\.]+)$"), true);
    }

    @TestMetadata("baseClassToInterface")
    public void testBaseClassToInterface() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/baseClassToInterface/");
        doTest(fileName);
    }

    @TestMetadata("baseInterfaceToClass")
    public void testBaseInterfaceToClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/baseInterfaceToClass/");
        doTest(fileName);
    }

    @TestMetadata("baseOpenToFinal")
    public void testBaseOpenToFinal() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/baseOpenToFinal/");
        doTest(fileName);
    }

    @TestMetadata("baseTypeParameterAdded")
    public void testBaseTypeParameterAdded() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/baseTypeParameterAdded/");
        doTest(fileName);
    }

    @TestMetadata("baseTypeParameterRemoved")
    public void testBaseTypeParameterRemoved() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/baseTypeParameterRemoved/");
        doTest(fileName);
    }

    @TestMetadata("listOfBasesAddClass")
    public void testListOfBasesAddClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/listOfBasesAddClass/");
        doTest(fileName);
    }

    @TestMetadata("listOfBasesAddInterface")
    public void testListOfBasesAddInterface() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/listOfBasesAddInterface/");
        doTest(fileName);
    }

    @TestMetadata("listOfBasesRemoveClass")
    public void testListOfBasesRemoveClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/listOfBasesRemoveClass/");
        doTest(fileName);
    }

    @TestMetadata("listOfBasesRemoveInterface")
    public void testListOfBasesRemoveInterface() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/listOfBasesRemoveInterface/");
        doTest(fileName);
    }

    @TestMetadata("primaryConstructorBecamePrivate")
    public void testPrimaryConstructorBecamePrivate() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/primaryConstructorBecamePrivate/");
        doTest(fileName);
    }

    @TestMetadata("renameClass")
    public void testRenameClass() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/renameClass/");
        doTest(fileName);
    }

    @TestMetadata("renameInterface")
    public void testRenameInterface() throws Exception {
        String fileName = KotlinTestUtils.navigationMetadata("jps-plugin/testData/incremental/classHierarchyChanged/renameInterface/");
        doTest(fileName);
    }

}
