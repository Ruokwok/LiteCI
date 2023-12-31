//import { FitAddon } from 'xterm-addon-fit';
var term = new Terminal({
    rendererType:"canvas",
	disableStdin:true,
	cursorBlink:false,
	columns: 160,
	fontSize: 15,
});
Terminal.applyAddon(fit);
term.open(document.getElementById('terminal'));
term.fit();

var _path = window.location.pathname.substring(6).replaceAll("%20", " ");
var p = _path.split("/");
var id = p[p.length - 1];
var path = _path.substring(0, _path.length - id.length - 1);
localStorage.path = path;

var sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay));
var loop = true;
const repeatedGreetings = async () => {
  while (loop) {
    update();
    await sleep(2000);
  }
}
repeatedGreetings()
function update() {
    post('/api/build/info', { params: { 'path': path, 'id': id}}, function (json) {
        console.log(json)
        $('#b-loading').hide();
        if (json.params.message == "success") {
            $("#name").text(json.name);
            $("#id").text(json.id);
            $("#trigger").text(getTrigger(json.trigger));
            if (json.status == 0) {
                $('#icon').html('<div class="mdui-list-item-icon mdui-spinner"></div>');
                mdui.mutation();
                $('#date').text('Building...')
                $('#time').text('Building...')
                $('#exit').text('Building...')
            } else if (json.status == 1) {
                $("#icon").html('<i class="mdui-icon material-icons mdui-text-color-green">check_circle</i>');
                loop = false;
                $('#date').html(toDate(json.date))
                $('#time').html(toTime(json.time))
                $('#exit').html(json.exit)
            } else if (json.status == 2) {
                $("#icon").html('<i class="mdui-icon material-icons mdui-text-color-red">error</i>');
                $('#date').html(toDate(json.date))
                $('#time').html(toTime(json.time))
                $('#exit').html(json.exit)
                loop = false;
            }
            term.clear();
            for (var i in json.output) {
                term.writeln(json.output[i]);
            }
            if (json.artifacts != undefined && json.artifacts.length > 0) {
                $("#artifact").text('');
                for (var i in json.artifacts) {
                    $("#artifact").append('<li><a href="/download' + localStorage.path  + '/latest/' + json.artifacts[i].name + '">' + json.artifacts[i].name + '</a><small class="mdui-m-l-2">' + formatSize(json.artifacts[i].size) + '</small></li>');
                }
            }
        } else {
            $("#warning").html(json.params.message);
            $("#card").hide();
            $("#error").show();
        }
        if (json.commits != undefined) {
            $('#change-card').show();
            for (var i in json.commits) {
                $('#change-list').append('<li>' + json.commits[i].change + ' (<a href="' + json.commits[i].url + '">' + json.commits[i].id + '</a>@' + json.commits[i].user + ')</li>');
            }
        }
    });
}

function getTrigger(t) {
    if (t == undefined) return "Unknown";
    if (t.type == 0) {
        return "{web.trigger.user}(" + t.content + ")";
    } else if (t.type == 1) {
        return "Webhook(" + t.content + ")";
    } else if (t.type == 2) {
        return "CRON";
    } else {
        return "UnKnown";
    }
}