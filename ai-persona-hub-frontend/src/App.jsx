import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { Box } from '@mui/material';
import { useState } from 'react';
import RandomProfile from "./Components/RandomProfile"
import GenerateAIProfile from "./Components/GenerateAIProfile"
import FriendList from "./Components/FriendList"
import Chat from "./Components/Chat"


function App() {
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
      <Router>
        <Routes>
          <Route path="/" element={<RandomProfile/>} />
          <Route path="/generate-ai-profile" element={<GenerateAIProfile/>} />
          <Route path="/friend-list" element={<FriendList/>} />
          <Route path="/chat" element={<Chat/>} />
        </Routes>
      </Router>
    </Box>
  )
}

export default App