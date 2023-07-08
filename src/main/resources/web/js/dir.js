localStorage.path = window.location.pathname.substring(4);
update()
var up_path;
function update() {
    $("#job-loading").show();
    $("#job-list").text('');
    var data = {};
    data.params = {};
    data.params.path = window.location.pathname.substring(5);
    $('#job-list').append('<tr onclick="back()"><td><i class="mdui-icon material-icons mdui-text-color-orange">folder</i></td><td>{web.back}</td><td>{web.none}</td><td>{web.none}</td><td>{web.none}</td><td></td></tr>');
    $.ajax({
        type: 'POST',
        url: '/api/jobs',
        data: JSON.stringify(data),
        success: function (json) {
            up_path = json.father;
            if (json.description != '' && json.description != undefined) {
                $("#description").text(json.description);
                $("#description").show();
            }
            $("#job-loading").hide();
            console.log(json);
            for (i in json.list) {
                if (json.list[i].is_dir) {
                    $('#job-list').append('<tr onclick="goto(\'' + window.location.pathname + '/' + json.list[i].name + '\')")><td>' + getIcon(json.list[i]) + '</td><td>' + json.list[i].name + '</td><td>{web.none}</td><td>{web.none}</td><td>{web.none}</td><td><i class="mdui-icon material-icons">play</i></td></tr>');
                } else {
                    $('#job-list').append('<tr onclick="goto(\'' + window.location.pathname + '/' + json.list[i].name + '\')")><td>' + getIcon(json.list[i]) + '</td><td>' + json.list[i].name + '</td><td>' + json.list[i].last_success + '</td><td>' + json.list[i].last_fail + '</td><td>' + json.list[i].last_time + '</td><td><i class="mdui-icon material-icons">play</i></td></tr>');
                }
            }
        },
        dataType:"json",
    });
}

function getIcon(list) {
    if (list.is_dir) return '<i class="mdui-icon material-icons mdui-text-color-orange">folder</i>';
    if (list.status == 0) return '<i class="mdui-icon material-icons mdui-text-color-blue">do_not_disturb_on</i>';
    if (list.status == 1) return '<i class="mdui-icon material-icons mdui-text-color-green">check_circle</i>';
    if (list.status == 2) return '<i class="mdui-icon material-icons mdui-text-color-red">error</i>';
}

function back() {
    if (up_path == undefined || up_path == "/" || up_path == "") {
        goto('/');
    } else {
        goto('/job' + up_path);
    }
}