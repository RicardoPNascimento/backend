package com.simbioff.simbioff.helpers;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;

public class NullAwareBeanUtils {

    public static void copyNonNullProperties(Object source, Object destination) {
        BeanUtils.copyProperties(source, destination,
                getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {
        var src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        var emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            //check if value of this property is null then add it to the collection
            Object srcValue = src.getPropertyValue(pd.getName());

            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];

        return emptyNames.toArray(result);
    }
}
