package com.sdl.dxa.tridion.models.entity;

import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;

public class Image extends MediaItem {

//        [SemanticProperty("s:name")]
//        [SemanticProperty("c:altText")]
        public String AlternateText;
//
//        public override string ToHtml(string widthFactor, double aspect = 0, string cssClass = null, int containerSize = 0)
//        {
//        }
//
//        /// <summary>
//        /// Read additional properties from XHTML element.
//        /// </summary>
//        /// <param name="xhtmlElement">XHTML element</param>
//        public override void ReadFromXhtmlElement(XmlElement xhtmlElement)
//        {
//            base.ReadFromXhtmlElement(xhtmlElement);
//
//            AlternateText = xhtmlElement.GetAttribute("alt");
//        }
//
//        /// <summary>
//        /// Gets the default View.
//        /// </summary>
//        /// <param name="localization">The context Localization</param>
//        /// <remarks>
//        /// This makes it possible possible to render "embedded" Image Models using the Html.DxaEntity method.
//        /// </remarks>
//        public override MvcData GetDefaultView(Localization localization)
//        {
//            return new MvcData("Core:Image");
//        }

    @Override
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return null;
    }

        /// <summary>
        /// Renders an HTML representation of the Media Item.
        /// </summary>
        /// <param name="widthFactor">The factor to apply to the width - can be % (eg "100%") or absolute (eg "120").</param>
        /// <param name="aspect">The aspect ratio to apply.</param>
        /// <param name="cssClass">Optional CSS class name(s) to apply.</param>
        /// <param name="containerSize">The size (in grid column units) of the containing element.</param>
        /// <returns>The HTML representation.</returns>
        /// <remarks>
        /// This method is used by the <see cref="IRichTextFragment.ToHtml()"/> implementation in <see cref="MediaItem"/> and by the HtmlHelperExtensions.Media implementation.
        /// Both cases should be avoided, since HTML rendering should be done in View code rather than in Model code.
        /// </remarks>
    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
//        if (String.IsNullOrEmpty(Url))
//            return String.Empty;
//
//
//        String responsiveImageUrl = SiteConfiguration.MediaHelper.GetResponsiveImageUrl(Url, aspect, widthFactor, containerSize);
//        String dataAspect = (Math.Truncate(aspect * 100) / 100).ToString(CultureInfo.InvariantCulture);
//        String widthAttr = string.IsNullOrEmpty(widthFactor) ? null : string.Format("width=\"{0}\"", widthFactor);
//        String classAttr = string.IsNullOrEmpty(cssClass) ? null : string.Format("class=\"{0}\"", cssClass);
//
//        HtmlElement result = new HtmlElement(cssClass,
//
//        return String.format("<img src=\"{0}\" alt=\"{1}\" data-aspect=\"{2}\" {3}{4}/>",
//                responsiveImageUrl, AlternateText, dataAspect, widthAttr, classAttr);
        return null;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {
        return null;
    }
}
