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
    $.ajax({
        type: 'POST',
        url: '/api1/create/' + t,
        data: JSON.stringify(data),
        success: function (json) {
        closeLoading();
            if (json.params.status == 'success') {
                asyncGoto('/');
            } else {
                dialog(json.content);
            }
        },
        dataType:"json",
    });
}