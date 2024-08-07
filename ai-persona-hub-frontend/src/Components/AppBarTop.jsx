import { useNavigate } from 'react-router-dom';
import React, { useState, useEffect, useRef } from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import AdbIcon from '@mui/icons-material/Adb';
import axios from 'axios';
import Cookies from 'js-cookie';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

const pages = ['HOME', 'AI-CHAT', 'GENERATE-AI-FRIENDS'];
const settings = ['My Profile', 'Logout'];

function AppBarTop() {
  const [anchorElNav, setAnchorElNav] = React.useState(null);
  const [anchorElUser, setAnchorElUser] = React.useState(null);
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);
  const [csrf, setCsrf] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);


  const handleOpenNavMenu = (event) => {
    setAnchorElNav(event.currentTarget);
  };
  const handleOpenUserMenu = (event) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleNav = (pages) => {
    const pageToRoute = {
      'HOME': '/',
      'GENERATE-AI-FRIENDS': '/generate-ai-profile',
      'AI-CHAT': '/friend-list'
    };
    setAnchorElNav(null);
    navigate(pageToRoute[pages]);
  };

  const handleProfileMenu = async (setting) => {
    if (setting === 'Logout') {
      window.location.href = '/logout';
    //   const csrfToken=Cookies.get('XSRF-TOKEN');
    //   setCsrf(csrfToken);
    //   setOpenDialog(true);
    //   const payload = {
    //     _csrf: csrfToken,
    // };
    // console.log(csrfToken);
    //   try {
    //     var response = await axios.post('/logout', payload, {}
    //     );
    //     console.log(response);
    //   }
    //   catch (e) {
    //     if (e.response) {
    //       console.log(e.response.data);
    //     }
    //   }
    }
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const getUserProfile = async () => {
    try {
      var response = await axios.
        get(`/profiles/user`);
      setUser(response.data);
      setError(null);
    }
    catch (e) {
      if (e.response) {
        setUser(null);
        setError("Error Fetching profile");
      }
    }
  };

  useEffect(() => {
    getUserProfile();
  }, []);

  return (
    <>
      <AppBar position="static" sx={{ backgroundColor: theme => theme.palette.primary.main }}>
        <Container maxWidth="xl">
          <Toolbar disableGutters>
            <AdbIcon sx={{ display: { xs: 'none', md: 'flex' }, mr: 1, color: 'gold' }} />
            <Typography
              variant="(sub)title1"
              noWrap
              component="a"
              href="#app-bar-with-responsive-menu"
              sx={{
                mr: 2,
                display: { xs: 'none', md: 'flex' },
                fontFamily: 'monospace',
                fontWeight: 700,
                letterSpacing: '.3rem',
                color: 'gold',
                textDecoration: 'none',
              }}
            >
              Ai-Persona-Hub
            </Typography>


            <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
              <IconButton
                onClick={handleOpenNavMenu}
                color='inherit'
              >
                <MenuIcon />
              </IconButton>
              <Menu
                id="menu-appbar"
                anchorEl={anchorElNav}
                anchorOrigin={{
                  vertical: 'bottom',
                  horizontal: 'left',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'left',
                }}
                open={Boolean(anchorElNav)}
                onClose={handleCloseNavMenu}
                sx={{
                  display: { xs: 'block', md: 'none' },
                }}
              >
                {pages.map((page) => (
                  <MenuItem key={page}
                    onClick={() => handleNav(page)}
                  >
                    <Typography textAlign="center">{page}</Typography>
                  </MenuItem>
                ))}
              </Menu>
            </Box>

            <AdbIcon sx={{ display: { xs: 'flex', md: 'none' }, mr: 1, color: 'gold' }} />
            <Typography
              variant="subtitle1"
              noWrap
              component="a"
              href="#app-bar-with-responsive-menu"
              sx={{
                mr: 2,
                display: { xs: 'flex', md: 'none' },
                flexGrow: 1,
                fontFamily: 'monospace',
                fontWeight: 700,
                letterSpacing: '.3rem',
                color: 'gold',
                textDecoration: 'none',
              }}
            >
              Ai-Persona-Hub
            </Typography>

            <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
              {pages.map((page) => (
                <Button
                  key={page}
                  onClick={() => handleNav(page)}
                  sx={{ ml: 5, my: 2, color: 'white', display: 'block', }}
                >
                  {page}
                </Button>
              ))}
            </Box>

            {user &&
              (<Box sx={{ flexGrow: 0 }}>
                <Tooltip title="Open settings">
                  <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                    <Avatar alt={user.firstName} src='random' />
                  </IconButton>
                </Tooltip>
                <Menu
                  sx={{ mt: '45px' }}
                  id="menu-appbar"
                  anchorEl={anchorElUser}
                  anchorOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                  }}
                  keepMounted
                  transformOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                  }}
                  open={Boolean(anchorElUser)}
                  onClose={handleCloseUserMenu}
                >
                  {settings.map((setting) => (
                    <MenuItem key={setting} onClick={handleCloseUserMenu}>
                      <Typography textAlign="center" onClick={() => handleProfileMenu(setting)}>{setting}</Typography>
                    </MenuItem>
                  ))}
                </Menu>
              </Box>)}
          </Toolbar>
          
        </Container>
      </AppBar>
    </>
  );
}
export default AppBarTop;