/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.fragment.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.USimpleNameReferenceExpression


/**
 * Lint check for detecting calls to [LayoutInflater.from(Context)][android.view.LayoutInflater.from] from
 * inside `DialogFragments`.
 *
 * When using a `DialogFragment` and you want to get a `LayoutInflater`, you should always
 * call the `getLayoutInflater()` method from its super-class `Fragment`.
 *
 */
@Suppress("UnstableApiUsage")
class DialogFragmentLayoutInflaterMisuseDetector : Detector(), SourceCodeScanner {

    companion object Issues {
        val JAVA_ISSUE = Issue.create(
            id = "DialogFragmentGetlayoutInflaterIssue",
            briefDescription = "Use [Fragment#getLayoutInflater()] as the LifecycleOwner instead " +
                "of " +
                "a Fragment instance when observing a LiveData object.",
            explanation = """When using a DialogFragment and you want to get a LayoutInflater,  \
                you should always call the getLayoutInflater() method from its super-class \
                Fragment`. \
                \
                Using `LayoutInflater.from(Context)` can return a LayoutInflater that does not \
                have the correct theme.""",
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                DialogFragmentLayoutInflaterMisuseDetector::class.java, Scope.JAVA_FILE_SCOPE
            ),
            androidSpecific = true
        )

        val KOTLIN_ISSUE = Issue.create(
            id = "DialogFragmentGetlayoutInflaterIssue",
            briefDescription = "Use Fragment.layoutinflater as the LifecycleOwner instead of " +
                "a Fragment instance when observing a LiveData object.",
            explanation = """When using a DialogFragment and you want to get a LayoutInflater,  \
                you should always call the getLayoutInflater() method from its super-class \
                Fragment`. \
                \
                Using `LayoutInflater.from(Context)` can return a LayoutInflater that does not \
                have the correct theme.""",
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                DialogFragmentLayoutInflaterMisuseDetector::class.java, Scope.JAVA_FILE_SCOPE
            ),
            androidSpecific = true
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UCallExpression::class.java)
    }
}

private val UNSAFE_METHOD = Method("android.view.LayoutInflater", "from")
private const val DIALOG_FRAGMENT_CLASS = "androidx.fragment.app.DialogFragment"

private val lifecycleMethods = setOf(
    "onCreateView", "onCreateDialog", "onViewCreated", "onActivityCreated",
    "onViewStateRestored"
)
