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
    });
}