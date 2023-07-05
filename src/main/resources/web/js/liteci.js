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