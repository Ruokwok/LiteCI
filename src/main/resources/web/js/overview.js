localStorage.path = '/';
update();
function update() {
    $.ajax({
        type: 'POST',
        url: '/api/jobs',
        data: JSON.stringify(data),
        success: function (json) {
        closeLoading();

        },
        dataType:"json",
    });
}