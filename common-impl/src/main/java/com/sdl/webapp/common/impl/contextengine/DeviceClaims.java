package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;


public class DeviceClaims extends ContextClaims{

	@Override
	protected String getAspectName()
	{
		return "device";
	}


	public int getDisplayHeight()
	{
		return getClaimValue("displayHeight", Integer.class);
	}

	public int getDisplayWidth()
	{
		return getClaimValue("displayWidth", Integer.class); 
	}

	public boolean getIsMobile()
	{
		return getClaimValue("mobile", Boolean.class); 
	}

	public String getModel()
	{
		return getClaimValue("model", String.class); 
	}

	public int getPixelDensity()
	{
		return getClaimValue("pixelDensity", Integer.class); 
	}

	public double getPixelRatio()
	{
		return getClaimValue("pixelRatio", Double.class); 
	}

	public boolean getIsRobot()
	{
		return getClaimValue("robot", Boolean.class); 
	}

	public boolean getIsTablet()
	{
		return getClaimValue("tablet", Boolean.class); 
	}

	public String getVariant()
	{
		return getClaimValue("variant", String.class); 
	}

	public String getVendor()
	{
		return getClaimValue("vendor", String.class); 
	}

	public String getVersion()
	{
		return getClaimValue("version", String.class);
	}

}
