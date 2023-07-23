localStorage.path = window.location.pathname.substring(4).replaceAll("%20", " ");
update()
var up_path;
function update() {
    $("#job-loading").show();
    $("#job-list").text('');
    var data = {};
    data.params = {};
    data.params.path = window.location.pathname.substring(5);
    $('#job-list').append('<tr onclick="back()"><td><i class="mdui-icon material-icons mdui-text-color-orange">folder</i></td><td>{web.path.back}</td><td>{web.none}</td><td>{web.none}</td><td>{web.none}</td><td></td></tr>');
    $.ajax({
        type: 'POST',
        url: '/api/jobs',
        data: JSON.stringify(data),
        success: function (json) {
            up_path = json.father;
            $('#name').text(json.name);
            $('title').text('{config.title} | ' + json.name);
            if (json.description != '' && json.description != undefined) {
                $("#description").html(json.description);
                $("#docs").val(json.description);
                $("#description").show();
            }
            $("#job-loading").hide();
            console.log(json);
            for (i in json.list) {
                if (json.list[i].is_dir) {
                    $('#job-list').append('<tr onclick="goto(\'' + window.location.pathname + '/' + json.list[i].name + '\')")><td>' + getIcon(json.list[i]) + '</td><td>' + json.list[i].name.replace(' ', '&nbsp;') + '</td><td>{web.none}</td><td>{web.none}</td><td>{web.none}</td><td><i class="mdui-icon material-icons">play</i></td></tr>');
                } else {
                    $('#job-list').append('<tr onclick="goto(\'' + window.location.pathname + '/' + json.list[i].name + '\')")><td>' + getIcon(json.list[i]) + '</td><td>' + json.list[i].name.replace(' ', '&nbsp;') + '</td><td>' + json.list[i].last_success + '</td><td>' + json.list[i].last_fail + '</td><td>' + json.list[i].last_time + '</td><td><i class="mdui-icon material-icons">play</i></td></tr>');
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

var e = true;
function edit() {
    if (e) {
        e = false;
        $('#edit').show();
        $('#description').hide();
        $('#btn').text('{web.retract}');
    } else {
        e = true;
        $('#edit').hide();
        $('#description').show();
        $('#btn').text('{web.edit.description}');
    }
}

function save() {
    var data = {};
    data.params = {};
    data.params.path = localStorage.path;
    data.params.description = $('#docs').val();
    showLoading();
    $.ajax({
        type: 'POST',
        url: '/api3/edit/dir',
        data: JSON.stringify(data),
        success: function (json) {
            closeLoading();
            $('#description').html(json.params.description);
            $('#docs').text(json.params.description);
            e = true;
            $('#edit').hide();
            $('#description').show();
            $('#btn').text('{web.edit.description}');
        },
        dataType:"json",
    });
}

function remove() {
    mdui.dialog({
        title: '{web.remove.dir.ask}',
        content: '{web.remove.dir.ask.c}',
        buttons: [
            {
                text: '{web.no}',
            },
            {
                text: '{web.yes}',
                onClick: function (inst) {
                    var data = {};
                    data.params = {};
                    data.params.path = window.location.pathname.substring(4).replace("%20", " ");
                    post('/api3/remove/dir', data, function (json) {
                        if (json.params.status == 'success') {
                            asyncGoto('/');
                        }
                    });
                }
            }
        ]
    });
}