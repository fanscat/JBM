/*
 * Copyright 2016-2021 Pnoker. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jbm.framework.opcua.util;

import cn.hutool.core.convert.Convert;
import com.jbm.framework.opcua.attribute.ValueType;
import jodd.typeconverter.TypeConversionException;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author wesley.zhang
 */
@Slf4j
public class DriverUtils {


    /**
     * 通过类型转换数据
     *
     * @param type  String Type, short/int/long/float/double/boolean/string
     * @param value String Value
     * @param <T>   T
     * @return T
     */
    public static <T> T value(String type, String value) {
        return Convert.convertByClassName(getTypeClassName(type), value);
    }

    /**
     * 通过类型转换数据
     *
     * @param valueType String Type, short/int/long/float/double/boolean/string
     * @param value     String Value
     * @param <T>       T
     * @return T
     */
    public static <T> T value(ValueType valueType, String value) {
        return Convert.convertByClassName(getTypeClassName(valueType), value);
    }


    /**
     * Base 64 解码
     *
     * @param content string
     * @return string
     */
    public static String base64Encode(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base 64 编码
     *
     * @param content string
     * @return string
     */
    public static String base64Decode(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return new String(Base64.getDecoder().decode(bytes));
    }

    /**
     * 将byte[]转成十六进制字符串
     *
     * @param bytes Byte Array
     * @return String
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * byte数组到int的转换(大端)
     *
     * @param bytes Byte Array
     * @return Integer
     */
    public static int bytesToInt(byte[] bytes) {
        byte[] temp = new byte[4];
        int length = bytes.length;
        System.arraycopy(bytes, 0, temp, 0, length);
        for (int i = length; i < 4; i++) {
            temp[i] = 0x00;
        }
        int int1 = temp[3] & 0xff;
        int int2 = (temp[2] & 0xff) << 8;
        int int3 = (temp[1] & 0xff) << 16;
        int int4 = (temp[0] & 0xff) << 24;

        return int1 | int2 | int3 | int4;
    }

    /**
     * byte数组到int的转换(小端)
     *
     * @param bytes Byte Array
     * @return Integer
     */
    public static int bytesToIntLE(byte[] bytes) {
        byte[] temp = new byte[4];
        int length = bytes.length;
        System.arraycopy(bytes, 0, temp, 0, length);
        for (int i = length; i < 4; i++) {
            temp[i] = 0x00;
        }
        int int1 = temp[0] & 0xff;
        int int2 = (temp[1] & 0xff) << 8;
        int int3 = (temp[2] & 0xff) << 16;
        int int4 = (temp[3] & 0xff) << 24;
        return int1 | int2 | int3 | int4;
    }

    /**
     * 将byte[]转成Ascii码
     *
     * @param bytes Byte Array
     * @return String
     */
    public static String bytesToAscii(byte[] bytes) {
        String asciiStr = null;
        try {
            asciiStr = new String(bytes, "ISO8859-1");
        } catch (UnsupportedEncodingException ignored) {
        }
        return asciiStr;
    }

    /**
     * 将byte[]颠倒
     *
     * @param bytes Byte Array
     * @return Byte Array
     */
    public static byte[] byteReverse(byte[] bytes) {
        int length = bytes.length;
        byte[] reverse = new byte[length];
        for (int i = 0; i < length; i++) {
            reverse[length - 1 - i] = bytes[i];
        }
        return reverse;
    }

    /**
     * 合并byte[]
     *
     * @param bytes Byte Array
     * @return Byte Array
     */
    public static byte[] mergerBytes(byte[]... bytes) {
        int lengthByte = 0;
        for (byte[] value : bytes) {
            lengthByte += value.length;
        }
        byte[] allByte = new byte[lengthByte];
        int countLength = 0;
        for (byte[] b : bytes) {
            System.arraycopy(b, 0, allByte, countLength, b.length);
            countLength += b.length;
        }
        return allByte;
    }

    /**
     * 获取字节间的异或值
     *
     * @param bytes Byte Array
     * @return Byte
     */
    public static byte xorBytes(byte[]... bytes) {
        byte xor = 0x00;
        for (byte[] value : bytes) {
            for (byte b : value) {
                xor ^= b;
            }
        }
        return xor;
    }


    /**
     * 获取基本类型 Class Name
     *
     * @param type String Type, short/int/long/float/double/boolean/string
     * @return Class Name
     */
    public static String getTypeClassName(String type) {
        ValueType valueType = null;
        try {
            valueType = ValueType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new TypeConversionException(String.format("没有找到对应的类型:{}", type), e);
        }
        return getTypeClassName(valueType);
    }

    /**
     * 获取基本类型 Class Name
     *
     * @param valueType String Type, short/int/long/float/double/boolean/string
     * @return Class Name
     */
    public static String getTypeClassName(ValueType valueType) {
        String className = "java.lang.String";
        switch (valueType) {
            case SHORT:
                className = "java.lang.Short";
                break;
            case INT:
                className = "java.lang.Integer";
                break;
            case LONG:
                className = "java.lang.Long";
                break;
            case FLOAT:
                className = "java.lang.Float";
                break;
            case DOUBLE:
                className = "java.lang.Double";
                break;
            case BOOLEAN:
                className = "java.lang.Boolean";
                break;
            default:
                break;
        }
        return className;
    }

}
