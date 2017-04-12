var timerId;
var timerId1;

function registerPlayer(){
    sendRequest("register","containerTable");
    clearInterval(timerId1);
    timerId=setTimeout(refresh, 2500);
}

function refresh(){
    sendRequest("refresh","containerTable");
    timerId= setTimeout(refresh, 2500);
}

function checkEnemy(){
    timerId1= setTimeout(checkEnemy, 5000);
    sendRequest("checkEnemy","enemyTable");
}

function chooseEnemy(player){
    clearInterval(timerId);
    sendRequest("chooseEnemy," + player.id,"containerTable");
}

function play(){
    clearInterval(timerId);
    timerId1= setTimeout(checkEnemy, 5000);
    sendRequest("play"+getNumber(),"containerTable");
}

function shot(){
    sendRequest("shot"+getNumber(), "containerTable");
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

function sendRequest(request, container){
    var XmlHTTP;
    if (window.XMLHttpRequest) XmlHTTP=new XMLHttpRequest();
    XmlHTTP.onreadystatechange=function() {
        if (XmlHTTP.readyState==4 && XmlHTTP.status==200)
            document.getElementById(container).innerHTML=XmlHTTP.responseText;
    }
    XmlHTTP.open("POST",document.getElementById("login").value+","+ request,true);
    XmlHTTP.send();
}