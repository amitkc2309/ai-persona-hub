import React, { useState, useEffect } from 'react';
import { styled } from '@mui/material/styles';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import Fab from '@mui/material/Fab';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemText from '@mui/material/ListItemText';
import ListSubheader from '@mui/material/ListSubheader';
import Avatar from '@mui/material/Avatar';
import MenuIcon from '@mui/icons-material/Menu';
import AddIcon from '@mui/icons-material/Add';
import SearchIcon from '@mui/icons-material/Search';
import MoreIcon from '@mui/icons-material/MoreVert';
import AppBarTop from "./AppBarTop"
import { Card, CardActions, CardContent, CardHeader, Divider, Tooltip } from '@mui/material';
import { Chat } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import config from "../config.json"
import axios from 'axios';

export default function FriendList() {
    const navigate = useNavigate();
    const [friends, setFriends] = useState(null);
    const [error, setError] = useState(null);

    const getAIFriends = async () => {
        try {
            var response = await axios.
                get(`/profiles/matched-bots`);
            const friendsData = response.data.map(friend => friend.body);
            setFriends(friendsData);
            setError(null);
        }
        catch (e) {
            if (e.response) {
                if (e.response.status === 404) {
                    setFriends(null);
                    setError("No AI Profile Found for you. Go create some AI friends in Menu Options.");
                } else {
                    setFriends(null);
                    setError(`Error: ${e.response.status} - ${e.response.statusText}`);
                }
            }
        }
        finally {
        }
    };

    useEffect(() => {
        getAIFriends();
    }, []);

    const goToChat = (id) => {
        const friend = friends.find(f => f.id === id);
        navigate('/chat', { state: { selectedprofile: friend } });
    };

    const goToProfile = () => {
        navigate('/', { state: { data: 'example data' } });
    };

    return (
        <React.Fragment>
            <Box sx={{ width: "80%", maxWidth: 650 }}>
                <Paper square sx={{
                    display: 'flex',
                    flexDirection: 'column', mt: 1,
                    overflowY: 'auto',
                    maxHeight: '80vh',
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
                    {((friends && friends.length>0) && 
                    <List sx={{ border: 'none', boxShadow: 'none', m: .5 }}>
                        {friends.map(({ id, firstName, lastName, age, bio, imageUrls }) => (
                            <React.Fragment key={id}>
                                <Card sx={{ mb: 1, borderRadius: '16px', boxShadow: 4 }}>
                                    <ListItemButton sx={{ position: 'relative', '&:hover .chat-icon': { opacity: 1 } }}
                                        onClick={goToProfile}>
                                        <ListItemAvatar>
                                            {imageUrls && (<Avatar src={`/profiles/image/${id}`} />)}
                                            {!imageUrls && (<Avatar alt={firstName} src="static" />)}
                                        </ListItemAvatar>
                                        <ListItemText primary={`${firstName} ${lastName}`}
                                            secondary={bio.length > 50 ? `${bio.slice(0, 150)} ...` : bio}
                                            sx={{ ml: 2 }} />
                                    </ListItemButton>
                                    <CardActions sx={{justifyContent:'right'}}>
                                        <IconButton aria-label="chat" onClick={()=>goToChat(id)} 
                                        sx={{ color: theme => theme.palette.primary.main,}}>
                                            <Chat />
                                        </IconButton>
                                    </CardActions>
                                </Card>
                            </React.Fragment>
                        ))}
                    </List>)}
                </Paper>
            </Box>
        </React.Fragment>
    );
}