package com.fw.internal.utils;

import java.lang.annotation.*;

/**
 * Indicates that the annotated element is for internal use only.
 * It is not part of the supported public API and may be changed or removed without notice.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
public @interface Internal {
}