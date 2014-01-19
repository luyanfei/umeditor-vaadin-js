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
//	loadjscssfile("../umeditor/jquery-1.10.2.min.js", "js");
//	loadjscssfile("../umeditor/umeditor.config.js", "js");
//	loadjscssfile("../umeditor/umeditor.min.js", "js");
})();