/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package com.mirth.connect.model.converters;

import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class JavaScriptObjectConverter extends ReflectionConverter {

    private static final String RHINO_NATIVE_OBJECT = "org.mozilla.javascript.NativeObject";
    private static final String RHINO_NATIVE_ARRAY = "org.mozilla.javascript.NativeArray";
    private static final String RHINO_NATIVE_DATE = "org.mozilla.javascript.NativeDate";
    private static final String RHINO_NATIVE_BOOLEAN = "org.mozilla.javascript.NativeBoolean";

    public JavaScriptObjectConverter(Mapper mapper) {
        super(mapper, JVM.newReflectionProvider());
    }

    @Override
    public boolean canConvert(Class type) {
        String name = type.getName();
        return RHINO_NATIVE_OBJECT.equals(name) || RHINO_NATIVE_ARRAY.equals(name) || RHINO_NATIVE_DATE.equals(name) || RHINO_NATIVE_BOOLEAN.equals(name);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        try {
            String className = value.getClass().getName();
            if (RHINO_NATIVE_DATE.equals(className)) {
                // We don't want the quotes around the date, so call toISOString directly.
                // Avoid linking against NativeDate (some Rhino builds keep it package-private).
                double jsTime = ((Number) value.getClass().getMethod("getJSTimeValue").invoke(value)).doubleValue();
                Object iso = Class.forName(RHINO_NATIVE_DATE).getMethod("js_toISOString", double.class).invoke(null, jsTime);
                context.convertAnother(String.valueOf(iso));
                return;
            }

            context.convertAnother(NativeJSON.stringify(null, (Scriptable) value, value, null, null).toString());
        } catch (Exception e) {
            super.marshal(value, writer, context);
        }
    }
}