import React from 'react';

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

export default function RandomProfile() {
  const [expanded, setExpanded] = React.useState(false);
  return (
    <>
      <AppBarTop/>
      <Box sx={{ maxWidth: 512 }}>
        <Box sx={{ maxWidth: 512, mt: 1, display: 'flex', justifyContent: 'space-between' }}>
          <IconButton aria-label="add to favorites">
            <FavoriteIcon />
          </IconButton>
          <IconButton aria-label="share">
            <Chat />
          </IconButton>
          <IconButton aria-label="share">
            <SkipNext />
          </IconButton>
        </Box>
        <Card sx={{ maxWidth: 512, mt: 1 }}>
          <CardMedia
            component="img"
            image="http://localhost:8080/profiles/image/66a92ceb02f1af5579e7e095"
            alt="Image not available"
          />
          <CardHeader title="Foo Bar" subheader="age" />
          <CardContent>
            <Typography variant="body2" color="text.secondary">
              This impressive paella is a perfect party dish and a fun meal to cook
              together with your guests. Add 1 cup of frozen peas along with the mussels,
              if you like.
            </Typography>
          </CardContent>
        </Card>
      </Box>
    </>
  );
}