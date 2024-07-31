import { Avatar, Box, Button, Card, CardContent, CardHeader, InputLabel, MenuItem, Select, TextField, Typography } from "@mui/material";
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import AppBarTop from "./AppBarTop"
import { useState } from "react";

export default function GenerateAIProfile() {
    const [error, setError] = useState('');
    const [isButtonDisabled, setIsButtonDisabled] = useState(false);
    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);
    };

    const handleAgeChange = (event) => {
        const value = event.target.value;
        if (value === '') {
            setError(null);
            setIsButtonDisabled(false);
        }
        else if (value < 18 || value > 100) {
            setIsButtonDisabled(true);
            setError('Age must be between 18 and 100');
        }
        else {
            setError(null);
            setIsButtonDisabled(false);
        }
    }

    return (
        <>
            <Box sx={{ width: 512, display: 'flex', flexDirection: 'column' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', flexDirection: 'column' }}>
                    <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                        <AutoAwesomeIcon />
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        Generate AI Friends
                    </Typography>
                </Box>
                <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1, }} >
                    <InputLabel id="gender-label">Gender</InputLabel>
                    <Select labelId="gender-label" name="gender" fullWidth autoFocus defaultValue="FEMALE">
                        <MenuItem value="FEMALE">Female</MenuItem>
                        <MenuItem value="MALE">Male</MenuItem>
                        <MenuItem value="TRANS">Other</MenuItem>
                    </Select>
                    <TextField label="Age" name="age" fullWidth margin="normal"
                        type="number" inputProps={{ min: 18, max: 100, }} helperText={error} error={!!error} onChange={handleAgeChange}
                    />
                    <InputLabel id="ethnicity-label">Ethnicity</InputLabel>
                    <Select labelId="ethnicity-label" name="ethnicity" fullWidth autoFocus defaultValue="Indian">
                        <MenuItem value="Indian">Indian</MenuItem>
                        <MenuItem value="White">White</MenuItem>
                        <MenuItem value="European">European</MenuItem>
                        <MenuItem value="Asian">Asian</MenuItem>
                        <MenuItem value="Hispanic">Hispanic</MenuItem>
                        <MenuItem value="African">African</MenuItem>
                        <MenuItem value="Alien">Alien</MenuItem>
                    </Select>
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="info"
                        disabled={isButtonDisabled}
                        sx={{ mt: 3, mb: 2, }}
                    >
                        Create a Friend
                    </Button>
                </Box>
                <CardContent>
                    <Typography variant="body2" color="text.secondary">
                        Create AI friends tailored to your preferences. Once generated, they will be added to your friends list,
                        allowing you to chat with them.
                    </Typography>
                </CardContent>
            </Box>
        </>
    );
}