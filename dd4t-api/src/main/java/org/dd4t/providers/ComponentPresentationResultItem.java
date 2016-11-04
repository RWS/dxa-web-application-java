package org.dd4t.providers;

public interface ComponentPresentationResultItem<T> extends
		ProviderResultItem<T> {

	int getTemplateId();
	
	void setTemplateId(int id);
}
