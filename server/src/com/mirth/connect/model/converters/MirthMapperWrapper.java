/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package com.mirth.connect.model.converters;

import com.mirth.connect.donkey.util.xstream.DonkeyMapperWrapper;
import com.mirth.connect.model.Channel;
import com.mirth.connect.model.InvalidChannel;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class MirthMapperWrapper implements DonkeyMapperWrapper {

    private static final String RHINO_NATIVE_OBJECT = "org.mozilla.javascript.NativeObject";
    private static final String RHINO_NATIVE_ARRAY = "org.mozilla.javascript.NativeArray";
    private static final String RHINO_NATIVE_DATE = "org.mozilla.javascript.NativeDate";
    private static final String RHINO_NATIVE_BOOLEAN = "org.mozilla.javascript.NativeBoolean";

    @Override
    public MapperWrapper wrapMapper(MapperWrapper next) {
        return new MapperWrapper(next) {
            @Override
            public String serializedClass(Class type) {
                if (type == InvalidChannel.class) {
                    return super.serializedClass(Channel.class);
                } else if (isRhinoNativeType(type)) {
                    return super.serializedClass(String.class);
                }
                return super.serializedClass(type);
            }
        };
    }

    private static boolean isRhinoNativeType(Class type) {
        if (type == null) {
            return false;
        }
        String name = type.getName();
        return RHINO_NATIVE_OBJECT.equals(name) || RHINO_NATIVE_ARRAY.equals(name) || RHINO_NATIVE_DATE.equals(name) || RHINO_NATIVE_BOOLEAN.equals(name);
    }
}