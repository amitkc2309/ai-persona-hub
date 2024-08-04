import React, { useState, useEffect, useRef } from 'react';

import { Chat, SkipNext } from '@mui/icons-material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import CardMedia from '@mui/material/CardMedia';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { Box, CircularProgress, LinearProgress } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import config from "../config.json"
import axios from 'axios';
import Cookies from 'js-cookie';

export default function RandomProfile() {
  const navigate = useNavigate();
  const [randomProfile, setRandomProfile] = useState(null);
  const [error, setError] = useState(null);
  const [matched, setMatched] = useState(false);
  const [loading, setLoading] = useState(false);

  const goToChat = () => {
    navigate('/chat', { state: { data: 'example data' } });
  };

  const handlegetRandomProfile = async () => {
    setRandomProfile(null);
    setLoading(true);
    try {
      var response = await axios.
        get(`/profiles/random`);
      setRandomProfile(response.data);
      setError(null);
    }
    catch (e) {
      if (e.response) {
        if (e.response.status === 404) {
          setRandomProfile(null);
          setError("No AI Profile Found for you. Go create some AI friends in Menu Options.");
        } else {
          setRandomProfile(null);
          setError(`Error: ${e.response.status} - ${e.response.statusText}`);
        }
      }
      else if (e.request) {
        // Handle errors with the request itself
        setRandomProfile(null);
        setError("No response received from server.");
      }
    }
    finally {
      setLoading(false);
    }
  };

  const addMatchedProfile = async (id) => {
    try {
        var updatedUser = await axios.put(`/profiles/match/${id}`);
        setMatched(true);
        setError(null);
    }
    catch (e) {
        if (e.response) {
            setMatched(false);
            setError("Error adding as a friend");
           
        }
    }
    finally {
    }
};

  useEffect(() => {
    handlegetRandomProfile();
  }, []);

  return (
    <>
      {randomProfile && (<Box sx={{ width: "80%", maxWidth: 512 }}>
        <Box sx={{ mt: 1, display: 'flex', justifyContent: 'space-between' }}>
          {!randomProfile.isMatched && (<IconButton aria-label="add to favorites" onClick={() => addMatchedProfile(randomProfile.id)} sx={{ color: 'red' }}>
            <FavoriteIcon />
          </IconButton>)}
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
          {randomProfile.imageUrls && (<CardMedia
            component="img"
            alt="Image not available"
            image={`/profiles/image/${randomProfile.id}`}
          />)}
          <CardHeader title={`${randomProfile.firstName} ${randomProfile.lastName}`} subheader={randomProfile.age} />
          <CardContent>
            <Typography variant="body2" color="text.secondary">
              {randomProfile.bio}
            </Typography>
          </CardContent>
        </Card>
      </Box>)}
      {loading && <CircularProgress sx={{ color: theme => theme.palette.primary.main, mt:5}} />}
      {error && (<Box sx={{ mt: 1, display: 'flex', justifyContent: 'space-between' }}>
        <Card sx={{
          mt: 1,
        }}>
          <CardContent>
            <Typography variant="body2" color="error">
              {error}
            </Typography>
          </CardContent>
        </Card>
      </Box>)}

      {matched && (<Typography variant="body2" color="text.secondary">
              Profile Added as a Friend
      </Typography>)}
    </>
  );
}