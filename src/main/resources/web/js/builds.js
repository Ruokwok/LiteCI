update();
function update() {
    post('api/builds', undefined, function (json) {
        console.log(json);
        $('#list-loading').hide();
        for (var i in json) {
            $('#list').append('<tr><td><i class="mdui-icon material-icons mdui-text-color-' +
            (json[i].status?'green':'red') + '">'+
            (json[i].status?'check_circle':'error')+'</i></td><td>#'+
            json[i].id+'</td><td nowrap><strong>'+json[i].name+'</strong></td><td nowrap>'+toDate(json[i].date)+'</td><td>'+
            getTrigger(json[i].trigger)+'</td></tr>');
        }
    });
}

function getTrigger(i) {
    switch (i) {
        case 0: return 'USER';
        case 1: return 'WEBHOOK';
        case 2: return 'CRON';
    }
    return 'UNKNOWN';
}