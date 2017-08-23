package org.dd4t.providers;


public class ComponentPresentationResultItemImpl extends StringResultItemImpl  implements
ComponentPresentationResultItem<String>{

	int templateId;
	
	public ComponentPresentationResultItemImpl(int pubid, int itemid, int templateid) {
		super(pubid, itemid);
		this.templateId = templateid;
	}

	@Override
	public int getTemplateId() {
		return templateId;
	}

	@Override
	public void setTemplateId(int id) {
		this.templateId = id;
	}


}
