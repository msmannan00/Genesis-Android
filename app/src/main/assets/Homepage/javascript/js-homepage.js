
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
		mReferenceHTML += '<div class="hi_reference"><div class="clearfix content-heading"> <img style="float:left" src='+mObject[ReferenceWebsitesDataID.mIcon]+' alt="" /><h6 style="margin-left: 40px;padding-top:5px">'+mObject[ReferenceWebsitesDataID.mHeader]+'</h5><p style="margin-left: 0px;line-height: 18px;margin-top: 20px;color: #8c8c8c;font-size: 15px">'+mObject[ReferenceWebsitesDataID.mBody]+'</p></div></div>'
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
	onTriggerScriptHandler(Commands.onLoadReferenceWebsites)
});
