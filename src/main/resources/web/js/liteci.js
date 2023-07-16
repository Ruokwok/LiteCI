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
    loadingTab.close();
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
      var h = date.getHours() + ":";
      var m = date.getMinutes() + ":";
      var s = date.getSeconds();
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