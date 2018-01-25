package com.dream.pay.utils;


import com.dream.pay.bean.ValidationResult;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption 参数校验工具<br/>
 * <p>
 * 遵循JSR-349标准规范. 这里使用hibernate-validate的JSR-349规范实现.
 * </p>
 */
public class ValidationUtils {

    /**
     * 校验
     *
     * @param target 被校验对象
     * @param <T>    被校验对象类型
     * @return 校验结果
     */
    public static <T> ValidationResult validate(T target) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> constraints = validator.validate(target);
        StringBuilder message = new StringBuilder();

        for (ConstraintViolation<T> constraint : constraints) {
            message.append(constraint.getPropertyPath() + ":");
            message.append(constraint.getMessage());
            message.append(",");
        }
        return new ValidationResult(CollectionUtils.isEmpty(constraints), message.toString());
    }
}
