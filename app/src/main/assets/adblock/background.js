function logURL(requestDetails) {
  let redirectUrl = "https://google.com";
  return {redirectUrl};
}

browser.webRequest.onBeforeRequest.addListener(
      function(info) {
        var denyRequest = true;
        return {cancel: denyRequest}
    },

{ 
    urls: ["<all_urls>"],
    types: ["ping"],
},
["blocking"]);