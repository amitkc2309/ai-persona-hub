# AI-Persona-Hub

AI-Persona-Hub is a Spring framework based AI project that enables users to create artificial profiles/persona, add them as friends, and 
engage in conversations with them. The entire application is designed to run locally, eliminating the need for any paid 
AI services like ChatGPT. It utilizes Spring-AI to connect with a locally hosted Ollama model.

The code is developed using Java with Spring Boot, Spring AI, Spring cloud, Spring Security with OAuth2, Spring WebFlux, Reactive programming, Hibernate and React js. It is based on Microservices Architecture and deployed on Docker.

![Profile](samples/Generated-Profile.jpg)
![chat](samples/Chat.jpg)
![create-profile](samples/create.jpg)
![Friends](samples/Friends-List.jpg)

## Features:

- **AI-Driven Conversations**: Users can chat with AI profiles, with all conversations being stored in the Mongo database. 
The application uses locally hosted open-source Ollama to power these AI interactions.

- **AI Profile Management**: Users can create and store AI-generated profiles.

- **AI Image Creation**: Users can generate and save AI-generated images for profile picture, which runs locally as well.

- **Scalable Microservices Architecture**: The project is designed with a microservices architecture to ensure 
scalability and easy maintenance.

- **Robust Security**: Users can securely register, log in, and log out with their credentials. The application employs
  Spring Security and Keycloak to implement the OAuth2 Authorization Code flow.

- **User-Friendly Interface**: The application features an intuitive front end, developed using React.

- **Containerized**: Docker-compose file has been provided to quickly start the application.

## Setup and Running on Local
- Download [Ollama](https://ollama.com/) for your OS. On the same page, go to Models and download and install **llama3.1** model. We need this so user can chat with AI/bots and to generrate AI profiles. Remember to download model which has *Tools* support. Start Ollama on local `ollama run ollama3.1:latest`.
- *Optional Step for Image generation:* To generate AI profile images we need a image generation model. We have used [stable-diffusion-webui](https://github.com/AUTOMATIC1111/stable-diffusion-webui) for the same. Follow the instruction given on the github page to start stable-diffusion on local. It is recommended to run it on system having NVIDIA GPU.  
  We call stable-diffusion's API to generate images from our spring code, so make sure that you start stable diffusion with `--api` command line argument. For windows OS, you can set `COMMANDLINE_ARGS=--api` in `webui-user.bat` file to achieve this.
**Note**: You may choose to skip setup of stable-diffusion and the application will still start and run normally but profile images will not be generated.
- Download and start Docker on your system.
- You need to generate docker images for all spring-boot projects. So, go inside all three folders of `ai-persona-hub/ai-persona-hub-backend` and run `mvn compile jib:dockerBuild` command on each of them.
- Go indside `ai-persona-hub/ai-persona-hub-frontend` and run `docker build --no-cache -t amitking2309/ai-persona-frontend .` command.
- Go to `ai-persona-hub/ai-persona-hub-docker/` and create an .env file and provide the IP address of your local system in environment variable `MACHINE_URL`, like `MACHINE_URL=111.111.11.11`
- Go to `ai-persona-hub/ai-persona-hub-docker/` and run command `docker compose -f docker-compose-infra.yaml up -d`. Wait for keycloak, Mongo and Redis containers to start.
- Run command `docker compose up -d`. This will start the application in up to 2 minutes.
- Application will be available on `http://<Your_System's_IPAddress>:8080/`
- Register a new User then login
- Now go to `Generate AI Friend` Menu and create a new AI Profile of your choice.
- You can create as many AI profiles as you want. These profiles will be visible on Home Tab. You can add a profile as favourite and it will be visible to you in `AI-Chat` tab.
- You can chat with any AI Profile by clicking on chat widget button.

## ⚠️ Disclaimer

This project is for **educational and learning purposes only**. All configurations and setups are designed for **demo environments**.  
The maintainers of this project are **not responsible** for any loss, damage, or misuse resulting from using this codebase. **Use at your own risk**.
