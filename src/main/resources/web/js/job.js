localStorage.path = window.location.pathname.substring(4).replace("%20", " ");
function edit() {
    goto('/edit' + localStorage.path);
}

function build() {
    post('/api2/build', { params: { path: localStorage.path}}, function (json) {
        console.log(json)
        console.log(123)
        asyncGoto(window.location.pathname);
    });
}

update();
function update() {
    post('/api/info/job', { params: { path: localStorage.path}}, function (json) {
        console.log(json)
        $("#b-loading").hide();
        $('#name').text(json.name);
        $('#date').html(toDate(json.date));
        $('#time').html(toTime(json.time));
        if (json.description != "") {
            $('#description').html(json.description);
            $('#description').show();
        }
        if (json.artifact == undefined) {
            $('#artifact').append('<li>{web.none}</li>');
        } else {
            for (var i in json.artifact) {
                $('#artifact').append('<li><a href="/download' + localStorage.path  + '/latest/' + json.artifact[i].name + '"/>' + json.artifact[i].name + '</a><small class="mdui-m-l-2">' + formatSize(json.artifact[i].size) + '</small></li>');
            }
        }
        $('#builds').text('');
        if (json.building > 0) {
            $('#builds').append('<li class="mdui-list-item mdui-ripple mdui-m-a-0" onclick="goto(\'/build' + localStorage.path + '/' + json.building + '\')"><div class="mdui-list-item-icon mdui-spinner"></div><div class="mdui-list-item-content"><text>#' + json.building + '</text></div></li>');
            mdui.mutation();
        }
        for (var i in json.list) {
            $('#builds').append('<li class="mdui-list-item mdui-ripple mdui-m-a-0" onclick="goto(\'/build' + localStorage.path + '/' + json.list[i].id + '\')"><i class="mdui-list-item-icon mdui-icon material-icons mdui-text-color-' + (json.list[i].status ? 'green':'red') + '">' + (json.list[i].status ? 'check_circle' : 'error') + '</i><div class="mdui-list-item-content"><text>#' + json.list[i].id + '</text><code class="mdui-m-l-3">' + toDate(json.list[i].date) + '</code></div></li>');
        }
        if (json.commits != undefined) {
            $('#change-card').show();
            for (var i in json.commits) {
                $('#change-list').append('<li>' + json.commits[i].change + ' (<a href="' + json.commits[i].url + '">' + json.commits[i].id + '</a>@' + json.commits[i].user + ')</li>');
            }
        }
    });
}