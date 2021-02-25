
/*Homepage Classes*/

class homepage {

  constructor() {
  }

  /*Helper Methods*/

  onLoadReferenceWebsites(){
	document.getElementById('mReferenceWebsites').className = 'hide';
  }

  onLoadReferenceWebsiteContent(mJson){
  	var mResponseJson = mJson;
	var obj = JSON.parse(mResponseJson);
	var mReferenceHTML = strings.emptyString;

	Object.keys(obj).forEach(function(key) {
		var mObject = obj[key];
		mReferenceHTML += '<div class="hi_reference" onclick="location.href=\' ' + mObject[ReferenceWebsitesDataID.mUrl] + ' \';"><div class="hi_reference_website content-heading"> <div class="hi_image_container"><img class="hi_reference_image" alt="&#10063;" src="'+mObject[ReferenceWebsitesDataID.mIcon]+'"/></div><h6 class="hi_reference_header">'+mObject[ReferenceWebsitesDataID.mHeader]+'</h5><p class="hi_reference_body">'+mObject[ReferenceWebsitesDataID.mBody]+'</p></div></div>'
	});


	var mReferenceID = document.getElementById(UIID.mReferenceWebsites);
	mReferenceID.innerHTML = mReferenceHTML;

	document.getElementById('mReferenceWebsites').className = 'show';
  }

  /*Ajax Request*/

  onParseReferenceWebsites() {
  	var $_GET=[];
	decodeURIComponent(window.location.href).replace(/[?&]+([^=&]+)=([^&]*)/gi,function(a,name,value){$_GET[name]=value;});
  	
	setTimeout(mHomepageLoader.onLoadReferenceWebsites, 500);
	setTimeout(mHomepageLoader.onLoadReferenceWebsiteContent, 1000, $_GET[GET.pData]);
  }

}

let mHomepageLoader = new homepage();

/*Helper Classes Manager*/
function onTriggerScriptHandler(pCommand) {
	if(pCommand == Commands.onLoadReferenceWebsites){
		mHomepageLoader.onParseReferenceWebsites()
	}
}

/*Default Loaders*/
$(window).on('load', function() {
	/* For Local Testing */
	 //var mResponseJson = '[{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://wikileaks.org/static/img/wl-logo.png", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"}]';
	 //setTimeout(mHomepageLoader.onLoadReferenceWebsites, 500);
	 //setTimeout(mHomepageLoader.onLoadReferenceWebsiteContent, 1000, mResponseJson);

	onTriggerScriptHandler(Commands.onLoadReferenceWebsites)
});
