package com.jsm.boardgame.common.enums;

import com.jsm.boardgame.exception.ApiException;
import com.jsm.boardgame.exception.ErrorCodeType;
import com.jsm.boardgame.utils.EnumCodeConverterUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;

@Converter
public class AbstractEnumCodeAttributeConverter<E extends Enum<E> & EnumCodeType> implements AttributeConverter<E, String> {

    private final Class<E> targetEnumClass;
    private final boolean nullable;
    private final String enumName;

    public AbstractEnumCodeAttributeConverter(boolean nullable, String enumName) {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.targetEnumClass = (Class<E>) parameterizedType.getActualTypeArguments()[0];
        this.nullable = nullable;
        this.enumName = enumName;
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        if (!nullable && attribute == null) {
            throw new ApiException(ErrorCodeType.ENUM_CODE_CONVERT, String.format("%s(은)는 Null로 저장할 수 없습니다.", enumName));
        }

        return EnumCodeConverterUtils.toCode(attribute);
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if (!nullable && StringUtils.isBlank(dbData)) {
            throw new ApiException(ErrorCodeType.ENUM_CODE_CONVERT, String.format("%s(이)가 DB에 Null 혹은 Empty로 저장되어 있습니다.", enumName));
        }

        return EnumCodeConverterUtils.ofCode(dbData, targetEnumClass);
    }
}
