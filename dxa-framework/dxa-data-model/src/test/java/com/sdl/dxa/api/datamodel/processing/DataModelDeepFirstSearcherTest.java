package com.sdl.dxa.api.datamodel.processing;

import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.KeywordModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataModelDeepFirstSearcherTest {

    @Test
    public void shouldTraverseEveryLeaf() {
        //given
        String expected = "mecmpm";
        final int[] deep = {10};
        final int[] steps = {0};

        ContentModelData metadata = new ContentModelData();
        metadata.put("val", new EntityModelData().setId("m"));

        ContentModelData content = new ContentModelData();
        content.put("val", new EntityModelData().setId("c"));

        PageModelData page = (PageModelData) new PageModelData()
                .setId("p")
                .setRegions(Lists.newArrayList(new RegionModelData(null, null,
                        Lists.newArrayList(new EntityModelData(null, null, null, metadata, null, null, "e", null, content, null, null)),
                        Lists.newArrayList(new RegionModelData(null, null, null, metadata, null, null, null, null, null, null)))))
                .setMetadata(metadata);

        StringBuilder result = new StringBuilder();

        //when
        new DataModelDeepFirstSearcher() {

            void traversePage(PageModelData pageModelData) {
                traverseObject(pageModelData);
            }

            @Override
            protected boolean goingDeepIsAllowed() {
                ++steps[0];
                return --deep[0] > 0;
            }

            @Override
            protected void goLevelUp() {
                deep[0]++;
            }

            @Override
            protected void processPageModel(PageModelData pageModelData) {
                result.append(pageModelData.getId());
            }

            @Override
            protected void processEntityModel(EntityModelData entityModelData) {
                result.append(entityModelData.getId());
            }

            @Override
            protected void processKeywordModel(KeywordModelData keywordModelData) {
                result.append(keywordModelData.getId());
            }

            @Override
            protected void processRichTextData(RichTextData richTextData) {
                result.append(richTextData.toString());
            }
        }.traversePage(page);

        //then
        assertEquals(expected, result.toString());
        assertEquals(10, deep[0]);
        assertEquals(15, steps[0]);
    }
}