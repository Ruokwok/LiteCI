localStorage.path = window.location.pathname.substring(4).replace("%20", " ");
function edit() {
    goto('/edit' + localStorage.path);
}

function build() {
    post('/api1/build', { params: { path: localStorage.path}});
}

update();
function update() {
    post('/api/info/job', { params: { path: localStorage.path}}, function (json) {
        console.log(json)
        $('#name').text(json.name);
        $('#date').text(toDate(json.date));
        $('#time').html(toTime(json.time));
        if (json.description != "") {
            $('#description').html(json.description);
            $('#description').show();
        }
        if (json.artifact == undefined) {
            $('#artifact').append('<li>{web.none}</li>');
        } else {
            for (var i in json.artifact) {
                $('#artifact').append('<li><a href="#">' + json.artifact[i].name + '</a><small class="mdui-m-l-2">' + json.artifact[i].size + '</small></li>')
            }
        }
        $('#builds').text('');
        for (var i in json.list) {
            $('#builds').append('<li class="mdui-list-item mdui-ripple mdui-m-a-0"><i class="mdui-list-item-icon mdui-icon material-icons mdui-text-color-' + (json.list[i].status ? 'green':'red') + '">' + (json.list[i].status ? 'check_circle' : 'error') + '</i><div class="mdui-list-item-content"><text>#' + json.list[i].id + '</text><code class="mdui-m-l-3">' + toDate(json.list[i].date) + '</code></div></li>');
        }
    });
}