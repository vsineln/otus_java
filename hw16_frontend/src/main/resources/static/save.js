var stompClient = null;

function connect() {
    stompClient = Stomp.over(new SockJS('/ws'));
    stompClient.connect({}, function () {
        console.log('connected stompClient');
        stompClient.subscribe('/topic/response/user/save', function (output) {
            console.log('response from user/save');
            alert(output.body);
        });
    }, function (err) {
        alert('error' + err);
    });
}

function saveUser() {
    var name = $("#name").val();
    var login = $("#login").val();
    var password = $("#password").val();
    var role = $("#role").val();

    if(!login || 0 === login.length || !password || 0 === password.length){
        alert('Login and password can not be null');
    }else{
        stompClient.send("/app/user/save", {}, JSON.stringify({name, login, password, role}));
    }
};

$(() => {
    $("form").on('submit', event => event.preventDefault());
});
