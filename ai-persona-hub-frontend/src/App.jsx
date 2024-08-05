import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { Box, CssBaseline, ThemeProvider } from '@mui/material';
import { useState } from 'react';
import RandomProfile from "./Components/RandomProfile"
import GenerateAIProfile from "./Components/GenerateAIProfile"
import FriendList from "./Components/FriendList"
import Chat from "./Components/Chat"
import theme from "./Components/Theme"
import AppBarTop from "./Components/AppBarTop"


function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          position: 'relative',
          mx: 4,
        }}>
        <Router>
        <AppBarTop/>
          <Routes>
            <Route path="/" element={<RandomProfile />} />
            <Route path="/generate-ai-profile" element={<GenerateAIProfile />} />
            <Route path="/chat" element={<Chat />} />
            <Route path="/friend-list" element={<FriendList />} />
          </Routes>
        </Router>
      </Box>
    </ThemeProvider>
  )
}

export default App