#include <sys/types.h>
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <string.h>
#include <termios.h>
#include <unistd.h>

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <pthread.h>

void *serial_conn(void *dummy);
int *start_server(void *port);

char dataFromArduino[1024] = "Mock data from Arduino.";
char dataFromJava[1024] = "None.";

int main(int argc, char **argv) {
	pthread_t serial_t;
	pthread_t socket_t;
	int port = 19108;
	pthread_create(&serial_t, NULL, &serial_conn, NULL);
	pthread_create(&socket_t, NULL, &start_server, &port);
	pthread_join(serial_t, NULL);
	pthread_join(socket_t, NULL);

}

void *serial_conn(void *dummy) {

	// first, open the connection
	int fd = open("/dev/ttyACM0", O_RDWR);
	if (fd == -1) {
		printf("Failed open device.\n");
		return;
	}

	// then configure it
	struct termios options;
	tcgetattr(fd, &options);
	cfsetispeed(&options, 9600);
	cfsetospeed(&options, 9600);
	tcsetattr(fd, TCSANOW, &options);
	// read data
	while(1) {
		write(fd, dataFromJava, strlen(dataFromJava));
		int chars_read = read(fd, dataFromArduino, 1023);
		dataFromArduino[chars_read] = '\0';
		printf("Arduino: %s\n", dataFromArduino);
		sleep(3);
	}

	close(fd);
}


int *start_server(void *port)
{
      int PORT_NUMBER = *(int *) port;
      // structs to represent the server and client
      struct sockaddr_in server_addr,client_addr;    
      
      int sock; // socket descriptor

      // 1. socket: creates a socket descriptor that you later use to make other system calls
      if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
	perror("Socket");
	exit(1);
      }
      int temp;
      if (setsockopt(sock,SOL_SOCKET,SO_REUSEADDR,&temp,sizeof(int)) == -1) {
	perror("Setsockopt");
	exit(1);
      }

      // configure the server
      server_addr.sin_port = htons(PORT_NUMBER); // specify port number
      server_addr.sin_family = AF_INET;         
      server_addr.sin_addr.s_addr = INADDR_ANY; 
      bzero(&(server_addr.sin_zero),8); 
      
      // 2. bind: use the socket and associate it with the port number
      if (bind(sock, (struct sockaddr *)&server_addr, sizeof(struct sockaddr)) == -1) {
	perror("Unable to bind");
	exit(1);
      }
      
      // 3. listen: indicates that we want to listn to the port to which we bound; second arg is number of allowed connections
      if (listen(sock, 5) == -1) {
	perror("Listen");
	exit(1);
      }
          
      printf("\nServer waiting for connection on port %d\n", PORT_NUMBER);
      fflush(stdout);
     

      // 4. accept: wait until we get a connection on that port
      int sin_size = sizeof(struct sockaddr_in);

      while (1) {
	      int fd = accept(sock, (struct sockaddr *)&client_addr,(socklen_t *)&sin_size);
	      printf("Server got a connection from (%s, %d)\n", inet_ntoa(client_addr.sin_addr),ntohs(client_addr.sin_port));
	      
	      char instr[20];
	      char *INS_DATA = "GPSDATA";
	      char *INS_MSG = "MESSAGE";
	      int bytes_received = recv(fd, instr, 20, 0);	      
	      instr[bytes_received] = '\0';
	      printf("Instruction: %s\n", instr);
              if (strcmp(instr, INS_DATA) == 0) {
			printf("Received command for sending data.\n");
		      	send(fd, dataFromArduino, strlen(dataFromArduino), 0);
			
	      } else if (strcmp(instr, INS_MSG) == 0) {
			printf("Received command for receiving message.\n");
			bytes_received = recv(fd, dataFromJava, 1024, 0);
			dataFromJava[bytes_received] = '\0';
			printf("Server received message: %s\n", dataFromJava);
	      }

	      close(fd);
	      
	      printf("Server closed connection\n");
       }
       return 0;
} 
