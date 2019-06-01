package com.xining.angen.annotation;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author xining
 * @since 2019/5/23
 */
public class ValidatorUtil {

    /**
     * 校验&抛出异常
     *
     * @param object
     * @param <T>
     */
    public static <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = doValidate(object, groups);
        complain(violations);
    }

    /**
     * 校验异常
     *
     * @param object
     * @param <T>
     * @return
     */
    private static <T> Set<ConstraintViolation<T>> doValidate(T object, Class<?>... groups) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(object, groups);
    }

    /**
     * 抛出异常
     *
     * @param violations
     * @param <T>
     * @throws InvalidValidaException
     */
    public static <T> void complain(Set<ConstraintViolation<T>> violations) throws InvalidValidaException {
        if (violations == null || violations.size() == 0) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<T> violation : violations) {
            stringBuilder.append(violation.getMessage());
            stringBuilder.append(";");
        }
        throw new InvalidValidaException(stringBuilder.toString());
    }
}
