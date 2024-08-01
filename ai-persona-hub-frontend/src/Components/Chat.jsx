import React, { useState,useEffect,useRef } from 'react';
import { useLocation } from "react-router-dom";
import { AppBar, Toolbar, Typography, IconButton, Paper, List, ListItem, ListItemAvatar, Avatar, ListItemText, TextField, Button, Divider, Card, CardHeader } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import ChatIcon from '@mui/icons-material/Chat';
import AppBarTop from "./AppBarTop";
import { Box } from "@mui/material";

export default function Chat() {
    const location = useLocation();
    const data = location.state?.data;
    const messagesEndRef = useRef(null);
    const sampleMessages = [
        { id: 1, sender: 'John', message: '1' },
        { id: 2, sender: 'You', message: '2' },
        { id: 3, sender: 'John', message: '3' },
        { id: 4, sender: 'you', message: 'dhdkashdkjashdajkdasjkdashdjkasdawuiedhmasn dsajldgawdnas bdashjasbndawdkwl;jdkasdjkasndasm,dnwqludbj asndedubjandslds;oljnekd.sm,dbsd ljkdnawwkd' },
        { id: 5, sender: 'John', message: '5' },
        { id: 6, sender: 'you', message: '6' },
        { id: 7, sender: 'John', message: '7' },
        { id: 8, sender: 'you', message: '8' },
        { id: 9, sender: 'John', message: '9' },
    ];
    const [messages, setMessages] = useState(sampleMessages);
    const [newMessage, setNewMessage] = useState('');
    const handleSend = () => {
        if (newMessage.trim()) {
            setMessages([...messages, { id: messages.length + 1, sender: 'You', message: newMessage }]);
            setNewMessage('');
        }
    };
    // Scroll to bottom when messages update
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    return (
        <React.Fragment>
            <Box sx={{ width: "80%", display: 'flex', flexDirection: 'column', mt: 1 }}>
                <Card sx={{ border: 'none', boxShadow: 'none', display: 'flex', alignItems: 'center' }}>
                    <Avatar alt="Profile Picture" src="http://192.168.148.105:8084/66a92ceb02f1af5579e7e095.png" sx={{ mr: 1 }} />
                    <CardHeader title="Foo Bar" />
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
                    <List sx={{ border: 'none', boxShadow: 'none', m: .5 }}>
                        {messages.map(({ id, sender, message }) => (
                            <React.Fragment key={id}>
                                <Box
                                    sx={{
                                        display: 'flex',
                                        justifyContent: id % 2 === 0 ? 'flex-end' : 'flex-start',
                                        mb: 1,
                                    }}>
                                    <Card sx={{ mb: 1, borderRadius: '16px', boxShadow: 4, maxWidth:"75%"}}>
                                        <ListItem>
                                            {/* <ListItemAvatar>
                                            <Avatar>{sender.charAt(0)}</Avatar>
                                        </ListItemAvatar> */}
                                            <ListItemText
                                                // primary={sender}
                                                secondary={message}
                                            />
                                        </ListItem>
                                    </Card>
                                </Box>
                            </React.Fragment>
                        ))}
                        {/* Reference for scrolling */}
                        <div ref={messagesEndRef} />
                    </List>
                </Paper>
                <Box sx={{ display: 'flex', alignItems: 'center', mt: 4, mb: 4}}>
                    <TextField
                        fullWidth
                        variant="outlined"
                        placeholder="Type a message..."
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                    />
                    <IconButton color="primary" onClick={handleSend} sx={{ ml: 2 }}>
                        <SendIcon />
                    </IconButton>
                </Box>
                {/* <Box sx={{ maxWidth: 512, display: 'flex', flexDirection: 'column' }}>
                    {data}
                </Box> */}
            </Box>
        </React.Fragment>
    );
}
