var term = new Terminal({
    rendererType:"canvas",
	disableStdin:true,
	cursorBlink:false,
	columns: 160,
	fontSize: 15,
});
term.open(document.getElementById('terminal'));

var _path = window.location.pathname.substring(6).replaceAll("%20", " ");
var p = _path.split("/");
var id = p[p.length - 1];
var path = _path.substring(0, _path.length - id.length - 1);
localStorage.path = path;


function update() {
    post('/api/build/info', { params: { 'path': path, 'id': id}}, function (json) {
        console.log(json)
        if (json.params.message == "success") {
            $("#name").text(json.name);
            $("#id").text(json.id);
            if (json.status == 0) {
            } else if (json.status == 1) {
                $("#icon").html('<i class="mdui-icon material-icons mdui-text-color-green">check_circle</i>');
            } else if (json.status == 2) {
                $("#icon").html('<i class="mdui-icon material-icons mdui-text-color-red">error</i>');
            }
            for (var i in json.output) {
                term.writeln(json.output[i]);
            }
        } else {
            $("#warning").html(json.params.message);
            $("#card").hide();
            $("#error").show();
        }
    });
}