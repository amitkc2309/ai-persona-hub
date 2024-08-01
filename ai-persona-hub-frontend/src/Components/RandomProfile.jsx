import React, { useState } from 'react';

import { Chat, SkipNext } from '@mui/icons-material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import CardMedia from '@mui/material/CardMedia';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { Box } from '@mui/material';
import AppBarTop from './AppBarTop';
import { useNavigate } from 'react-router-dom';
import config from "../config.json"
import axios from 'axios';

export default function RandomProfile() {
  const [expanded, setExpanded] = React.useState(false);
  const navigate = useNavigate();
  const [createError, setCreateError] = useState(false);
    const [created, setCreated] = useState(false);
    const [isButtonDisabled, setIsButtonDisabled] = useState(false);
  const goToChat = () => {
    navigate('/chat', { state: { data: 'example data' } });
  };
  const handlegetRandomProfile = async (event) => {
    event.preventDefault();
    try {
        const response = await axios.
        get(`${config.BACKEND_URL}/profiles/random`);
        setCreated(true);
        setCreateError(false);
        console.log(response)
    } catch (error) {
        setCreateError(error);
        setCreated(false)
        console.error('Failed to Create AI profile', error);
    }
};
  return (
    <>
      <Box sx={{ width: "80%", maxWidth: 650}}>
        <Box sx={{ mt: 1, display: 'flex', justifyContent: 'space-between' }}>
          <IconButton aria-label="add to favorites" sx={{ color: 'red' }}>
            <FavoriteIcon />
          </IconButton>
          <IconButton aria-label="chat" onClick={goToChat} sx={{ color: theme => theme.palette.primary.main }}>
            <Chat />
          </IconButton>
          <IconButton aria-label="skip" sx={{ color: '#007bff' }} onClick={handlegetRandomProfile}>
            <SkipNext />
          </IconButton>
        </Box>
        <Card sx={{
          mt: 1,
        }}>
          <CardMedia
            component="img"
            image="http://192.168.148.105:8084/66a92ceb02f1af5579e7e095.png"
            alt="Image not available"
          />
          <CardHeader title="Foo Bar" subheader="age" />
          <CardContent>
            <Typography variant="body2" color="text.secondary">
              Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
              eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut
              enim ad minim veniam, quis nostrud exercitation ullamco laboris
              nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in
              reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
              pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa
              qui officia deserunt mollit anim id est laborum.
            </Typography>
          </CardContent>
        </Card>
      </Box>
    </>
  );
}