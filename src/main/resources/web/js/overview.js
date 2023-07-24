localStorage.path = '/';
update();
var sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay))
const repeatedGreetings = async () => {
  while (true) {
    queue();
    await sleep(2000);
  }
}
repeatedGreetings()

function update() {
    $("#job-list").text('');
    $("#job-loading").show();
    var data = {};
    data.params = {};
    data.params.path = "/";
    post('/api/jobs', data, function (json) {
        if (json.description != '' && json.description != undefined) {
            $("#description").html(json.description);
            $("#description").show();
        }
        $("#job-loading").hide();
        console.log(json);
        for (i in json.list) {
            if (json.list[i].is_dir) {
                $('#job-list').append('<tr onclick="goto(\'/job/' + json.list[i].name + '\')")><td>' + getIcon(json.list[i]) + '</td><td>' + json.list[i].name + '</td><td>{web.none}</td><td>{web.none}</td><td>{web.none}</td><td><i class="mdui-icon material-icons">play</i></td></tr>');
            } else {
                $('#job-list').append('<tr onclick="goto(\'/job/' + json.list[i].name + '\')")><td>' + getIcon(json.list[i]) + '</td><td nowrap>' + json.list[i].name + '</td><td nowrap>' + toDate(json.list[i].last_success) + '</td><td nowrap>' + toDate(json.list[i].last_fail) + '</td><td nowrap>' + toTime(json.list[i].last_time) + '</td><td><i class="mdui-icon material-icons">play</i></td></tr>');
            }
        }
    });
}

function getIcon(list) {
    if (list.is_dir) return '<i class="mdui-icon material-icons mdui-text-color-orange">folder</i>';
    if (list.status == 0) return '<i class="mdui-icon material-icons mdui-text-color-blue">do_not_disturb_on</i>';
    if (list.status == 1) return '<i class="mdui-icon material-icons mdui-text-color-green">check_circle</i>';
    if (list.status == 2) return '<i class="mdui-icon material-icons mdui-text-color-red">error</i>';
}

var taskList;
function queue() {
    post('api/queue', undefined, function (json) {
        console.log(json)
        if (JSON.stringify(json) != taskList) {
            taskList = JSON.stringify(json);
            $('#task').text('');
            for (var i in json.task) {
                if (json.task[i].name == undefined) {
                    $('#task').append('<li class="mdui-list-item mdui-ripple"><div class="mdui-list-item-content mdui-m-l-4"><div class="mdui-typo-body-1-opacity mdui-m-l-4"><em>' + i + '.{web.build.idle}</em></div></div></li>')
                } else {
                    $('#task').append('<li class="mdui-list-item mdui-ripple"><div class="mdui-list-item-icon mdui-spinner"></div><div class="mdui-list-item-content"><div><text class="mdui-m-r-4">'+ json.task[i].name + '#' + json.task[i].id + '</text><code>'+ json.task[i].thread +'</code></div></div></li>');
                }
            }
            mdui.mutation();
            $('#queue').text('');
            for (var i in json.queue) {
                $('#queue').append('<li class="mdui-list-item mdui-ripple"><i class="mdui-icon material-icons mdui-text-color-blue">do_not_disturb_on</i><div class="mdui-list-item-content mdui-m-l-2">' + json.queue[i] + '</div></li>')
            }
        }
    });
}