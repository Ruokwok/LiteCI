update();
function update() {
//    showLoading();
    $.ajax({
        type: 'POST',
        url: '/api4/setting/theme/get',
        data: '',
        success: function (json) {
//            closeLoading();
            console.log(json);
            $('#title').val(json.params.title);
            $('#docs').val(json.params.description);
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
    data.params.description = $("#docs").val();
    data.params.theme = $('input[name="theme"]:radio').filter(":checked").attr("id").slice(2);
    data.params.accent = $('input[name="accent"]:radio').filter(":checked").attr("id").slice(2);
    console.log(data)
    showLoading();
    post('/api4/setting/theme/set', data, function (json) {
      closeLoading();console.log(json)
      if (json.type == "dialog") {
          mdui.dialog({
            content: json.content,
            buttons: [
              {
                text: 'OK',
              }
            ]
          });
      }
  });
}