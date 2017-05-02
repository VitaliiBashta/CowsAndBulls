var timerId;
var timerId1;

function registerPlayer(){
    sendPost("register","containerTable");
    clearInterval(timerId1);
//    timerId= setTimeout(refresh, 2500);
}

function refresh(){
    sendPost("refresh","containerTable");
    timerId= setTimeout(refresh, 2500);
}

function checkEnemy(){
    timerId1= setTimeout(checkEnemy, 5000);
    sendPost("checkEnemy","enemyTable");
}

function chooseEnemy(player){
    clearInterval(timerId);
    sendPost("chooseEnemy," + player.id,"containerTable");
}

function play(){
    clearInterval(timerId);
    timerId1= setTimeout(checkEnemy, 5000);
    sendPost("play"+getNumber(),"containerTable");
}

function shot(){
    sendPost("shot"+getNumber(), "containerTable");
}

function getNumber(){
    var result ="";
    for (var i= 0; i<4;i++){
        var radios = document.getElementsByName("bullDigit"+i);
        for (var j = 0; j < 10; j++)
            if (radios[j].checked) result+=j;
    }
    return result;
}

function sendPost(request, container){
    var XmlHTTP;
    if (window.XMLHttpRequest) XmlHTTP=new XMLHttpRequest();
    XmlHTTP.onreadystatechange = function() {
        if (XmlHTTP.readyState==4 && XmlHTTP.status==200)
            document.getElementById(container).innerHTML=XmlHTTP.responseText;
    }
    var params = document.getElementById("login").value + ',' + request;
    XmlHTTP.open("POST","/",true);
    XmlHTTP.send(params);
}

function sendGet(request, container){
    var XmlHTTP;
    if (window.XMLHttpRequest) XmlHTTP=new XMLHttpRequest();
    XmlHTTP.onreadystatechange = function() {
        if (XmlHTTP.readyState==4 && XmlHTTP.status==200)
            document.getElementById(container).innerHTML=XmlHTTP.responseText;
    }
    var params = document.getElementById("login").value + ',' + request;
    XmlHTTP.open("GET",params,true);
    XmlHTTP.send();
}