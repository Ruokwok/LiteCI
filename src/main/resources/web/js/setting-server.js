load();
function load() {
    showLoading();
    post('/api4/setting/server/get', undefined, function (json) {
        console.log(json);
        closeLoading();
        $('#port').val(json.http_port);
        var d = '';
        for (var i in json.domains) {
            d += json.domains[i] + "\n";
        }
        $("#domain").val(d.trim());
        $("#task").val(json.task_count);
        $("#timeout").val(json.build_timeout);
        $("#a_get_item").attr("checked", json.anonymous.get_item);
        $("#a_set_item").attr("checked", json.anonymous.set_item);
        $("#a_download").attr("checked", json.anonymous.download);
        $("#a_build").attr("checked", json.anonymous.build);
        $("#a_user").attr("checked", json.anonymous.user);
        $("#a_setting").attr("checked", json.anonymous.setting);
        $("#r_get_item").attr("checked", json.register.get_item);
        $("#r_set_item").attr("checked", json.register.set_item);
        $("#r_download").attr("checked", json.register.download);
        $("#r_build").attr("checked", json.register.build);
        $("#r_user").attr("checked", json.register.user);
        $("#r_setting").attr("checked", json.register.setting);
        $("#ssl").attr("checked", json.ssl);
        $("#ssl-pwd").val(json.keystore_password);
    });
}

function save() {
    showLoading();
    var data = {}
    data.http_port = $('#port').val();
    data.domains = $('#domain').val().trim().split("\n");
    data.task_count = $('#task').val();
    data.build_timeout = $('#timeout').val();
    data.anonymous = {};
    data.anonymous.get_item = $('#a_get_item').is(':checked');
    data.anonymous.set_item = $('#a_set_item').is(':checked');
    data.anonymous.build = $('#a_build').is(':checked');
    data.anonymous.download = $('#a_download').is(':checked');
    data.anonymous.setting = $('#a_setting').is(':checked');
    data.anonymous.user = $('#a_user').is(':checked');
    data.register = {};
    data.register.get_item = $('#r_get_item').is(':checked');
    data.register.set_item = $('#r_set_item').is(':checked');
    data.register.build = $('#r_build').is(':checked');
    data.register.download = $('#r_download').is(':checked');
    data.register.setting = $('#r_setting').is(':checked');
    data.register.user = $('#r_user').is(':checked');
    data.ssl = $("#ssl").is(':checked');
    data.keystore_password = $("#ssl-pwd").val();
    post('/api4/setting/server/set', data, function (json) {
        console.log(json);
        closeLoading
    });
}