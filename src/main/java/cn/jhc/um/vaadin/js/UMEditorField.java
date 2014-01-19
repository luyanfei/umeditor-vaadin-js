package cn.jhc.um.vaadin.js;


import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;

@JavaScript({"http://libs.baidu.com/jquery/1.10.2/jquery.min.js","umeditor.config.js", 
	"umeditor.js","umeditor-connector.js"})
public class UMEditorField extends AbstractJavaScriptComponent {
	
	public UMEditorField() {
		addFunction("onValueChange", new JavaScriptFunction() {
			
			@Override
			public void call(JSONArray arguments) throws JSONException {
				setValue(arguments.getString(0));
			}
		});
	}
	
	@Override
	protected UMEditorState getState() {
		return (UMEditorState) super.getState();
	}
	
	public void setValue(String newValue) {
		getState().value = newValue;
		System.err.println("setValue is invoked: " + newValue);
	}
	
	public String getValue() {
		return getState().value;
	}
}
