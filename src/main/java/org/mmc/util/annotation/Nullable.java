/*
 * Copyright (C) 2013 The Android Open Source Project
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
package org.mmc.util.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * 用于标记方法参数、返回值或字段，表示该元素可以为 null。
 * 类似地，静态代码分析工具会使用 @Nullable 注解来辅助分析代码，并提醒开发者在处理可能为
 * null 的情况时要格外小心，以避免潜在的空指针异常。
 * <p>
 * Denotes that a parameter, field or method return value can be null.
 * <p>
 * When decorating a method call parameter, this denotes that the parameter can
 * legitimately be null and the method will gracefully deal with it. Typically
 * used on optional parameters.
 * <p>
 * When decorating a method, this denotes the method might legitimately return
 * null.
 * <p>
 * This is a marker annotation and it has no specific attributes.
 */
@Documented
@Retention(CLASS)
@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
public @interface Nullable {
}
