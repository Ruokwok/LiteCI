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
    });
}