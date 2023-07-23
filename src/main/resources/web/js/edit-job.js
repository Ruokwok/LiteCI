localStorage.path = window.location.pathname.substring(5).replace("%20", " ");
$('#path').text(localStorage.path);
$('#webhook-url').text(window.location.protocol + "//" + document.domain + "/webhook" + localStorage.path + "?token={TOKEN}");
$(document).on('change', 'input[type="checkbox"]', function(){
    var isChecked = $(this).prop('checked');
    var id = $(this).attr('id');
    if (id == 'webhook') {
        if (isChecked) {
            $('#webhook-card').show();
        } else {
            $('#webhook-card').hide();
        }
    } else if (id == 'cron') {
        if (isChecked) {
            $('#cron-card').show();
        } else {
            $('#cron-card').hide();
        }
    } else if (id == 'check') {
        if (isChecked) {
            $('#check-card').show();
        } else {
            $('#check-card').hide();
        }
    } else if (id == 'artifact') {
        if (isChecked) {
            $('#artifact-card').show();
        } else {
            $('#artifact-card').hide();
        }
    }

});

function back() {
    goto('/job' + localStorage.path);
}

function save() {
    var data = {};
    data.path = localStorage.path;
    data.description = $('#description').val();
    data.webhook = {};
    data.webhook.enable = $('#webhook').is(':checked');
    data.webhook.token = $('#token').val();
    data.cron = {};
    data.cron.enable = $('#cron').is(':checked');
    data.cron.expression = $('#cron-expression').val();
    data.check = {};
    data.check.enable = $('#check').is(':checked');
    data.check.shell = $('#check-shell').val().trim().split('\n');
    data.check.only_cron = $('#only-cron').is(':checked');
    data.shell = $('#build-shell').val().trim().split('\n');
    data.artifact = {};
    data.artifact.enable = $('#artifact').is(':checked');
    data.artifact.files = $('#artifact-file').val().trim().split('\n');
    post('/api3/edit/job', data, function(json) {
        console.log(json);
    });
}

update();
function update() {
//    showLoading();
    post('/api3/get/job', { params: { path: localStorage.path}}, function(json) {
        console.log(json);
        $('#description').val(json.description);
        $('#webhook').prop('checked', json.webhook.enable);
        if (json.webhook.enable) $('#webhook-card').show();
        $('#token').val(json.webhook.token);
        $('#cron').prop('checked', json.cron.enable);
        if (json.cron.enable) $('#cron-card').show();
        $('#cron-expression').val(json.cron.expression);
        $('#check').prop('checked', json.check.enable);
        if (json.check.enable) $('#check-card').show();
        var checkShell = "";
        for (var i in json.check.shell) {
            checkShell += "\n" + json.check.shell[i];
        }
        $('#check-shell').val(checkShell.trim());
        $('#only-cron').prop('checked', json.check.only_cron);
        var shell = "";
        for (var i in json.shell) {
            shell += "\n" + json.shell[i];
        }
        $('#build-shell').val(shell.trim());
        $('#artifact').prop('checked', json.artifact.enable);
        if (json.artifact.enable) $('#artifact-card').show();
        var files = "";
        for (var i in json.artifact.files) {
            files += "\n" + json.artifact.files[i];
        }
        $('#artifact-file').val(files.trim());
//        closeLoading();
    });
}

function remove() {
    var job = localStorage.path;
    mdui.dialog({
        title: '{web.remove.job.ask}',
        content: job,
        buttons: [
            {
                text: '{web.no}'
            },
            {
                text: '{web.yes}',
                onClick: function (inst) {
                    post('/api3/remove/job', { params: { path: job}}, function(json) {
                        console.log(json);
                        if (json.params.status == "success") {
                            asyncGoto('/');
                        }
                    });
                }
            }
        ]
    });
}