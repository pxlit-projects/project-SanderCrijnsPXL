# Fullstack Java Project

## Sander Crijns (3AONC)

## Folder structure

- Readme.md
- _architecture_: this folder contains documentation regarding the architecture of your system.
- `docker-compose.yml` : to start the backend (starts all microservices)
- _backend-java_: contains microservices written in java
- _demo-artifacts_: contains images, files, etc that are useful for demo purposes.
- _frontend-web_: contains the Angular webclient

Each folder contains its own specific `.gitignore` file.  
**:warning: complete these files asap, so you don't litter your repository with binary build artifacts!**

## How to setup and run this application

### Start frontend + databases
Run the following commands in the location of the angular project
```
npm install
ng build
```
Run the following command in the root of the project
```
docker compose up
```

### Start backend
Start the microservices
Do it in the folowing order:
1) ConfigService
2) DiscoveryService
3) PostService, ReviewService, CommentService, Gateway
   
![image](https://github.com/user-attachments/assets/f01a709b-f4e2-42b8-9227-4b1c1f3ebbc8)
