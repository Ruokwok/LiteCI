$('#path').text(localStorage.path);
function back() {
    goto('/job' + localStorage.path);
}

function save() {
    var data = {};
    data.path = localStorage.path;
    data.description = $('#description').val();

    post('/api2/edit/job', data, function(json) {
        console.log(json);
    });
}