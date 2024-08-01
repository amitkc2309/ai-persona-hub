import React from 'react';
import { Avatar, Box, Card, CardContent, CardHeader, CardMedia, Typography, useTheme, useMediaQuery } from '@mui/material';


export default function Profile() {
    const theme = useTheme();
    const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));

    const sampleUser = {
        profileImage: 'https://via.placeholder.com/100',
        coverImage: 'https://via.placeholder.com/600x300',
        firstName: 'John',
        lastName: 'Doe',
        age: 30,
        bio: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam.',
        email: 'john.doe@example.com',
        phone: '+1234567890'
    };

    const UserProfile = ({ user }) => {
        const theme = useTheme();
        const isSmallScreen = useMediaQuery(theme.breakpoints.down('sm'));
        return (
            <>
                {/* <Box
                    sx={{
                        display: 'flex',
                        flexDirection: isSmallScreen ? 'column' : 'row',
                        alignItems: 'center',
                        justifyContent: 'center',
                        p: 2,
                        minHeight: '100vh',
                        bgcolor: theme.palette.background.paper
                    }}
                >
                    <Card sx={{ maxWidth: isSmallScreen ? '100%' : 600 }}>
                        <CardHeader
                            avatar={<Avatar src={user.profileImage} alt={`${user.firstName} ${user.lastName}`} sx={{ width: 100, height: 100 }} />}
                            title={`${user.firstName} ${user.lastName}`}
                            subheader={`Age: ${user.age}`}
                            sx={{ textAlign: 'center' }}
                        />
                        <CardMedia
                            component="img"
                            image={user.coverImage}
                            alt="Cover Image"
                            sx={{
                                height: isSmallScreen ? 200 : 300,
                                objectFit: 'cover'
                            }}
                        />
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Bio
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                {user.bio}
                            </Typography>
                            <Typography variant="h6" gutterBottom sx={{ mt: 2 }}>
                                Contact Information
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                <strong>Email:</strong> {user.email}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                <strong>Phone:</strong> {user.phone}
                            </Typography>
                        </CardContent>
                    </Card>
                </Box> */}
            </>
        );
    }
}