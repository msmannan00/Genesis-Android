
/*Homepage Classes*/

class homepage {

  mLastLinkID = "";

  constructor() {
  }

  /*Helper Methods*/

  onLoadReferenceWebsites(){
	document.getElementById('mReferenceWebsites').className = 'hide';
  }

  onLoadReferenceWebsiteContent(mJson){
  	var mResponseJson = mJson;
	var mOBJ = JSON.parse(mResponseJson);
	var mReferenceHTML = strings.emptyString;

	var mIDCounter = 0;
	Object.keys(mOBJ).forEach(function(key) {
		var mObject = mOBJ[key];
		mReferenceHTML += '<div id="'+ mIDCounter +'" class="hi_reference" onclick="onTriggerScriptHandler(\'onClickReferenceWebsite\',[\''+ mIDCounter +'\',\''+ mObject[ReferenceWebsitesDataID.mUrl] +'\'])"><div class="hi_reference_website content-heading"> <div class="hi_image_container"><img class="hi_reference_image" alt="&#10063;" src="'+mObject[ReferenceWebsitesDataID.mIcon]+'"/></div><h6 class="hi_reference_header">'+mObject[ReferenceWebsitesDataID.mHeader]+'</h5><p class="hi_reference_body">'+mObject[ReferenceWebsitesDataID.mBody]+'</p></div></div>'
		mIDCounter+=1;
	});


	var mReferenceID = document.getElementById(UIID.mReferenceWebsites);
	mReferenceID.innerHTML = mReferenceHTML;

	document.getElementById('mReferenceWebsites').className = 'show';
  }

  onLoadStaticWebpage(pData){
  	if(this.mLastLinkID.localeCompare("") != 0){
		document.getElementById(this.mLastLinkID).style.backgroundColor = "#1c1b21";
  	}

	document.getElementById(pData[0]).style.backgroundColor = "#18171c";
	window.open(pData[1],"_self");
	this.mLastLinkID = pData[0];
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
function onTriggerScriptHandler(pCommand,pData) {
	if(pCommand == Commands.onLoadReferenceWebsites){
		mHomepageLoader.onParseReferenceWebsites()
	}
	else if(pCommand == Commands.onClickReferenceWebsite){
		mHomepageLoader.onLoadStaticWebpage(pData)
	}
}

/*Default Loaders*/
$(window).on('load', function() {
	/* For Local Testing */
	
	// var mResponseJson = '[{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://wikileaks.org/static/img/wl-logo.png", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"},{ "mIcon":"https://cdn.sstatic.net/Sites/stackoverflow/Img/favicon.ico?v=ec617d715196", "mHeader":"Experience", "mBody":"Donec id elit non mi porta gravida at eget metus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui"}]';
	// setTimeout(mHomepageLoader.onLoadReferenceWebsites, 500);
	// setTimeout(mHomepageLoader.onLoadReferenceWebsiteContent, 1000, mResponseJson);
	document.getElementById("pBody").classList.toggle('body_fadein');

	setTimeout(function (){
	onTriggerScriptHandler(Commands.onLoadReferenceWebsites, null)
	}, 100); 
});
