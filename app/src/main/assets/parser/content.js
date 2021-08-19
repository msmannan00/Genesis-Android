console.log(`content:start`);
let JSBridge = {
    postMessage: function (message) {
        browser.runtime.sendMessage({
            action: "JSBridge",
            data: message
        });
    }
}
window.wrappedJSObject.JSBridge = cloneInto(
    JSBridge,
    window,
    { cloneFunctions: true });

browser.runtime.onMessage.addListener((data, sender) => {
    console.log("content:eval:" + data);
    if (data.action === 'evalJavascript') {
        return Promise.resolve(document.documentElement.innerHTML);
    }
});