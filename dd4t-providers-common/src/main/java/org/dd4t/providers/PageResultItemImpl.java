package org.dd4t.providers;

public class PageResultItemImpl extends StringResultItemImpl implements PageProviderResultItem<String>{

	private String url;
	public PageResultItemImpl(int pubid, int itemid, String url) {
		super(pubid, itemid);
		
		this.url = url;
	}
	
	@Override
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
