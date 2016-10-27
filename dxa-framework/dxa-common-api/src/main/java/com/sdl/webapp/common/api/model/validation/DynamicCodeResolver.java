package com.sdl.webapp.common.api.model.validation;

import com.sdl.webapp.common.api.model.ViewModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Error codes resolver that check the given model for presence of {@link DynamicValidationMessage} annotation and call the found method.
 */
@Slf4j
public final class DynamicCodeResolver {

    private DynamicCodeResolver() {
    }

    /**
     * Tries to resolve given error code.
     *
     * @param code  code to resolve
     * @param model model that's expected to have an annotated method
     * @return resolved message by calling a method annotated,
     * or {@code null} if no annotation found or args are null,
     * or {@code error code} is we cannot invoke a method
     */
    @Nullable
    public static String resolveCode(@Nullable String code, @Nullable ViewModel model) {
        if (code == null || model == null) {
            return null;
        }

        for (Method method : model.getClass().getDeclaredMethods()) {
            DynamicValidationMessage annotation = method.getAnnotation(DynamicValidationMessage.class);
            if (annotation != null && annotation.errorCode().equals(code)) {
                try {
                    return (String) method.invoke(model);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.warn("Method is annotated with {} but we cannot invoke it", DynamicValidationMessage.class, e);
                    return annotation.errorCode();
                }
            }
        }

        return null;
    }
}
