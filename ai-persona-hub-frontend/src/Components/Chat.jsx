import React, { useState, useEffect, useRef } from 'react';
import { useLocation } from "react-router-dom";
import { AppBar, Toolbar, Typography, IconButton, Paper, List, ListItem, ListItemAvatar, Avatar, ListItemText, TextField, Button, Divider, Card, CardHeader, CircularProgress } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import ChatIcon from '@mui/icons-material/Chat';
import AppBarTop from "./AppBarTop";
import { Box } from "@mui/material";
import axios from 'axios';

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
        var responseMessage = '';
        var nextId = 0;
        if (messages && messages.length > 0) {
            var lastMessage = messages[messages.length - 1];
            nextId = Number(lastMessage.id) + 1;
        }
        else {
            nextId = 1;
        }
        try {
            const params = {
                profile: selectedprofile.username
            };
            const sentMessage = {
                id: nextId,
                messageText: sentText,
            };
            setMessages(messages => [...messages, sentMessage]);
            setSentText('');
            setTyping("Typing....")
            responseMessage = await axios.put(`/conversation/${conversation.id}`, sentMessage, { params });
            setMessages(messages => [...messages, responseMessage.data]);
            setTyping('');
            setError(null);
        }
        catch (e) {
            if (e.response) {
                setError(e.response);
            }
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
