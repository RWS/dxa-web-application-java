package com.sdl.dxa.tridion.models.entity;

import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.joda.time.DateTime;

public class Article extends AbstractEntityModel {

//}    [SemanticEntity(Vocab = SchemaOrgVocabulary, EntityName= "Article", Prefix= "s", Public=true)]
    //public class Article : EntityModel
    //{
        //[SemanticProperty("s:headline")]
        public String Headline;
        //[SemanticProperty("s:image")]
        public Image Image;
        //[SemanticProperty("s:dateCreated")]
        public DateTime Date;
        //[SemanticProperty("s:about")]
        public String Description;

        //[SemanticProperty("s:articleBody")]
        //public List<Paragraph> ArticleBody;
    }
