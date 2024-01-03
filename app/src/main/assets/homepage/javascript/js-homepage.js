
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
		mReferenceHTML += '<a href="'+mObject[ReferenceWebsitesDataID.mUrl]+'" class="gc--clear-selection" style="text-decoration:none;"> <div class="home__reference"> <h6 class="home__reference--header">'+mObject[ReferenceWebsitesDataID.mHeader]+'</h6> <p class="home__reference_info">' + mObject[ReferenceWebsitesDataID.mBody] + '</p><div class="home__reference-image-container"> <img class="home__reference-image" alt="" src="'+ mObject[ReferenceWebsitesDataID.mIcon] +'"/> </div><p class="home__reference-status"><b>Status | <span class="home__reference-status--color">running</span></b></p></div></a>'
		mIDCounter+=1;
	});


	var mReferenceID = document.getElementById(UIID.mReferenceWebsites);
	mReferenceID.innerHTML = mReferenceHTML;

	document.getElementById('mReferenceWebsites').className = 'show';
  }

  onLoadStaticWebpage(pData){
  	if(this.mLastLinkID.localeCompare("") != 0){
		document.getElementById(this.mLastLinkID).style.backgroundColor = "#fff";
  	}

	document.getElementById(pData[0]).style.backgroundColor = "#f2f2f2";
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

  onLoadSearchEngine(pData) {
    var event = window.event || event;
    if (event.key === 'Enter' || event.keyCode === 13) {
      const inputValue = pData.value;
      const url = 'http://juhanurmihxlp77nkq76byazcldy2hlmovfu2epvl5ankdibsot4csyd.onion/search?q=' + encodeURIComponent(inputValue);
      window.location.href = url;
    }
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
	else if(pCommand == Commands.onLoadSearchEngine){
		mHomepageLoader.onLoadSearchEngine(pData)
	}
}

/*Default Loaders*/
$(window).on('load', function() {
	/* For Local Testing */
	document.getElementById("pBody").classList.toggle('body_fadein');

	setTimeout(function (){
	onTriggerScriptHandler(Commands.onLoadReferenceWebsites, null)
	}, 1000); 
});
