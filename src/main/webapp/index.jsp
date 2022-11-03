<%--
  Created by IntelliJ IDEA.
  User: yorkwu
  Date: 2022/10/28
  Time: 23:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="http://libs.baidu.com/jquery/2.0.0/jquery.js"></script>
</head>
<body>
<script type="text/javascript">
    $(function(){
        var host= window.location.host;
        var websocket;
        if ('WebSocket' in window) {
            websocket = new WebSocket("ws://"+host+"/im?userId=9528");
        } else if ('MozWebSocket' in window) {
            websocket = new MozWebSocket("ws://"+host+"/im?userId=9528");
        } else {
            websocket = new SockJS("http://"+host+"/socketJs/im?userId=9528");
        }
        websocket.onopen = function (evnt) {
            console.log("链接服务器成功!")
        };
        websocket.onmessage = function (evnt) {
            console.log(evnt.data);
            $("#message").html(evnt.data);
        };
        websocket.onerror = function (evnt) {
            console.log("websocket错误");
        };
        websocket.onclose = function (evnt) {
            console.log("与服务器断开了链接!")
        }
        $('#send').bind('click', function() {
            send();
        });
        function send(){
            if (websocket != null) {
                var message = document.getElementById('message').value;
                websocket.send(message);
            } else {
                alert('未与服务器链接.');
            }
        }
    });
</script>

</body>
</html>
