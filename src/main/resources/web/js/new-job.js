if (localStorage.path == undefined) {
    goto('/');
}
$('#path').text(localStorage.path);

function create() {
    showLoading();
    var t = $('input[name="type"]:radio').filter(":checked").attr("id");
    var data = {};
    data.params = {};
    data.params.name = $('#name').val();
    data.params.path = localStorage.path;
    post('/api3/create/' + t, data, function (json) {
        closeLoading();
        if (json.params.status == 'success') {
            if (t == "dir") {
                if (localStorage.path == "/") {
                    asyncGoto('/');
                } else {
                    asyncGoto('/job' + localStorage.path);
                }
            } else {
                asyncGoto('/edit' + localStorage.path + (localStorage.path == '/' ? '' : '/') + data.params.name);
            }
        } else {
            dialog(json.content);
        }
    });
}