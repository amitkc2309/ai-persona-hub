import { Avatar, Box, Button, Card, CardHeader, TextField, Typography } from "@mui/material";
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import AppBarTop from "./AppBarTop"

export default function GenerateAIProfile() {
    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);
        console.log({
            email: data.get('email'),
            password: data.get('password'),
        });
    };

    return (
        <>
            <AppBarTop/>
            <Box sx={{ maxWidth: 512, display: 'flex', flexDirection: 'column', alignItems: 'center', }}>
                <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                    <AutoAwesomeIcon />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Generate Random AI Friends
                </Typography>
                <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                    <TextField label="Gender" name="gender" fullWidth margin="normal"
                        type="text" autoFocus
                    />
                    <TextField label="Age" name="age" fullWidth margin="normal"
                        type="number"
                    />
                    <TextField label="Ethnicity" name="ethnicity" fullWidth margin="normal"
                        type="text"
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        Create a Friend
                    </Button>

                </Box>
            </Box>
        </>
    );
}