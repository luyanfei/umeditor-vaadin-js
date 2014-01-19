function loadjscssfile(filename, filetype) {
	if (filetype == "js") { // if filename is a external JavaScript file
		var fileref = document.createElement('script');
		fileref.setAttribute("type", "text/javascript");
		fileref.setAttribute("src", filename);
	} else if (filetype == "css") { // if filename is an external CSS file
		var fileref = document.createElement("link");
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", filename);
	}
	if (typeof fileref != "undefined")
		document.getElementsByTagName("head")[0].appendChild(fileref);
}

(function(){
	var servletcontext = /^https?:\/\/[^\/]+(\/[^\/]+)\/.*/.exec(window.location.href)[1];
	loadjscssfile(servletcontext + "/umeditor/themes/default/css/umeditor.css","css");
})();

window.cn_jhc_um_vaadin_js_UMEditorField = function() {
//	console.log("connectid:" + this.getConnectorId() + ",parentid:"
//			+ this.getParentId());
	var elm = this.getElement();
	var um_textarea_id = "myumeditor" + this.getConnectorId();
	if(document.getElementById(um_textarea_id) == null){
		elm.innerHTML = "<textarea id=\"" + um_textarea_id + "\" style=\"width:700px;height:300px;\"></textarea>"
	}
	var connector = this;
	var editor = window.UM.getEditor(um_textarea_id);

	connector.updateValue = function() {
		var newvalue = editor.getContent();
		if(connector.old_editor_value !== newvalue) {
			connector.onValueChange(newvalue);
			connector.old_editor_value = newvalue;
		}
	};
	
	editor.ready(function() {
		var umcontainer = elm.getElementsByClassName("edui-body-container")[0];
		umcontainer.onblur = function() {
			console.log("onblur called: ");
			connector.updateValue();
		};
	});

	connector.onStateChange = function() {
		var newvalue = connector.getState().value;
		if (connector.old_state_value !== newvalue) {
			editor.ready(function() {
				editor.setContent(newvalue);
			});
			connector.old_state_value = newvalue;
		}
	};
};