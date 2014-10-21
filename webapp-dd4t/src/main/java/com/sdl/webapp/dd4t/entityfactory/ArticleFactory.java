package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.api.ContentProviderException;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.entity.Paragraph;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.GenericComponent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sdl.webapp.dd4t.entityfactory.FieldUtil.getEmbeddedValues;
import static com.sdl.webapp.dd4t.entityfactory.FieldUtil.getStringValue;

@Component
public class ArticleFactory implements EntityFactory {

    private static final Class<?>[] SUPPORTED_ENTITY_TYPES = { Article.class };

    @Override
    public Class<?>[] supportedEntityTypes() {
        return SUPPORTED_ENTITY_TYPES;
    }

    @Override
    public Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityType)
            throws ContentProviderException {
        final Article article = new Article();

        final GenericComponent component = componentPresentation.getComponent();
        final Map<String, Field> content = component.getContent();

        article.setHeadline(getStringValue(content, "headline"));

        // TODO: image, date, description

//        final List<Paragraph> articleBody = new ArrayList<>();
//        for (FieldSet fieldSet : getEmbeddedValues(content, "articleBody")) {
//            if (fieldSet.getSchema().getRootElement().equals("Paragraph")) {
//                articleBody.add(createParagraph(fieldSet));
//            }
//        }
//        article.setArticleBody(articleBody);

        return article;
    }

    private Paragraph createParagraph(FieldSet fieldSet) {
        final Paragraph paragraph = new Paragraph();

        final Map<String, Field> content = fieldSet.getContent();

        paragraph.setSubheading(getStringValue(content, "subheading"));
        paragraph.setContent(getStringValue(content, "content"));

        // TODO: media

        paragraph.setCaption(getStringValue(content, "caption"));

        return paragraph;
    }
}
