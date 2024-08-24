# use this command to create image-> docker build --no-cache -t amitking2309/ai-persona-frontend .
# Using Docker multistage build
# Stage 1
FROM node:17-alpine as builder
WORKDIR /app
COPY . .
RUN npm install --force
RUN npm run build

# Stage 2
FROM nginx:1.21.0-alpine as production
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]