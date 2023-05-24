const config = document.getElementsByClassName('snipe-config')[0];

const question_queue = document.getElementById('snipe-list');

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

function getQueue() {
    httpGetAsync(`/api/v1/getqueue`, null, (res) => {
        console.log(res);

        const data = JSON.parse(res);

        question_queue.innerHTML = "";

        if (data.queue.length == 0) {
            question_queue.innerHTML = "Nothing to see here.";
        }

        for (let i in data.queue) {

            const q = data.queue[i];

            const date = new Date(q.time);

            const ques = (q.poll ? "Poll: " : "Question: ") + q.question;

            var card = `

<div class="question" style = "border-left: 5px solid ${qotdColor}">
    <div class="main">
        <div class="header">
            <h3><b>Added by: ${q.user}</b></h3>
        </div>
        <div class="title">
            <h2><b>${ques}</b></h2>
        </div>
        <div class="description">
            <h4>Footer: <i>${q.footer}</i></h4>
        </div>
        <div class="footer">
            <h4>Added on: ${formatDate(date)}</h4>
        </div>
    </div>

    <div class="aside">
        <button class="delete" title="Remove" onclick="deleteQOTD('queue','${q.uuid}')">&#128465;&#65039;</button>
    </div>
</div >

            `;

            question_queue.innerHTML += card;
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