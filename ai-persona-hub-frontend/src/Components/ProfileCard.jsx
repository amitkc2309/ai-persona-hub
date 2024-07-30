import React from 'react';

import { Chat } from '@mui/icons-material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import CardMedia from '@mui/material/CardMedia';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { Box } from '@mui/material';

export default function ProfileSelector() {
  const [expanded, setExpanded] = React.useState(false);
  const handleExpandClick = () => {
    setExpanded(!expanded);
  };
  return (
    <Box sx={{ maxWidth: 512 }}>
      <Box sx={{ maxWidth: 512, mt: 1, display: 'flex', justifyContent: 'space-between' }}>
        <IconButton aria-label="add to favorites">
          <FavoriteIcon />
        </IconButton>
        <IconButton aria-label="share">
          <Chat />
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
  );
}