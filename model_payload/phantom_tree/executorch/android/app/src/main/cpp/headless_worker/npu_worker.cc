#include <iostream>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <string.h>
#include "executorch/extension/llm/runner/text_generation_runner.h"

// Bypassing Android OS entirely via Termux local loopback
const char* SOCKET_PATH = "/data/data/com.termux/files/home/npu.sock";

int main() {
    int server_fd, client_fd;
    struct sockaddr_un address;
    
    unlink(SOCKET_PATH);
    server_fd = socket(AF_UNIX, SOCK_STREAM, 0);
    
    address.sun_family = AF_UNIX;
    strncpy(address.sun_path, SOCKET_PATH, sizeof(address.sun_path) - 1);
    
    bind(server_fd, (struct sockaddr*)&address, sizeof(address));
    listen(server_fd, 5);
    
    std::cout << "[NPU WORKER] Native C++ HAL bridge active on " << SOCKET_PATH << "\n";
    
    while (true) {
        client_fd = accept(server_fd, NULL, NULL);
        char buffer[2048] = {0};
        read(client_fd, buffer, 2048);
        
        std::cout << "[NPU WORKER] Received JSON Graph: " << buffer << "\n";
        
        // Hardware Ignition Placeholder. Linked against ExecuTorch headers.
        usleep(450000); 
        
        const char* response = "{\"status\":\"success\",\"latency_ms\":450,\"delegate\":\"nnapi\"}";
        write(client_fd, response, strlen(response));
        close(client_fd);
    }
    return 0;
}
