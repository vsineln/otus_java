var stompClient = null;

function getUserList() {
    stompClient = Stomp.over(new SockJS('/ws'));

    stompClient.connect({}, function () {
        console.log('connected stompClient');
        stompClient.send("/app/user/list", {}, '');
        stompClient.subscribe('/topic/response/user/list', function (output) {
            console.log('response from /user/list');
            var usersResp = JSON.parse(output.body);

            var tableData = '<tbody>'
            for (i in usersResp) {
                tableData+= '<tr>';
                tableData+= '<td>' + usersResp[i].name + '</td>';
                tableData+= '<td>' + usersResp[i].login + '</td>';
                tableData+= '<td>' + usersResp[i].role + '</td>';
                tableData+= '</tr>';
            }
            tableData+='</tbody>';
            document.getElementById('tableData').innerHTML = tableData;
        });
    }, function (err) {
        alert('error' + err);
    });
}
