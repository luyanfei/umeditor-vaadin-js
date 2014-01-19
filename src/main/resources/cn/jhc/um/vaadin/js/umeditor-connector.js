function umeditor_init(element) {
	var child = document.getElementById("myeditor01");
	if (child == null)
		element.innerHTML = "<textarea id=\"myeditor01\" style=\"width:700px;height:300px;\"></textarea>";
}

window.cn_jhc_um_vaadin_js_UMEditorField = function() {
	console.log("connectid:" + this.getConnectorId() + ",parentid:" + this.getParentId());
	umeditor_init(this.getElement());

	var connector = this;
	var editor = window.UM.getEditor("myeditor01");

	// editor.addListener("contentchange", function(){
	// console.log("contentchange: " +
	// document.getElementsByClassName("edui-body-container")[0].innerHTML);
	// });
	connector.updateValue = function() {

		// console.log("editor,updateValue:" + editor.getContent());
		// editor.ready(function(){
		// console.log("editor,updateValue:" + editor.getContent());
		// });
//		console
//				.log("updatevalue: "
//						+ document
//								.getElementsByClassName("edui-body-container")[0].innerHTML);
		connector.onValueChange(editor.getContent());
	};
	var elm = this.getElement();
	editor.ready(function(){
	console.log("inner:" + elm.innerHTML);
	var umcontainer = elm.getElementsByClassName("edui-body-container")[0];
	umcontainer.onblur = function(){
		console.log("onblur called: ");
		connector.updateValue();
	};
	});

	
	connector.onStateChange = function() {
		var newvalue = connector.getState().value;
		if (connector.oldvalue !== newvalue) {
			editor.ready(function() {
				editor.setContent(newvalue);
			});
			connector.oldvalue = newvalue;
		}

	};
};