import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import CssBaseline from "@mui/material/CssBaseline"; //Do not remove
import { Box } from '@mui/material';
import { useState } from 'react';
import RandomProfile from "./Components/RandomProfile"
import GenerateAIProfile from "./Components/GenerateAIProfile"


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
        </Routes>
      </Router>
    </Box>
  )
}

export default App