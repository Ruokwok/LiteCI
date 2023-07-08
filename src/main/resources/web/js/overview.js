localStorage.path = '/';
update();
function update() {
    $("#job-list").text('');
    $("#job-loading").show();
    var data = {};
    data.params = {};
    data.params.path = "/";
    $.ajax({
        type: 'POST',
        url: '/api/jobs',
        data: JSON.stringify(data),
        success: function (json) {
            if (json.description != '' && json.description != undefined) {
                $("#description").text(json.description);
                $("#description").show();
            }
            $("#job-loading").hide();
            console.log(json);
            for (i in json.list) {
                if (json.list[i].is_dir) {
                    $('#job-list').append('<tr onclick="goto(\'/job/' + json.list[i].name + '\')")><td>' + getIcon(json.list[i]) + '</td><td>' + json.list[i].name + '</td><td>{web.none}</td><td>{web.none}</td><td>{web.none}</td><td><i class="mdui-icon material-icons">play</i></td></tr>');
                } else {
                    $('#job-list').append('<tr onclick="goto(\'/job/' + json.list[i].name + '\')")><td>' + getIcon(json.list[i]) + '</td><td>' + json.list[i].name + '</td><td>' + json.list[i].last_success + '</td><td>' + json.list[i].last_fail + '</td><td>' + json.list[i].last_time + '</td><td><i class="mdui-icon material-icons">play</i></td></tr>');
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