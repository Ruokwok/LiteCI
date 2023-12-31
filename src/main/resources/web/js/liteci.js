var loadingTab = new mdui.Dialog('#loading', { destroyOnClosed: true, modal: true, history: false});

function decrypt(text, key){
    var decrypted = CryptoJS.AES.decrypt(text, key, {});
    return decrypted.toString(CryptoJS.enc.Utf8);
}

function encrypt(text, key) {
    var ciphertext = CryptoJS.AES.encrypt(text, key, {});
    return ciphertext.toString();
}

function showLoading(text) {
    $("#loading-text").text(text);
    loadingTab.open();
}

function closeLoading() {
    setTimeout(()=>{loadingTab.close()}, 100);
}

function goto(url) {
//    $(location).attr('href', url);
    window.location.href = url;
}

function asyncGoto(url) {
    setTimeout(()=>{goto(url)}, 200);
}

function dialog(str) {
mdui.dialog({
    content: str,
    buttons: [
        {
            text: 'OK',
        }
    ]
});
}

function post(url, data, success) {
    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(data),
        success: function (json) {
            if (json.type == 'dialog') {
                closeLoading();
                dialog(json.content);
            } else {
                success(json);
            }
        },
        dataType: 'json'
    });
}

function toDate(stamp) {
    if (stamp == 0) return "{web.none}";
    var date = new Date(stamp);
      var Y = date.getFullYear() + "-";
      var M =
        (date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1) + "-";
      var D = (date.getDate() < 10 ? "0" + date.getDate() : date.getDate()) + "&nbsp;";
      var h = (date.getHours() < 10? "0" + date.getHours() : date.getHours()) + ":";
      var m = (date.getMinutes() < 10? "0" + date.getMinutes() : date.getMinutes()) + ":";
      var s = (date.getSeconds() < 10? "0" + date.getSeconds() : date.getSeconds());
      return Y + M + D + h + m + s;
}

function toTime(stamp) {
    time = parseInt(stamp / 1000);
    function f_m_dispose(min, sec) { // 分秒处理函数
//		if (min < 10 && sec < 10) {
//			return min + "&nbsp;min&nbsp;" + sec + "&nbsp;sec"; // 如果分和秒都小于10，则前面都加入0
//		} else if (min < 10 && sec >= 10) {
//			return min + "&nbsp;min&nbsp;" + sec + "&nbsp;sec"; // 如果分小于10，秒大于10，则给分前面加0
//		} else if (min >= 10 && sec < 10) {
//			return min + "&nbsp;min&nbsp;" + sec + "&nbsp;sec"; // 如果分大于10，秒小于10，则给秒前面加0
//		} else {
//			return min + "&nbsp;min&nbsp;" + sec + "&nbsp;sec"; // 如果分秒都大于10，则直接return
//		}
		return min + "&nbsp;min&nbsp;" + sec + "&nbsp;sec";
	}
	let hour = Number.parseInt(time / 3600); // 获取总的小时
    let min = Number.parseInt((time - hour * 3600) / 60); // 获取总分钟
    let sec = time - (hour * 3600) - (min * 60); // 减去总 分 后剩余的分秒数
    if (!hour) { // 小时为0时
	    return f_m_dispose(min, sec);
    } else { // 小时大于0的处理
	    if (!min) { // 分为0时
			// 如果秒也小于10，则返回 例1:00:07
		    return sec < 10 ? hour + "&nbsp;hr&nbsp;" + "0" + "&nbsp;min&nbsp;0" + sec + "&nbsp;sec" : hour + "&nbsp;hr&nbsp;" + "00" + "&nbsp;min&nbsp;" + sec;
		} else { // 有分钟时的处理
			return hour + "&nbsp;hr&nbsp;" + f_m_dispose(min, sec); // 返回总小时加上处理好的分秒数
		}
    }
}

function userinfo() {
    if (localStorage.user == undefined) {
        goto('/login');
    } else {
        mdui.dialog({
            title: localStorage.user,
            buttons: [
                {
                    text: '{web.quit}',
                    onClick: function (inst) {
                        if (localStorage.user == null) {
                            goto('/login');
                        } else {
                            post('/api/quit', undefined, function (json) {
                            localStorage.clear();
                            asyncGoto('/');
                        });
                        }
                    }
                },
                {
                    text: '{web.close}',
                }
            ]
        });
    }
}

permission();
function permission() {
    post("/api/permission", undefined, function (json) {
        console.log(json);
        if (json.setting) {
            var s4 = $('.liteci-secure-4');
            for (var i = 0; i <= s4.length; i++) {
                $(s4[i]).show();
            }
        }
        if (json.get_item) {
            var s0 = $('.liteci-secure-0');
            for (var i = 0; i <= s0.length; i++) {
                $(s0[i]).show();
            }
        }
        if (json.download) {
            var s1 = $('.liteci-secure-1');
            for (var i = 0; i <= s1.length; i++) {
                $(s1[i]).show();
            }
        }
        if (json.build) {
            var s2 = $('.liteci-secure-2');
            for (var i = 0; i <= s2.length; i++) {
                $(s2[i]).show();
            }
        }
        if (json.set_item) {
            var s3 = $('.liteci-secure-3');
            for (var i = 0; i <= s3.length; i++) {
                $(s3[i]).show();
            }
        }
        if (json.user) {
            var s5 = $('.liteci-secure-5');
            for (var i = 0; i <= s5.length; i++) {
                $(s5[i]).show();
            }
        }
        if (json.name == undefined) {
            localStorage.removeItem('user');
            localStorage.removeItem('token');
        }
    });
}

function formatSize(bytes) {
    if (isNaN(bytes)) {
        return '';
    }
    var symbols = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    var exp = Math.floor(Math.log(bytes)/Math.log(2));
    if (exp < 1) {
        exp = 0;
    }
    var i = Math.floor(exp / 10);
    bytes = bytes / Math.pow(2, 10 * i);

    if (bytes.toString().length > bytes.toFixed(2).toString().length) {
        bytes = bytes.toFixed(2);
    }
    return bytes + ' ' + symbols[i];
}