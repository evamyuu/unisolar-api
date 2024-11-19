// Select the necessary HTML elements for the chat interface
const chat = document.querySelector('#chat'); // The container for the chat bubbles
const input = document.querySelector('#input'); // The input field where the user types messages
const sendButton = document.querySelector('#send-button'); // The button that sends the message

// Add event listener for the send button click to trigger message sending
sendButton.addEventListener('click', sendMessage);

// Add event listener for the 'Enter' key press to trigger message sending
input.addEventListener('keyup', function(event) {
    event.preventDefault(); // Prevent default behavior (form submission)
    if (event.keyCode === 13) { // Check if 'Enter' key (keyCode 13) was pressed
        sendButton.click(); // Trigger the send button click
    }
});

// When the DOM content is fully loaded, scroll to the bottom of the chat
document.addEventListener('DOMContentLoaded', scrollToBottom);

/**
 * Function to send a message when the user interacts with the chat
 */
async function sendMessage() {
    // Check if input is empty or null and return early if true
    if(input.value == '' || input.value == null) return;

    // Get the message typed by the user and clear the input field
    const message = input.value;
    input.value = '';

    // Create a new bubble for the user's message and add it to the chat
    const newBubble = createUserBubble();
    newBubble.innerHTML = message;
    chat.appendChild(newBubble);

    // Create an empty bot bubble to be filled with the bot's response
    let newBotBubble = createBotBubble();
    chat.appendChild(newBotBubble);

    // Scroll to the bottom of the chat after adding new messages
    scrollToBottom();

    // Send the user's message to the backend for processing
    fetch('http://localhost:8080/chat', {
        method: 'POST', // HTTP method to be used (POST)
        headers: {
            'Content-Type': 'application/json', // Set the content type as JSON
        },
        body: JSON.stringify({'question': message}), // Send the user's message as JSON in the body
    }).then(async response => {
        // Handle the response from the backend
        if (!response.ok) {
            throw new Error('An error occurred!'); // Throw an error if the response is not OK
        }

        const responseReader = response.body.getReader(); // Create a reader to read the response body
        let partialResponse = ''; // Initialize a variable to store the response content

        // Read the response stream in chunks and update the bot bubble with each chunk
        while (true) {
            const {
                done: finished,
                value: chunk
            } = await responseReader.read(); // Read the next chunk

            if (finished) break; // Exit the loop if the entire response is read

            partialResponse += new TextDecoder().decode(chunk); // Decode and append the chunk
            newBotBubble.innerHTML = marked.parse(partialResponse); // Update the bot bubble's content with the decoded response
            scrollToBottom(); // Scroll to the bottom after updating the bot's message
        }
    }).catch(error => {
        // Handle any errors that occur during the fetch process
        alert(error.message); // Display an alert with the error message
    });
}

/**
 * Function to create a new bubble for the user's message
 * @returns {HTMLElement} The user message bubble element
 */
function createUserBubble() {
    const bubble = document.createElement('p'); // Create a new paragraph element for the bubble
    bubble.classList = 'chat__bubble chat__bubble--user'; // Add classes for styling the user bubble
    return bubble; // Return the created bubble
}

/**
 * Function to create a new bubble for the bot's response
 * @returns {HTMLElement} The bot message bubble element
 */
function createBotBubble() {
    let bubble = document.createElement('p'); // Create a new paragraph element for the bubble
    bubble.classList = 'chat__bubble chat__bubble--bot'; // Add classes for styling the bot bubble
    return bubble; // Return the created bubble
}

/**
 * Function to scroll the chat container to the bottom, ensuring the latest messages are visible
 */
function scrollToBottom() {
    chat.scrollTop = chat.scrollHeight; // Set the scroll position to the bottom of the chat container
}
