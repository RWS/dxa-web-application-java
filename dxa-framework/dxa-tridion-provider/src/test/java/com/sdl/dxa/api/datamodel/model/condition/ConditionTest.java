package com.sdl.dxa.api.datamodel.model.condition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.targetgroup.TargetGroup;
import com.sdl.dxa.api.datamodel.model.util.ListWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.ListIterator;

import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.CONTAINS;
import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.ENDS_WITH;
import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.EQUALS;
import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.GREATER_THAN;
import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.LESS_THEN;
import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.NOT_EQUAL;
import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.STARTS_WITH;
import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.STRING_EQUALS;
import static com.sdl.dxa.api.datamodel.model.condition.ConditionOperator.UNKNOWN_BY_CLIENT;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConditionTest.SpringConfigurationContext.class)
public class ConditionTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldDeserializeConditions() throws IOException {
        //given
        File file = new ClassPathResource("pageModel-Conditions.json").getFile();

        //when
        PageModelData pageModelData = objectMapper.readValue(file, PageModelData.class);

        //then
        //noinspection unchecked
        ListWrapper<Condition> conditions = (ListWrapper<Condition>) pageModelData.getExtensionData().get("Conditions");
        ListIterator<Condition> iterator = conditions.getValues().listIterator();

        TrackingKeyCondition trackingKeyCondition = (TrackingKeyCondition) iterator.next();
        assertEquals("Test Keyword 1", trackingKeyCondition.getTrackingKeyTitle());
        assertEquals(EQUALS, trackingKeyCondition.getOperator());
        assertEquals(1234.0d, trackingKeyCondition.getValue());
        assertTrue(trackingKeyCondition.isNegate());

        CustomerCharacteristicCondition characteristicCondition = (CustomerCharacteristicCondition) iterator.next();
        assertEquals(1234.0d, characteristicCondition.getValue());
        assertEquals("Test Keyword 2", characteristicCondition.getName());
        assertEquals(GREATER_THAN, characteristicCondition.getOperator());
        assertTrue(characteristicCondition.isNegate());

        characteristicCondition = (CustomerCharacteristicCondition) iterator.next();
        assertFalse(characteristicCondition.isNegate());
        assertEquals(LESS_THEN, characteristicCondition.getOperator());

        TargetGroupCondition targetGroupCondition = (TargetGroupCondition) iterator.next();
        assertTrue(targetGroupCondition.isNegate());
        TargetGroup targetGroup = targetGroupCondition.getTargetGroup();
        assertEquals("TargetGroup", targetGroup.getDescription());
        assertEquals("Id", targetGroup.getId());
        assertEquals("Title", targetGroup.getTitle());

        trackingKeyCondition = (TrackingKeyCondition) targetGroup.getConditions().get(0);
        assertEquals("Test Keyword 1", trackingKeyCondition.getTrackingKeyTitle());
        assertEquals(NOT_EQUAL, trackingKeyCondition.getOperator());
        assertEquals("str", trackingKeyCondition.getValue());
        assertTrue(trackingKeyCondition.isNegate());

        characteristicCondition = (CustomerCharacteristicCondition) targetGroup.getConditions().get(1);
        assertEquals(true, characteristicCondition.getValue());
        assertEquals("Test Keyword 2", characteristicCondition.getName());
        assertEquals(STRING_EQUALS, characteristicCondition.getOperator());
        assertTrue(characteristicCondition.isNegate());

        trackingKeyCondition = (TrackingKeyCondition) targetGroup.getConditions().get(2);
        assertEquals(CONTAINS, trackingKeyCondition.getOperator());

        trackingKeyCondition = (TrackingKeyCondition) targetGroup.getConditions().get(3);
        assertEquals(STARTS_WITH, trackingKeyCondition.getOperator());

        trackingKeyCondition = (TrackingKeyCondition) targetGroup.getConditions().get(4);
        assertEquals(ENDS_WITH, trackingKeyCondition.getOperator());

        trackingKeyCondition = (TrackingKeyCondition) targetGroup.getConditions().get(5);
        assertEquals(UNKNOWN_BY_CLIENT, trackingKeyCondition.getOperator());
    }

    @Configuration
    public static class SpringConfigurationContext {

        @Bean
        public ObjectMapper objectMapper() {
            return new DataModelSpringConfiguration().dxaR2ObjectMapper();
        }
    }
}