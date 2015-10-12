package com.sdl.webapp.tridion.xpm;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.tridion.EntityBuilder;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;

/**
 * Created by Administrator on 10/7/2015.
 */
public abstract class DefaultModelBuilder implements EntityBuilder {

    /**
     * Determine MVC data such as view, controller and area name from a Component Presentation
     * @param cp The DD4T Component Presentation
     * @return MVC data
     */
    public static MvcData getMvcData(ComponentPresentation cp)
    {
            MvcData mvcData = null;
//        IComponentTemplate template = cp.ComponentTemplate;
//        string viewName = Regex.Replace(template.Title, @"\[.*\]|\s", String.Empty);
//
//        if (template.MetadataFields != null)
//        {
//            if (template.MetadataFields.ContainsKey("view"))
//            {
//                viewName = template.MetadataFields["view"].Value;
//            }
//        }
//
//        MvcData mvcData = CreateViewData(viewName);
//        // defaults
//        mvcData.ControllerName = SiteConfiguration.GetEntityController();
//        mvcData.ControllerAreaName = SiteConfiguration.GetDefaultModuleName();
//        mvcData.ActionName = SiteConfiguration.GetEntityAction();
//        mvcData.RouteValues = new Dictionary<string, string>();
//
//        // TODO: remove code duplication of splitting area and controller/region names
//        if (template.MetadataFields != null)
//        {
//            if (template.MetadataFields.ContainsKey("controller"))
//            {
//                string[] controllerNameParts = template.MetadataFields["controller"].Value.Split(':');
//                if (controllerNameParts.Length > 1)
//                {
//                    mvcData.ControllerName = controllerNameParts[1];
//                    mvcData.ControllerAreaName = controllerNameParts[0];
//                }
//                else
//                {
//                    mvcData.ControllerName = controllerNameParts[0];
//                }
//            }
//            if (template.MetadataFields.ContainsKey("regionView"))
//            {
//                string[] regionNameParts = template.MetadataFields["regionView"].Value.Split(':');
//                if (regionNameParts.Length > 1)
//                {
//                    mvcData.RegionName = regionNameParts[1];
//                    mvcData.RegionAreaName = regionNameParts[0];
//                }
//                else
//                {
//                    mvcData.RegionName = regionNameParts[0];
//                    mvcData.RegionAreaName = SiteConfiguration.GetDefaultModuleName();
//                }
//            }
//            if (template.MetadataFields.ContainsKey("action"))
//            {
//                mvcData.ActionName = template.MetadataFields["action"].Value;
//            }
//            if (template.MetadataFields.ContainsKey("routeValues"))
//            {
//                string[] routeValues = template.MetadataFields["routeValues"].Value.Split(',');
//                foreach (string routeValue in routeValues)
//                {
//                    string[] routeValueParts = routeValue.Trim().Split(':');
//                    if (routeValueParts.Length > 1 && !mvcData.RouteValues.ContainsKey(routeValueParts[0]))
//                    {
//                        mvcData.RouteValues.Add(routeValueParts[0], routeValueParts[1]);
//                    }
//                }
//            }
//        }
//        else
//        {
//            // fallback if no meta - use the CT title to determine region view name and area name
//            Match match = Regex.Match(template.Title, @".*?\[(.*?)\]");
//            if (match.Success)
//            {
//                string module;
//                string regionName = DetermineRegionViewNameAndModule(match.Groups[1].Value, out module);
//                mvcData.RegionName = regionName;
//                mvcData.RegionAreaName = module;
//            }
//        }

        return mvcData;
    }
}
