update();
function update() {
//    showLoading();
    $.ajax({
        type: 'POST',
        url: '/api1/setting/theme/get',
        data: '',
        success: function (json) {
//            closeLoading();
            console.log(json);
            $('#title').val(json.params.title);
            $('#t-' + json.params.theme).prop('checked', true);
            $('#a-' + json.params.accent).prop('checked', true);
        },
        dataType:"json",
    });
}

function save() {
    var data = {};
    data.params = {};
    data.params.title = $("#title").val();
    data.params.theme = $('input[name="theme"]:radio').filter(":checked").attr("id").slice(2);
    data.params.accent = $('input[name="accent"]:radio').filter(":checked").attr("id").slice(2);
    console.log(data)
        showLoading();
        $.ajax({
            type: 'POST',
            url: '/api1/setting/theme/set',
            data: JSON.stringify(data),
            success: function (json) {
                closeLoading();
                console.log(json);
            },
            dataType:"json",
        });
}