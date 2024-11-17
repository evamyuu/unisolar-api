const chat = document.querySelector('#chat');
const input = document.querySelector('#input');
const sendButton = document.querySelector('#send-button');

sendButton.addEventListener('click', sendMessage);

input.addEventListener('keyup', function(event) {
    event.preventDefault();
    if (event.keyCode === 13) {
        sendButton.click();
    }
});

document.addEventListener('DOMContentLoaded', scrollToBottom);

async function sendMessage() {
    if(input.value == '' || input.value == null) return;

    const message = input.value;
    input.value = '';

    const newBubble = createUserBubble();
    newBubble.innerHTML = message;
    chat.appendChild(newBubble);

    let newBotBubble = createBotBubble();
    chat.appendChild(newBotBubble);
    scrollToBottom();

    fetch('http://localhost:8080/chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({'question': message}),
    }).then(async response => {
        if (!response.ok) {
            throw new Error('An error occurred!');
        }

        const responseReader = response.body.getReader();
        let partialResponse = '';

        while (true) {
            const {
                done: finished,
                value: chunk
            } = await responseReader.read();

            if (finished) break;

            partialResponse += new TextDecoder().decode(chunk);
            newBotBubble.innerHTML = marked.parse(partialResponse);
            scrollToBottom();
        }
    }).catch(error => {
        alert(error.message);
    });
}

function createUserBubble() {
    const bubble = document.createElement('p');
    bubble.classList = 'chat__bubble chat__bubble--user';
    return bubble;
}

function createBotBubble() {
    let bubble = document.createElement('p');
    bubble.classList = 'chat__bubble chat__bubble--bot';
    return bubble;
}

function scrollToBottom() {
    chat.scrollTop = chat.scrollHeight;
}
