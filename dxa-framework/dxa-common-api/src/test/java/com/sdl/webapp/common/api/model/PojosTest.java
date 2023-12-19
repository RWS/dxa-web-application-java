package com.sdl.webapp.common.api.model;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.SerializableMustHaveSerialVersionUIDRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.Tester;
import com.openpojo.validation.utils.ValidationHelper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
public abstract class PojosTest {

    @Test
    public void shouldBeValidPojo() {
        //given
        PojoClass keywordPojo = PojoClassFactory.getPojoClass(getPojoClass());

        //when
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new NoFieldShadowingRule())
                .with(new SerializableMustHaveSerialVersionUIDRule())
                .with(new NoPublicFieldsExceptStaticFinalRule())

                .with(identityTester())
                .with(getterSetterTester())
                .build();

        //then
        validator.validate(keywordPojo);
    }

    @NotNull
    private Tester getterSetterTester() {
        return pojoClass -> {
            final Object classInstance = ValidationHelper.getBasicInstance(pojoClass);
            for (final PojoField fieldEntry : pojoClass.getPojoFields()) {
                if (!getExcludedGetterSetterFieldNames().contains(fieldEntry.getName())) {
                    Object value = RandomFactory.getRandomValue(fieldEntry);
                    fieldEntry.invokeSetter(classInstance, value);
                    assertEquals(fieldEntry.invokeGetter(classInstance), value,
                            "Setter/getter are not working fine together, if you have custom logic write a separate test " +
                            "and exclude from automatic check by overriding #getExcludedGetterSetterFieldNames()");
                } else {
                    log.debug("{} field is excluded from automatic test, please make sure you have a custom test for " +
                            "this by overriding #getExcludedGetterSetterFieldNames()", fieldEntry.getName());
                }
            }
        };
    }

    @NotNull
    private Tester identityTester() {
        return pojoClass -> {
            Object basicInstance = ValidationHelper.getBasicInstance(pojoClass);
            Object basicInstance2 = ValidationHelper.getBasicInstance(pojoClass);

            int c = 0;
            for (Field field : pojoClass.getClazz().getDeclaredFields()) {
                try {
                    Object value = field.getType().newInstance();
                    field.setAccessible(true);
                    field.set(basicInstance, value);
                    field.set(basicInstance2, value);
                } catch (IllegalAccessException | InstantiationException e) {
                    c++;
                    log.debug("Tried to instantiate a field with dummy value, but failed since there is no default constructor, skipping");
                }
            }

            if (pojoClass.getClazz().getDeclaredFields().length != c) {
                log.debug("At least one field instantiated, so doing not-equality check");
                Object basicInstance3 = ValidationHelper.getBasicInstance(pojoClass);
                assertNotEquals(basicInstance3, basicInstance);
                assertNotEquals(basicInstance3.toString(), basicInstance.toString());
                assertNotEquals(basicInstance3.hashCode(), basicInstance.hashCode());
            }

            assertEquals(basicInstance2, basicInstance);
            assertEquals(basicInstance2.toString(), basicInstance.toString());
            assertEquals(basicInstance2.hashCode(), basicInstance.hashCode());

            Class<?> clazz = pojoClass.getClazz();
            try {
                clazz.getDeclaredMethod("equals", Object.class);
                clazz.getDeclaredMethod("hashCode");
                clazz.getDeclaredMethod("toString");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("equals, hashCode or toString do not exist in " + clazz);
            }
        };
    }

    @SuppressWarnings("WeakerAccess")
    protected Set<String> getExcludedGetterSetterFieldNames() {
        return Collections.emptySet();
    }

    protected abstract Class<?> getPojoClass();
}
