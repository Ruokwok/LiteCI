localStorage.path = window.location.pathname.substring(4).replace("%20", " ");
function edit() {
    goto('/edit' + localStorage.path);
}

function build() {
    post('/api1/build', { params: { path: localStorage.path}});
}