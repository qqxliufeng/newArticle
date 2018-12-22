(function (doc, win) {
    var docEl = doc.documentElement,    
    resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',    
    recalc = function () {    
    	var clientWidth = docEl.clientWidth;    
    	if (!clientWidth) return;    
    	docEl.style.fontSize = 20 * (clientWidth / 375) + 'px';   
    	//判断设备密度     delete 2016/8/9 yanpeihong
//		if(window.devicePixelRatio == 3 || window.devicePixelRatio > 3){
//			docEl.setAttribute("data-dpr","3")
//		}else if(window.devicePixelRatio == 2){
//			docEl.setAttribute("data-dpr","2")
//		}
//		else if(window.devicePixelRatio == 1){
//			docEl.setAttribute("data-dpr","1")
//		}
	};    
	if (!doc.addEventListener) return;    
	win.addEventListener(resizeEvt, recalc, false);    
	doc.addEventListener('DOMContentLoaded', recalc, false);    
})(document, window);

//console.log(window.devicePixelRatio);
