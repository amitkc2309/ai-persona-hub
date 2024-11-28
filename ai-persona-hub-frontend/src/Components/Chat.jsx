import React, { useState, useEffect, useRef } from 'react';
import { useLocation } from "react-router-dom";
import { AppBar, Toolbar, Typography, IconButton, Paper, List, ListItem, ListItemAvatar, Avatar, ListItemText, TextField, Button, Divider, Card, CardHeader, CircularProgress } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import ChatIcon from '@mui/icons-material/Chat';
import AppBarTop from "./AppBarTop";
import { Box } from "@mui/material";
import axios from 'axios';
import Cookies from 'js-cookie';

export default function Chat() {
    const location = useLocation();
    const selectedprofile = location.state?.selectedprofile;
    const messagesEndRef = useRef(null);
    const [messages, setMessages] = useState('');
    const [conversation, setConversation] = useState('');
    const [sentText, setSentText] = useState('');
    const [error, setError] = useState('');
    const [typing, setTyping] = useState('');

    const handleSend = async () => {
        let nextId = 0;
        if (messages && messages.length > 0) {
            const lastMessage = messages[messages.length - 1];
            nextId = Number(lastMessage.id) + 1;
        } else {
            nextId = 1;
        }

        const sentMessage = {
            id: nextId,
            messageText: sentText,
        };

        try {
            const params = new URLSearchParams({ profile: selectedprofile.username });

            // Add the user's message to the state
            setMessages((messages) => [...messages, sentMessage]);
            setSentText('');  // Clear the input field
            setTyping('Typing...');  // Show typing indicator

            // Get CSRF token
            const csrfToken = Cookies.get('XSRF-TOKEN');

            // Send the message to the backend and start reading the stream
            const response = await fetch(`/conversation/${conversation.id}?${params}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken,
                },
                body: JSON.stringify(sentMessage),
            });

            // Ensure the response is a stream
            const reader = response.body.getReader();
            const decoder = new TextDecoder('utf-8');
            let aiResponseText = '';  // Initialize an empty string to accumulate the chunks
            let buffer = '';  // Buffer to accumulate incomplete chunks

            const aiMessage = {
                id: nextId + 1, // New ID for the AI message
                messageText: '',  // Will be progressively updated
                senderProfile: selectedprofile.username,
            };

            // Read the stream chunk by chunk
            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                // Decode the chunk and clean it up
                let chunk = decoder.decode(value, { stream: true });

                // Remove the 'data:' prefix if it exists
                if (chunk.startsWith('data:')) {
                    chunk = chunk.substring(5);  // Remove the 'data:' prefix
                }

                // Add the chunk to the buffer
                buffer += chunk;

                // Try to parse the buffer as JSON
                try {
                    // Try parsing the buffer as JSON
                    const jsonData = JSON.parse(buffer);

                    // If successful, update the AI response
                    aiResponseText = jsonData.messageText;
                    aiMessage.messageText = aiResponseText;

                    // Update the messages with the new part of the AI response
                    setMessages((prevMessages) => {
                        const updatedMessages = [...prevMessages];
                        const aiIndex = updatedMessages.findIndex((msg) => msg.id === aiMessage.id);

                        if (aiIndex >= 0) {
                            updatedMessages[aiIndex] = { ...aiMessage };  // Update existing message
                        } else {
                            updatedMessages.push(aiMessage);  // Add the AI message if it's new
                        }

                        return updatedMessages;
                    });

                    // Clear the buffer after successful JSON parse
                    buffer = '';
                } catch (error) {
                    // If parsing fails, continue accumulating data
                    console.log('Buffer not yet complete, waiting for more data...');
                }
            }

            // Once the stream is finished, hide the typing indicator
            setTyping('');
        } catch (error) {
            console.error('Error sending message:', error);
            setError(error.message || 'Failed to send message.');
            setTyping('');
        }
    };


    const loadSelectedProfileChat = async () => {
        try {
            const params = {
                profile: selectedprofile.username
            };
            const conversation = await axios.get(`/conversation/fetch`, { params });
            setMessages(conversation.data.messages);
            setConversation(conversation.data);
            setError(null);
        }
        catch (e) {
            if (e.response) {

            }
        }
    }

    useEffect(() => {
        loadSelectedProfileChat();
    }, []);

    // Scroll to bottom when messages update
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    return (
        <React.Fragment>
            <Box sx={{ width: "80%", display: 'flex', flexDirection: 'column', mt: 1 }}>
                <Card sx={{ border: 'none', boxShadow: 'none', display: 'flex', alignItems: 'center' }}>
                    {selectedprofile.imageUrls && (<Avatar src={`/profiles/image/${selectedprofile.id}`} sx={{ mr: 1 }} />)}
                    {!selectedprofile.imageUrls && (<Avatar alt={selectedprofile.firstName} src='random' sx={{ mr: 1 }} />)}
                    <CardHeader title={`${selectedprofile.firstName} ${selectedprofile.lastName}`} />
                </Card>
                <Paper square sx={{
                    display: 'flex',
                    flexDirection: 'column', mt: 1,
                    overflowY: 'auto',
                    maxHeight: '50vh',
                    border: 'none', boxShadow: 'none',
                    scrollBehavior: 'smooth',
                    '&::-webkit-scrollbar': {
                        width: '8px',
                    },
                    '&::-webkit-scrollbar-thumb': {
                        backgroundColor: theme => theme.palette.primary.main,
                        borderRadius: '5px',
                    },
                }}>
                    {((messages && messages.length > 0) &&
                        <List sx={{ border: 'none', boxShadow: 'none', m: .5 }}>
                            {messages.map(({ id, messageText, senderProfile }) => (
                                <React.Fragment key={id}>
                                    <Box
                                        sx={{
                                            display: 'flex',
                                            justifyContent: senderProfile === selectedprofile.username ? 'flex-start' : 'flex-end',
                                            mb: 1,
                                        }}>
                                        <Card sx={{ mb: 1, borderRadius: '16px', boxShadow: 4, maxWidth: "75%" }}>
                                            <ListItem>
                                                <ListItemText
                                                    secondary={messageText}
                                                />
                                            </ListItem>
                                        </Card>
                                    </Box>
                                </React.Fragment>
                            ))}
                            {/* Reference for scrolling */}
                            <div ref={messagesEndRef} />
                        </List>)}
                    {typing && (
                        <Box
                            sx={{
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center',
                                p: 1,
                            }}
                        >
                            <CircularProgress size={24} />
                        </Box>
                    )}
                </Paper>
                <Box sx={{ display: 'flex', alignItems: 'center', mt: 4, mb: 4 }}>
                    <TextField
                        fullWidth
                        variant="outlined"
                        placeholder="Type a message..."
                        value={sentText}
                        onChange={(e) => setSentText(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                    />
                    <IconButton color="primary" onClick={handleSend} sx={{ ml: 2 }}>
                        <SendIcon />
                    </IconButton>
                </Box>
            </Box>
        </React.Fragment>
    );
}
