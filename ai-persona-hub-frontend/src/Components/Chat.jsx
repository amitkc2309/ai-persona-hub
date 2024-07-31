import React, { useState } from 'react';
import { useLocation } from "react-router-dom";
import { AppBar, Toolbar, Typography, IconButton, Paper, List, ListItem, ListItemAvatar, Avatar, ListItemText, TextField, Button, Divider } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import ChatIcon from '@mui/icons-material/Chat';
import AppBarTop from "./AppBarTop";
import { Box } from "@mui/material";

export default function Chat() {
    const location = useLocation();
    const data = location.state?.data;
    const sampleMessages = [
        { id: 1, sender: 'John', message: 'Hey there!' },
        { id: 2, sender: 'You', message: 'Hello!' },
        { id: 4, sender: 'John', message: 'How are you?' },
        { id: 5, sender: 'John', message: 'How are you?' },
        { id: 6, sender: 'John', message: 'How are you?' },
        { id: 7, sender: 'John', message: 'How are you?' },
        { id: 8, sender: 'John', message: 'How are you?' },
        { id: 9, sender: 'John', message: 'How are you?' },
        { id: 10, sender: 'John', message: 'How are you?' },
    ];
    const [messages, setMessages] = useState(sampleMessages);
    const [newMessage, setNewMessage] = useState('');
    const handleSend = () => {
        if (newMessage.trim()) {
            setMessages([...messages, { id: messages.length + 1, sender: 'You', message: newMessage }]);
            setNewMessage('');
        }
    };

    return (
        <React.Fragment>
            <AppBarTop />
            <Box sx={{ maxWidth: 1000, display: 'flex', flexDirection: 'column' }}>
                <Paper square sx={{
                    display: 'flex',
                    flexDirection: 'column', mt: 1, overflowY: 'auto', maxHeight: '70vh',
                    scrollBehavior: 'smooth',
                    '&::-webkit-scrollbar': {
                        width: '8px',
                    },
                    '&::-webkit-scrollbar-thumb': {
                        backgroundColor: '#1ABC9C',
                        borderRadius: '5px',
                    },
                }}>
                    <List>
                        {messages.map(({ id, sender, message }) => (
                            <React.Fragment key={id}>
                                <ListItem>
                                    <ListItemAvatar>
                                        <Avatar>{sender.charAt(0)}</Avatar>
                                    </ListItemAvatar>
                                    <ListItemText
                                        primary={sender}
                                        secondary={message}
                                    />
                                </ListItem>
                                {/* <Divider /> */}
                            </React.Fragment>
                        ))}
                    </List>
                </Paper>
                <div style={{ display: 'flex', padding: '16px' }}>
                    <TextField
                        fullWidth
                        variant="outlined"
                        placeholder="Type a message..."
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                    />
                    <IconButton color="primary" onClick={handleSend} style={{ marginLeft: '8px' }}>
                        <SendIcon />
                    </IconButton>
                </div>
                {/* <Box sx={{ maxWidth: 512, display: 'flex', flexDirection: 'column' }}>
                    {data}
                </Box> */}
            </Box>
        </React.Fragment>
    );
}
