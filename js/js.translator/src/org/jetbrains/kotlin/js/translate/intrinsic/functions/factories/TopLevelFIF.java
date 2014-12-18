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

package org.jetbrains.kotlin.js.translate.intrinsic.functions.factories;

import com.google.dart.compiler.backend.js.ast.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.builtins.PrimitiveType;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.PropertyDescriptor;
import org.jetbrains.kotlin.js.descriptors.DescriptorPredicate;
import org.jetbrains.kotlin.js.descriptors.NamePredicate;
import org.jetbrains.kotlin.js.translate.callTranslator.CallInfo;
import org.jetbrains.kotlin.js.translate.context.Namer;
import org.jetbrains.kotlin.js.translate.context.TranslationContext;
import org.jetbrains.kotlin.js.translate.intrinsic.functions.basic.FunctionIntrinsic;
import org.jetbrains.kotlin.js.translate.utils.AnnotationsUtils;
import org.jetbrains.kotlin.js.translate.utils.BindingUtils;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.JetExpression;
import org.jetbrains.kotlin.psi.JetQualifiedExpression;
import org.jetbrains.kotlin.psi.JetReferenceExpression;
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall;
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver;
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue;
import org.jetbrains.kotlin.types.JetType;

import java.util.List;

import static org.jetbrains.kotlin.js.descriptors.DescriptorsPackage.getNameIfStandardType;
import static org.jetbrains.kotlin.js.descriptors.PatternBuilder.pattern;
import static org.jetbrains.kotlin.js.translate.intrinsic.functions.basic.FunctionIntrinsic.CallParametersAwareFunctionIntrinsic;
import static org.jetbrains.kotlin.js.translate.utils.ManglingUtils.getStableMangledNameForDescriptor;

public final class TopLevelFIF extends CompositeFIF {
    public static final DescriptorPredicate EQUALS_IN_ANY = pattern("kotlin", "Any", "equals");
    @NotNull
    public static final KotlinFunctionIntrinsic KOTLIN_EQUALS = new KotlinFunctionIntrinsic("equals");
    @NotNull
    public static final FunctionIntrinsic IDENTITY_EQUALS = new FunctionIntrinsic() {
        @NotNull
        @Override
        public JsExpression apply(
                @Nullable JsExpression receiver, @NotNull List<JsExpression> arguments, @NotNull TranslationContext context
        ) {
            assert arguments.size() == 1 : "Unexpected argument size for kotlin.identityEquals: " + arguments.size();
            return new JsBinaryOperation(JsBinaryOperator.REF_EQ, receiver, arguments.get(0));
        }
    };

    @NotNull
    public static final DescriptorPredicate HASH_CODE_IN_ANY = pattern("kotlin", "Any", "hashCode");
    @NotNull
    public static final KotlinFunctionIntrinsic KOTLIN_HASH_CODE = new KotlinFunctionIntrinsic("hashCode");

    @NotNull
    private static final FunctionIntrinsic RETURN_RECEIVER_INTRINSIC = new FunctionIntrinsic() {
        @NotNull
        @Override
        public JsExpression apply(
                @Nullable JsExpression receiver,
                @NotNull List<JsExpression> arguments,
                @NotNull TranslationContext context
        ) {
            assert receiver != null;
            return receiver;
        }
    };

    private static final FunctionIntrinsic NATIVE_MAP_GET = new NativeMapGetSet() {
        @NotNull
        @Override
        protected String operationName() {
            return "get";
        }

        @Nullable
        @Override
        protected ExpressionReceiver getExpressionReceiver(@NotNull ResolvedCall<?> resolvedCall) {
            ReceiverValue result = resolvedCall.getDispatchReceiver();
            return result instanceof ExpressionReceiver ? (ExpressionReceiver) result : null;
        }

        @Override
        protected JsExpression asArrayAccess(
                @NotNull JsExpression receiver,
                @NotNull List<JsExpression> arguments,
                @NotNull TranslationContext context
        ) {
            return ArrayFIF.GET_INTRINSIC.apply(receiver, arguments, context);
        }
    };

    private static final FunctionIntrinsic NATIVE_MAP_SET = new NativeMapGetSet() {
        @NotNull
        @Override
        protected String operationName() {
            return "put";
        }

        @Nullable
        @Override
        protected ExpressionReceiver getExpressionReceiver(@NotNull ResolvedCall<?> resolvedCall) {
            ReceiverValue result = resolvedCall.getExtensionReceiver();
            return result instanceof ExpressionReceiver ? (ExpressionReceiver) result : null;
        }

        @Override
        protected JsExpression asArrayAccess(
                @NotNull JsExpression receiver,
                @NotNull List<JsExpression> arguments,
                @NotNull TranslationContext context
        ) {
            return ArrayFIF.SET_INTRINSIC.apply(receiver, arguments, context);
        }
    };

    private static final FunctionIntrinsic PROPERTY_METADATA_IMPL = new FunctionIntrinsic() {
        @NotNull
        @Override
        public JsExpression apply(
                @Nullable JsExpression receiver, @NotNull List<JsExpression> arguments, @NotNull TranslationContext context
        ) {
            JsNameRef functionRef = new JsNameRef("PropertyMetadata", Namer.KOTLIN_NAME);
            return new JsNew(functionRef, arguments);
        }
    };

    @NotNull
    public static final KotlinFunctionIntrinsic TO_STRING = new KotlinFunctionIntrinsic("toString");

    @NotNull
    public static final FunctionIntrinsicFactory INSTANCE = new TopLevelFIF();

    private TopLevelFIF() {
        add(EQUALS_IN_ANY, KOTLIN_EQUALS);
        add(pattern("kotlin", "toString").isExtensionOf("kotlin.Any"), TO_STRING);
        add(pattern("kotlin", "equals").isExtensionOf("kotlin.Any"), KOTLIN_EQUALS);
        add(pattern("kotlin", "identityEquals").isExtensionOf("kotlin.Any"), IDENTITY_EQUALS);
        add(HASH_CODE_IN_ANY, KOTLIN_HASH_CODE);
        add(pattern(NamePredicate.PRIMITIVE_NUMBERS, "equals"), KOTLIN_EQUALS);
        add(pattern("String|Boolean|Char|Number.equals"), KOTLIN_EQUALS);
        add(pattern("kotlin", "arrayOfNulls"), new KotlinFunctionIntrinsic("nullArray"));
        add(pattern("kotlin", "PropertyMetadataImpl", "<init>"), PROPERTY_METADATA_IMPL);
        add(pattern("kotlin", "iterator").isExtensionOf("kotlin.Iterator"), RETURN_RECEIVER_INTRINSIC);

        add(pattern("kotlin", "Map", "get").checkOverridden(), NATIVE_MAP_GET);
        add(pattern("kotlin.js", "set").isExtensionOf("kotlin.MutableMap"), NATIVE_MAP_SET);

        add(pattern("java.util", "HashMap", "<init>"), new MapSelectImplementationIntrinsic(false));
        add(pattern("java.util", "HashSet", "<init>"), new MapSelectImplementationIntrinsic(true));

        add(pattern("kotlin.js", "Json", "get"), ArrayFIF.GET_INTRINSIC);
        add(pattern("kotlin.js", "Json", "set"), ArrayFIF.SET_INTRINSIC);

        add(pattern("kotlin", "Throwable", "getMessage"), MESSAGE_PROPERTY_INTRINSIC);
    }

    private abstract static class NativeMapGetSet extends CallParametersAwareFunctionIntrinsic {
        @NotNull
        protected abstract String operationName();

        @Nullable
        protected abstract ExpressionReceiver getExpressionReceiver(@NotNull ResolvedCall<?> resolvedCall);

        protected abstract JsExpression asArrayAccess(
                @NotNull JsExpression receiver,
                @NotNull List<JsExpression> arguments,
                @NotNull TranslationContext context
        );

        @NotNull
        @Override
        public JsExpression apply(@NotNull CallInfo callInfo, @NotNull List<JsExpression> arguments, @NotNull TranslationContext context) {
            ExpressionReceiver expressionReceiver = getExpressionReceiver(callInfo.getResolvedCall());
            JsExpression thisOrReceiver = getThisOrReceiverOrNull(callInfo);
            assert thisOrReceiver != null;
            if (expressionReceiver != null) {
                JetExpression expression = expressionReceiver.getExpression();
                JetReferenceExpression referenceExpression = null;
                if (expression instanceof JetReferenceExpression) {
                    referenceExpression = (JetReferenceExpression) expression;
                }
                else if (expression instanceof JetQualifiedExpression) {
                    JetExpression candidate = ((JetQualifiedExpression) expression).getReceiverExpression();
                    if (candidate instanceof JetReferenceExpression) {
                        referenceExpression = (JetReferenceExpression) candidate;
                    }
                }

                if (referenceExpression != null) {
                    DeclarationDescriptor candidate = BindingUtils.getDescriptorForReferenceExpression(context.bindingContext(),
                                                                                                       referenceExpression);
                    if (candidate instanceof PropertyDescriptor && AnnotationsUtils.isNativeObject(candidate)) {
                        return asArrayAccess(thisOrReceiver, arguments, context);
                    }
                }
            }

            String mangledName = getStableMangledNameForDescriptor(KotlinBuiltIns.getInstance().getMutableMap(), operationName());

            return new JsInvocation(new JsNameRef(mangledName, thisOrReceiver), arguments);
        }
    }

    private static class MapSelectImplementationIntrinsic extends CallParametersAwareFunctionIntrinsic {
        private final boolean isSet;

        private MapSelectImplementationIntrinsic(boolean isSet) {
            this.isSet = isSet;
        }

        @NotNull
        @Override
        public JsExpression apply(
                @NotNull CallInfo callInfo,
                @NotNull List<JsExpression> arguments,
                @NotNull TranslationContext context
        ) {
            JetType keyType = callInfo.getResolvedCall().getTypeArguments().values().iterator().next();
            Name keyTypeName = getNameIfStandardType(keyType);
            String collectionClassName = null;
            if (keyTypeName != null) {
                if (NamePredicate.PRIMITIVE_NUMBERS.apply(keyTypeName)) {
                    collectionClassName = isSet ? "PrimitiveNumberHashSet" : "PrimitiveNumberHashMap";
                }
                else if (PrimitiveType.BOOLEAN.getTypeName().equals(keyTypeName)) {
                    collectionClassName = isSet ? "PrimitiveBooleanHashSet" : "PrimitiveBooleanHashMap";
                }
                else if (keyTypeName.asString().equals("String")) {
                    collectionClassName = isSet ? "DefaultPrimitiveHashSet" : "DefaultPrimitiveHashMap";
                }
            }

            if (collectionClassName == null ) {
                collectionClassName = isSet ? "ComplexHashSet" : "ComplexHashMap";
            }

            return new JsNew(context.namer().kotlin(collectionClassName), arguments);
        }
    }
}
