const config = document.getElementsByClassName('snipe-config')[0];

const snipe_list = document.getElementById('snipe-list');

const prefix = document.getElementById('prefix');

const snipedeletedmessages = document.getElementById('snipedeletedmessages');
const snipedeletedfiles = document.getElementById('snipedeletedfiles');
const snipeeditedmessages = document.getElementById('snipeeditedmessages');
const snipeeditedfiles = document.getElementById('snipeeditedfiles');
const sendsnipenotifs = document.getElementById('sendsnipenotifs');
const snipenonhumans = document.getElementById('snipenonhumans');
const snipemessagemanagers = document.getElementById('snipemessagemanagers');
const enablesnipecommand = document.getElementById('enablesnipecommand');

const maxmessagecache = document.getElementById('maxmessagecache');
const maxsnipecache = document.getElementById('maxsnipecache');

const snipedeletedlogsid = document.getElementById('snipedeletedlogsid');
const snipeeditedlogsid = document.getElementById('snipeeditedlogsid');

prefix.addEventListener('input', alertConfig);

snipedeletedmessages.addEventListener('change', alertConfig);
snipedeletedfiles.addEventListener('change', alertConfig);
snipeeditedmessages.addEventListener('change', alertConfig);
snipeeditedfiles.addEventListener('change', alertConfig);
sendsnipenotifs.addEventListener('change', alertConfig);
snipenonhumans.addEventListener('change', alertConfig);
snipemessagemanagers.addEventListener('change', alertConfig);
enablesnipecommand.addEventListener('change', alertConfig);

maxmessagecache.addEventListener('input', alertConfig);
maxsnipecache.addEventListener('input', alertConfig);

snipedeletedlogsid.addEventListener('input', alertConfig);
snipeeditedlogsid.addEventListener('input', alertConfig);

function alertConfig() {
    overlay.style.position = "sticky";
    overlay.classList.add('slide-in');
}

function deleteSnipe(msgid) {
    httpPostAsync(`/api/v1/delete`, `{"msgid":"${msgid}"}`, (res) => {
        window.location.reload();
    });
}

function getConfig() {
    httpGetAsync("/api/v1/getconfig", null, (res) => {
        console.log(res);

        const data = JSON.parse(res);

        prefix.value = data.prefix;
        snipedeletedmessages.value = data.snipedeletedmessages;
        snipedeletedfiles.value = data.snipedeletedfiles;
        snipeeditedmessages.value = data.snipeeditedmessages;
        snipeeditedfiles.value = data.snipeeditedfiles;
        sendsnipenotifs.value = data.sendsnipenotifs;
        snipenonhumans.value = data.snipenonhumans;
        snipemessagemanagers.value = data.snipemessagemanagers;
        enablesnipecommand.value = data.enablesnipecommand;

        maxmessagecache.value = data.maxmessagecache;
        maxsnipecache.value = data.maxsnipecache;

        snipedeletedlogsid.value = data.snipedeletedlogsid;
        snipeeditedlogsid.value = data.snipeeditedlogsid;

        overlay.classList.add('slide-out');
        setTimeout(function () {
            overlay.classList.remove('slide-in');
            overlay.classList.remove('slide-out');
            overlay.style.position = "";
        }, 100);
    });
}


function setConfig() {
    const body = `
        {
            "prefix": "${prefix.value}",
            "snipedeletedmessages": "${snipedeletedmessages.value}",
            "snipedeletedfiles": "${snipedeletedfiles.value}",
            "snipeeditedmessages": "${snipeeditedmessages.value}",
            "snipeeditedfiles": "${snipeeditedfiles.value}",
            "sendsnipenotifs": "${sendsnipenotifs.value}",
            "snipenonhumans": "${snipenonhumans.value}",
            "snipemessagemanagers": "${snipemessagemanagers.value}",
            "enablesnipecommand": "${enablesnipecommand.value}",

            "maxmessagecache": "${maxmessagecache.value}",
            "maxsnipecache": "${maxsnipecache.value}",

            "snipedeletedlogsid": "${snipedeletedlogsid.value}",
            "snipeeditedlogsid": "${snipeeditedlogsid.value}"

        }
        `;

    console.log(body);
    httpPostAsync(`/api/v1/setconfig`, body, (res) => {
        window.location.reload();
    });
}

function getSnipeList() {
    httpGetAsync(`/api/v1/getsnipelist`, null, (res) => {
        console.log(res);

        const data = JSON.parse(res);

        snipe_list.innerHTML = "";

        if (data.cache.length == 0) {
            snipe_list.innerHTML = "Nothing to see here.";
        }

        for (let i in data.cache) {

            const c = data.cache[i];

            var card = `

<div class="snipe">
    <div class="main">
        <div class="author">
            <img
                src="${c.avatarurl}">
            <h4>${c.user}</h4>
        </div>

        <hr>
        <hr style="background-color: transparent">

        <div class="contents">
            <div class="from">
                <b>Message Edited:</b><br>
                ${c.from}
            </div>

            <div class="to">
                <b>Message Edited:</b><br>
                ${c.to}
            </div>

            <div class="other">
                <b>Message Other:</b><br>
                ${c.other}
            </div>
        </div>

        <hr>
        <hr style="background-color: transparent">

        <div class="time">
            <h4>${c.time}</h4>
        </div>
    </div>

    <div class="aside">
        <button class="delete" title="Remove" onclick="deleteSnipe('${c.msgid}')">&#128465;&#65039;
        </button>
    </div>
</div>

            `;

            snipe_list.innerHTML += card;
        }
    });
}

function httpGetAsync(url, body, callback) {
    console.log(url);

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
            callback(xmlHttp.responseText);
        }
    }

    xmlHttp.open("GET", url, true);
    xmlHttp.setRequestHeader('Content-Type', 'application/json');
    xmlHttp.send(body);
}


function httpPostAsync(url, body, callback) {
    console.log(url);

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
            callback(xmlHttp.responseText);
        }
    }

    xmlHttp.open("POST", url, true);
    xmlHttp.setRequestHeader('Content-Type', 'application/json');
    xmlHttp.send(body);
}

function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year} /${month}/${day} ${hours}:${minutes}:${seconds} `;
}

getConfig();
getSnipeList();