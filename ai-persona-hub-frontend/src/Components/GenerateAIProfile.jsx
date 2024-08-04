import { Avatar, Box, Button, Card, CardContent, CardHeader, CircularProgress, InputLabel, LinearProgress, MenuItem, Select, TextField, Typography } from "@mui/material";
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import { useState } from "react";
import config from "../config.json"
import axios from "axios";

export default function GenerateAIProfile() {
    const [ageError, setAgeError] = useState('');
    const [createError, setCreateError] = useState(false);
    const [created, setCreated] = useState(false);
    const [isButtonDisabled, setIsButtonDisabled] = useState(false);
    const [loading, setLoading] = useState(false);
    const [generatedProfile, setGeneratedProfile] = useState(null);

    const handleCreateAIProfile = async (event) => {
        event.preventDefault();
        setGeneratedProfile(null);
        setCreated(false);
        setLoading(true)
        const data = new FormData(event.currentTarget);
        const payload = {
            gender: data.get('gender'),
            age: data.get('age'),
            ethnicity: data.get('ethnicity'),
        };
        try {
            const response = await axios.
                post(`/profiles/generate-random`, payload);
            setGeneratedProfile(response.data);
            setCreated(true);
            setCreateError(false);
        } catch (e) {
            setGeneratedProfile(null);
            setCreateError(true);
            setCreated(false)
            console.error('Failed to Create AI profile', e);
        }
        finally {
            setLoading(false);
        }
    };

    const handleAgeChange = (event) => {
        const value = event.target.value;
        if (value === '') {
            setAgeError(null);
            setIsButtonDisabled(false);
        }
        else if (value < 18 || value > 100) {
            setIsButtonDisabled(true);
            setAgeError('Age must be between 18 and 100');
        }
        else {
            setAgeError(null);
            setIsButtonDisabled(false);
        }
    }

    return (
        <>
            <Box sx={{ width: "80%", maxWidth: 512, display: 'flex', flexDirection: 'column' }}>
                <Box>
                    <Box sx={{ display: 'flex', alignItems: 'center', flexDirection: 'column' }}>
                        <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                            <AutoAwesomeIcon />
                        </Avatar>
                        <Typography component="h1" variant="h5">
                            Generate AI Friends
                        </Typography>
                    </Box>
                    <Box component="form" onSubmit={handleCreateAIProfile} noValidate sx={{ mt: 1, }} >
                        <InputLabel id="gender-label">Gender</InputLabel>
                        <Select labelId="gender-label" name="gender" fullWidth autoFocus defaultValue="FEMALE">
                            <MenuItem value="FEMALE">Female</MenuItem>
                            <MenuItem value="MALE">Male</MenuItem>
                            <MenuItem value="TRANS">Other</MenuItem>
                        </Select>
                        <TextField label="Age" name="age" fullWidth margin="normal"
                            type="number" inputProps={{ min: 18, max: 100, }} helperText={ageError} error={!!ageError} onChange={handleAgeChange}
                        />
                        <InputLabel id="ethnicity-label">Ethnicity</InputLabel>
                        <Select labelId="ethnicity-label" name="ethnicity" fullWidth autoFocus defaultValue="White">
                            <MenuItem value="White">White</MenuItem>
                            <MenuItem value="Indian">Indian</MenuItem>
                            <MenuItem value="European">European</MenuItem>
                            <MenuItem value="Asian">Asian</MenuItem>
                            <MenuItem value="Hispanic">Hispanic</MenuItem>
                            <MenuItem value="African">African</MenuItem>
                            <MenuItem value="Alien">Alien</MenuItem>
                        </Select>
                        {!loading && (<Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="info"
                            disabled={isButtonDisabled}
                            sx={{ mt: 3, mb: 2, }}
                        >
                            Create a Friend
                        </Button>)}
                    </Box>
                </Box>
                {loading && (
                    <Box>
                        <LinearProgress sx={{ color: theme => theme.palette.primary.main, mt: 3, height: 10, borderRadius: 1 }} />
                        <Typography sx={{ color: theme => theme.palette.primary.main, mt: 1 }}>
                            AI profile creation request has been sent to our systems. It can take upto 3-4 minutes.
                        </Typography>
                    </Box>
                )}
                {createError &&
                    (<Typography color="error">
                        AI Profile Creation failed. Please Try after sometimes...
                    </Typography>)
                }
                {generatedProfile &&
                    (
                        <Card sx={{
                            maxHeight: '20vh',
                            overflowY: 'auto',
                            scrollBehavior: 'smooth',
                            '&::-webkit-scrollbar': {
                                width: '8px',
                            },
                            '&::-webkit-scrollbar-thumb': {
                                backgroundColor: theme => theme.palette.primary.main,
                                borderRadius: '5px',
                            },
                        }}>
                            <CardContent>
                                <Typography variant="body2" sx={{
                                    fontWeight: 'bold',
                                    color: theme => theme.palette.primary.main
                                }}>
                                    {generatedProfile}
                                </Typography>
                                <Typography variant="body2" color="text.secondary" sx={{
                                    fontWeight: 'bold',
                                    mt:1,
                                }}>
                                    Note: We are generating profile image as well which may take a while 
                                    depending on AI molel's computing resources.
                                </Typography>
                            </CardContent>
                        </Card>
                    )}
                <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                    Create AI friends tailored to your preferences. Once generated, they will be added to your friends list,
                    allowing you to chat with them.
                </Typography>
            </Box>
        </>
    );
}