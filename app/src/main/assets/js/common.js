//获取当前时间，格式YYYY-MM-DD
function getNowFormatDate() {
	var date = new Date();
	var seperator1 = "-";
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var strDate = date.getDate();
	if(month >= 1 && month <= 9) {
		month = "0" + month;
	}
	if(strDate >= 0 && strDate <= 9) {
		strDate = "0" + strDate;
	}
	var currentdate = year + seperator1 + month + seperator1 + strDate;
	return currentdate;
}

//if(window.location.pathname.indexOf("download.html") <= 0 && window.location.pathname.indexOf("forget.html") <= 0 && window.location.pathname.indexOf("register.html") <= 0 && window.location.pathname.indexOf("index.html") <= 0 && window.location.pathname.indexOf("login.html") <= 0 && !localStorage.getItem("uid")) {
//	window.location.href = "login.html"
//}

function isWeixinBrowser() {
	var agent = navigator.userAgent.toLowerCase();
	if(agent.match(/MicroMessenger/i) == "micromessenger") {
		return 1;
	} else {
		return 0;
	}
}

//  url = "http://"+location.host
var url = "http://article.581vv.com/"

turl = url
var dataRequest = {

	//ajax请求数据
	method: function(murl, mdata, method, success) {

		mdata['token'] = '0e468854ec9859feb51f7a08d51db106'
		$.ajax({
			type: method,
			url: url + murl,
			data: mdata,
			timeout: 20000,
			beforeSend: function(XMLHttpRequest) {
				mui.showLoading("正在加载..", "div");
				//$("body").append('<div id="pload" style="position:fixed;top:30%;z-index:1200;background:url(/Public/home/images/lod.gif) top center no-repeat;width:100%;height:140px;margin:auto auto;"></div>');
			},

			success: function(data) {
				mui.hideLoading("正在加载..", "div");
				//$("#pload").remove();
				success ? success(data) : function() {};
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {

				console.log(XMLHttpRequest.status);
				console.log(XMLHttpRequest.readyState);
				console.log(textStatus);
			}

		});
	}
}

//wx授权
function wxau() {
	var host = window.location.host
	var uid = localStorage.getItem('uid')
	var path = encodeURIComponent("http://" + host + "/is_wx_qauthorization?uid=" + uid + "&url=" + window.location.href); //登录后回调的地址
	var state = 1;
	var appid = 'wx07b816836b65046b'; //注册申请的appid
	var url = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + appid + '&redirect_uri=' + path + '&response_type=code&scope=snsapi_userinfo&state=' + state + '&connect_redirect=1#wechat_redirect';
	window.location.href = url
}

function dialog(content, title, url, time) {

	if(title == undefined) title = 'alert';
	if(url == undefined) url = '';
	if(time == undefined) time = 1000;
	$.dialog({
		content: content,
		title: title,
		time: time
	});

	if(url == 1) {
		window.setTimeout('javascript:location.reload()', time);
	} else if(url == 2) {
		window.setTimeout('javascript:history.back()', time);
	} else if(url != '') {
		window.setTimeout("javascript:location.href='" + url + "'", time);
	}
}

//JavaScript函数：
var minute = 1000 * 60;
var hour = minute * 60;
var day = hour * 24;
var halfamonth = day * 15;
var month = day * 30;

function getDateDiff(dateTimeStamp) {
	var now = new Date().getTime();
	var diffValue = now - dateTimeStamp;
	if(diffValue < 0) {
		//若日期不符则弹出窗口告之
		//alert("结束日期不能小于开始日期！");
	}
	var monthC = diffValue / month;
	var weekC = diffValue / (7 * day);
	var dayC = diffValue / day;
	var hourC = diffValue / hour;
	var minC = diffValue / minute;
	if(monthC >= 1) {
		result = parseInt(monthC) + "个月前";
	} else if(weekC >= 1) {
		result = parseInt(weekC) + "周前";
	} else if(dayC >= 1) {
		result = parseInt(dayC) + "天前";
	} else if(hourC >= 1) {
		result = parseInt(hourC) + "个小时前";
	} else if(minC >= 1) {
		result = parseInt(minC) + "分钟前";
	} else
		result = "刚刚";
	return result;
}

function getDateTimeStamp(dateStr) {
	return Date.parse(dateStr.replace(/-/gi, "/"));
}

function init() {

	var images = document.images;
	// 加载首屏图片
	for(var i = 0, len = images.length; i < len; i++) {
		var obj = images[i];
		// 如果在可视区域并且还没被加载过
		if(obj.getBoundingClientRect().top < document.documentElement.clientHeight && !obj.isLoad) {
			obj.isLoad = true;
			// 先调用 HTML5 方法
			if(obj.dataset)
				imageLoaded(obj, obj.dataset.original);
			else
				imageLoaded(obj, obj.getAttribute('data-original'));
		} else { // 假设图片标签在 HTML 中的顺序和实际页面中顺序一致
			break;
		}
	}
}

function imageLoaded(obj, src) {
	var img = new Image();
	img.onload = function() {
		obj.src = src;
	};
	img.src = src;
}

window.onscroll = function(e) {

	var e = e || window.event;
	lazyload();

};

function lazyload() {

	var lazy = 0;
	var images = document.images;
	for(var i = 0, len = images.length; i < len; i++) {
		var obj = images[i];
		if(obj.getBoundingClientRect().top - lazy < document.documentElement.clientHeight && !obj.isLoad) {
			obj.isLoad = true;
			if(obj.dataset)
				imageLoaded(obj, obj.dataset.original);
			else
				imageLoaded(obj, obj.getAttribute('data-original'));
		}
	}
}

function getParameterByName(name, url) {
	if(!url) url = window.location.href;
	name = name.replace(/[\[\]]/g, "\\$&");
	var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)", 'gi'),
		results = regex.exec(url);
	if(!results) return null;
	if(!results[2]) return '';
	return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function detail(obj, url) {
	var id = $(obj).attr("data-id")
	window.location.href = url + "?id=" + id
}
$pic = [];

function ajaxFileUpload(id, aurl, id1, uid) {
	document.domian = ""
	mui.showLoading("上传处理中..", "div");
	$.ajaxFileUpload({
		url: url + aurl, //处理图片脚本
		type: 'GET',
		secureuri: false,
		fileElementId: [id1], //file控件id。就是input type="file" id="image2"
		dataType: 'json',
		success: function(pic, status) {
			console.log(pic)
			if(pic.code == 200) {

				$("#" + id).prev().attr("src", turl + pic.result)
				$("#" + id).next().val(pic.result);
				dataRequest.method("/userData", {
					'user_id': uid,
					'user_pic': pic.result
				}, "post", function(res) {
					if(res.code == 200) {
						mui.hideLoading();
						uinfo = JSON.parse(localStorage.getItem("user_info"))
						uinfo.user_pic = pic.result
						localStorage.setItem("user_info", JSON.stringify(uinfo))
						mui.toast('修改成功')
					}
				})

			} else {
				mui.hideLoading();
				mui.toast('上传失败！');
			}
		},
		error: function(data, status, e) {

			console.log(e)
		}
	})

}
//上拉加载
isFirst = true;
/*
 * count 总页数
 * api   接口名
 * data  参数
 * */
function data_lod(count, api, data, token, num) {

	var pagesize = count; //总页数
	var currentpage = 1; //当前页数默认第一页
	var stop = true;
	var totalheight;
	if(pagesize <= 1) {
		//		$(".loadMore").hide();
		$('.loadMore').html("-- END --"); //总页数小于2则不显示加载更多
	}
	if(currentpage >= pagesize) {
		$('.loadMore').html("-- END --"); //总页数小于2则不显示加载更多
	} else {
		$('.loadMore').html('<img src="images/loading@2x.png" style="height:1rem;width:auto">加载更多');
		//		$(window).scroll(function() {
		window.onscroll = function() { //默认一个页面只能同时存在一个window.onscroll函数
			
			totalheight = parseFloat($(window).height()) + $(window).scrollTop();
			if($(window).height() <= totalheight) {
				
				if(stop == true) {
					stop = false;
					// 这里加载数据.. 
					$(".loadMore").addClass("animate");

					data['page'] = currentpage
					data['pagesize'] = pagesize
					dataRequest.method(api, data, "post", function(res) {

						if(res.code == 200) {
							currentpage++;
							if(currentpage > 1) {
								isFirst = true;
							}
							if(num != undefined && data.cat_id != undefined && localStorage.getItem('tid') != data.cat_id) {
								isFirst = false;
							} else {
								isFirst = true;
							}

							if(isFirst && res.result) { //防止重复点击

								setTimeout(function() {

									var html = template('list-template', {
										items: res.result,
										url: turl,
										token: token
									})
									$(".loadMore").show()
									if(token != '') {
										$(".v" + token).before(html)
									}
									if(token == undefined || !token || token == '') {
										$(".loadMore").before(html)
									}

									//$("img").lazyload();

								}, 200);

							} else {

								$('.loadMore').html("-- END --"); //加载所有页完毕去掉按钮
							}

						} else {
							$('.loadMore').html("-- END --"); //加载所有页完毕去掉按钮
						}
						if(currentpage >= pagesize) {
							$('.loadMore').html("-- END --"); //加载所有页完毕去掉按钮
						}
						stop = true;
					})

				}

			}

		}

	}

}

//验证码倒计时
function r_time() {
	var step = 59;
	$('#sms').text('重新发送60');
	$("#sms").attr("disabled", true); //设置disabled属性
	var res = setInterval(function() {
		$("#sms").attr("disabled", true); //设置disabled属性
		$('#sms').text('重新发送' + step);
		step--;

		if(step <= 0) {
			$("#sms").removeAttr("disabled"); //移除disabled属性
			$('#sms').text('获取验证码');
			clearInterval(res); //清除setInterval
		}
	}, 1000);
}

function wx_pay(json, paydata, return_url) {

	wx.config(json);
	wx.ready(function() {

		wx.chooseWXPay({
			appId: paydata.appId,
			timestamp: paydata.timestamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符  
			nonceStr: paydata.nonceStr, // 支付签名随机串，不长于 32 位  
			package: paydata.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）  
			signType: paydata.signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'  
			paySign: paydata.paySign, // 支付签名
			success: function(res) {

				// 支付成功后的回调函数  
				if(res.errMsg == "chooseWXPay:ok") {
					//支付成功 
					alert('支付成功');
					setTimeout(function() {
						window.location.href = return_url
					}, 100)

				} else {

					alert(res.errMsg);
				}
			},
			cancel: function(res) {
				//支付取消  
				alert('支付取消');
			},
			fail: function(res) {

			}
		});
	});

}
if(isWeixinBrowser()) {
	if($('a').attr('class') == 'mui-action-back') {
		$('a').attr('class', 'action-back')
		$('a').attr('href', 'javascript:;')
	}
} else {
	mui('body').on('tap', 'a', function() {
		document.location.href = this.href;
	});
}
$(document).on('tap', '.action-back', function() {
	mui.back()
})