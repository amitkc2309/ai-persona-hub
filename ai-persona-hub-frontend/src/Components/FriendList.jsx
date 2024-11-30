import React, {useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemText from '@mui/material/ListItemText';
import Avatar from '@mui/material/Avatar';
import {Card, CardActions} from '@mui/material';
import {Chat} from '@mui/icons-material';
import SkipNextIcon from '@mui/icons-material/SkipNext';
import SkipPreviousIcon from '@mui/icons-material/SkipPrevious';
import {useNavigate} from 'react-router-dom';
import axios from 'axios';
import Typography from "@mui/material/Typography";

export default function FriendList() {
    const navigate = useNavigate();
    const [friends, setFriends] = useState(null);
    const [error, setError] = useState(null);
    const [pagination, setPagination] = useState({ currentPage: 0, totalPages: 1 });

    const getAIFriends = async (page=0) => {
        try {
            var response = await axios.
                get(`/profiles/matched-bots?page=${page}&size=3`);
            const { profiles, currentPage, totalPages } = response.data;
            //const friendsData = response.data.profiles;//.map(friend => friend.body);
            console.log("friendsData="+profiles);
            setFriends(profiles);
            setPagination({ currentPage, totalPages });
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
        getAIFriends(0);
    }, []);

    const goToChat = (id) => {
        const friend = friends.find(f => f.id === id);
        navigate('/chat', { state: { selectedprofile: friend } });
    };

    const goToProfile = (id) => {
        const friend = friends.find(f => f.id === id);
        navigate('/', { state: { viewprofile: friend } });
    };

    const goToNextPage = () => {
        console.log("goToNextPage currentPage:"+pagination.currentPage);
        console.log("goToNextPage totalPages:"+pagination.totalPages);
        if (pagination.currentPage < pagination.totalPages - 1) {
            getAIFriends(pagination.currentPage + 1);
        }
    };

    const goToPreviousPage = () => {
        console.log("goToPreviousPage currentPage:"+pagination.currentPage);
        if (pagination.currentPage > 0) {
            getAIFriends(pagination.currentPage - 1);
        }
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
                                        onClick={()=>goToProfile(id)}>
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
                    {/* Pagination controls */}
                    <Box sx={{mt: 1, display: 'flex', justifyContent: 'space-between'}}>
                        <IconButton disabled={pagination.currentPage === 0}
                                    aria-label="previous" sx={{color: '#007bff'}} onClick={goToPreviousPage}>
                            <SkipPreviousIcon/>
                            <Typography variant="subtitle2">
                                Previous
                            </Typography>
                        </IconButton>
                        <IconButton disabled={pagination.currentPage === pagination.totalPages - 1}
                            aria-label="next" sx={{color: '#007bff'}} onClick={goToNextPage}>
                            <Typography variant="subtitle2">
                                Next
                            </Typography>
                            <SkipNextIcon/>
                        </IconButton>
                    </Box>
                </Paper>
            </Box>
        </React.Fragment>
    );
}