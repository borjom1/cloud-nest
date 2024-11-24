package com.cloud.nest.fm.util.filter;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

import static org.springframework.util.ReflectionUtils.makeAccessible;

@Component
public class JooqQueryFilterTranslator {

    /**
     * Reads an object with fields annotated with {@link Like}, {@link GreaterOrEqual} or {@link LessOrEqual}
     * and constructs a SQL-select criteria using {@link Condition} from JOOQ API.
     *
     * @return Condition object, or {@code null} when filter fields were not found or their values are {@code null}.
     */
    @Nullable
    public Condition toCondition(@Nullable Object filterObj) {
        if (filterObj == null) {
            return null;
        }
        Condition conditionResult = null;

        for (Field field : filterObj.getClass().getDeclaredFields()) {
            Condition newCondition = null;

            if (field.isAnnotationPresent(Like.class)) {
                newCondition = buildLikeQuery(field, filterObj);
            } else if (field.isAnnotationPresent(GreaterOrEqual.class)) {
                newCondition = buildGreaterThanOrEqualQuery(field, filterObj);
            } else if (field.isAnnotationPresent(LessOrEqual.class)) {
                newCondition = buildLessThanOrEqualQuery(field, filterObj);
            }

            if (newCondition == null) {
                continue;
            }

            if (conditionResult == null) {
                conditionResult = newCondition;
            } else {
                conditionResult = conditionResult.and(newCondition);
            }
        }

        return conditionResult;
    }

    private Condition buildLikeQuery(Field field, Object filterObj) {
        if (!field.getType().equals(String.class)) {
            throw new IllegalArgumentException(
                    "Like query cannot be built from field of type [%s]".formatted(field.getType().getName())
            );
        }

        ReflectionUtils.makeAccessible(field);
        final String fieldValue = (String) ReflectionUtils.getField(field, filterObj);

        return StringUtils.hasText(fieldValue)
                ? DSL.field((toSnakeCase(field.getName())), field.getType()).like("%" + fieldValue + "%")
                : null;
    }

    private Condition buildGreaterThanOrEqualQuery(Field field, Object filterObj) {
        final Class<?> type = field.getType();
        makeAccessible(field);

        final Object fieldValue = ReflectionUtils.getField(field, filterObj);
        if (fieldValue == null) {
            return null;
        }

        final String fieldName = field.getAnnotation(GreaterOrEqual.class).value();
        final String sqlFieldName = toSnakeCase(fieldName.isBlank() ? field.getName() : fieldName);

        if (type.equals(Integer.class)) {
            return DSL.field(sqlFieldName, Integer.class).greaterOrEqual((Integer) fieldValue);
        } else if (type.equals(Long.class)) {
            return DSL.field(sqlFieldName, Long.class).greaterOrEqual((Long) fieldValue);
        } else if (type.equals(Double.class)) {
            return DSL.field(sqlFieldName, Double.class).greaterOrEqual((Double) fieldValue);
        }
        throw new IllegalArgumentException(
                "Greater than or equal query cannot be built from field of type [%s]".formatted(type.getName())
        );
    }

    private Condition buildLessThanOrEqualQuery(Field field, Object filterObj) {
        final Class<?> type = field.getType();
        makeAccessible(field);

        final Object fieldValue = ReflectionUtils.getField(field, filterObj);
        if (fieldValue == null) {
            return null;
        }

        final String fieldName = field.getAnnotation(LessOrEqual.class).value();
        final String sqlFieldName = toSnakeCase(fieldName.isBlank() ? field.getName() : fieldName);

        if (type.equals(Integer.class)) {
            return DSL.field(sqlFieldName, Integer.class).lessOrEqual((Integer) fieldValue);
        } else if (type.equals(Long.class)) {
            return DSL.field(sqlFieldName, Long.class).lessOrEqual((Long) fieldValue);
        } else if (type.equals(Double.class)) {
            return DSL.field(sqlFieldName, Double.class).lessOrEqual((Double) fieldValue);
        }
        throw new IllegalArgumentException(
                "Less than or equal query cannot be built from field of type [%s]".formatted(type.getName())
        );
    }

    @NotNull
    private static String toSnakeCase(@NotNull String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

}
