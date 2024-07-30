  import { Box } from '@mui/material';
  import { useState } from 'react';
  import ProfileCard from "./Components/ProfileCard";
  import AppBarTop from "./Components/AppBarTop";


  function App() {
    const [count, setCount] = useState(0)
    return (
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          position: 'relative',
          mx: 4,
        }}>
        <AppBarTop/>
        <ProfileCard />
      </Box>
    )
  }

  export default App