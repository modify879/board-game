package com.jsm.boardgame.utils;

import com.jsm.boardgame.common.enums.EnumCodeType;
import com.jsm.boardgame.exception.ApiException;
import com.jsm.boardgame.exception.ErrorCodeType;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;

public class EnumCodeConverterUtils {

    public static <T extends Enum<T> & EnumCodeType> T ofCode(String code, Class<T> enumClass) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        return EnumSet.allOf(enumClass).stream()
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCodeType.ENUM_CODE_CONVERT));
    }

    public static <T extends Enum<T> & EnumCodeType> String toCode(T enumValue) {
        if (enumValue == null) {
            return null;
        }

        return enumValue.getCode();
    }
}
